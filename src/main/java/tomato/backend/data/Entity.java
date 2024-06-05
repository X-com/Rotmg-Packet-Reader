package tomato.backend.data;

import assets.AssetMissingException;
import assets.IdToAsset;
import assets.ImageBuffer;
import packets.data.ObjectStatusData;
import packets.data.WorldPosData;
import packets.data.enums.StatType;
import tomato.backend.StasisCheck;
import tomato.gui.character.CharacterStatMaxingGUI;
import tomato.gui.dps.DpsGUI;
import tomato.gui.mydmg.MyDamageGUI;
import tomato.gui.security.ParsePanelGUI;
import tomato.realmshark.RealmCharacter;
import tomato.realmshark.enums.CharacterClass;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Entity implements Serializable {
    private boolean isUser;
    public final Stat stat;
    transient private final TomatoData tomatoData;
    public final int id;
    public int objectType;
    private long creationTime;
    public WorldPosData pos;
    public final ArrayList<ObjectStatusData> statUpdates;
    private final ArrayList<Damage> damageList;
    private final HashMap<Integer, Damage> damagePlayer;
    public final HashMap<Integer, PlayerRemoved> playerDropped;
    private String name;
    private long firstDamageTaken = -1;
    private long lastDamageTaken = -1;
    private int charId;
    public int[] baseStats;
    private boolean isPlayer;
    public long stasisCounter;
    public boolean dammahCountered;

    private final static int ORYX_THE_MAD_GOD = 45363;
    private final static int ORYX_THE_MAD_GOD_GUARD_ANIMATION = -935464302;
    private final static int ORYX_THE_MAD_GOD_GUARD_EXALTED_ANIMATION = -918686683;
    private final static int CHANCELLOR_DAMMAH = 9635;
    private final static int FORGOTTEN_KING = 29039;
    private final static int FORGOTTEN_KING_REFLECTOR_ANIMATION = -123818367;

    public Entity(TomatoData tomatoData, int id, long time) {
        this.tomatoData = tomatoData;
        this.id = id;
        creationTime = time;
        damageList = new ArrayList<>();
        statUpdates = new ArrayList<>();
        stat = new Stat();
        damagePlayer = new HashMap<>();
        playerDropped = new HashMap<>();
    }

    public void entityUpdate(int type, ObjectStatusData status, long time) {
        updateStats(status, time);
        this.objectType = type;
        try {
            if (type != -1) {
                name = IdToAsset.objectName(type);
            }
        } catch (Exception e) {
        }
    }

    // TODO fix time
    public void updateStats(ObjectStatusData status, long time) {
        statUpdates.add(status);
        StasisCheck.checkManaFromStasis(this, status.stats);
        stat.setStats(status.stats);
        pos = status.pos;
        if (status.stats.length > 0) {
            if (isUser) {
                fame(time);
                tomatoData.player.charStat(charId, calculateBaseStats());
                MyDamageGUI.updatePlayer(this);
            } else if (isPlayer) {
                baseStats = calculateBaseStats();
            }
        }
        ParsePanelGUI.update(id, this);
    }

    public int maxHp() {
        if (stat.get(StatType.MAX_HP_STAT) == null) return 0;
        return stat.get(StatType.MAX_HP_STAT).statValue;
    }

    public int hp() {
        if (stat.get(StatType.HP_STAT) == null) return 0;
        return stat.get(StatType.HP_STAT).statValue;
    }

    public long getLastDamageTaken() {
        return lastDamageTaken;
    }

    public long getFirstDamageTaken() {
        return firstDamageTaken;
    }

    public String getFightTimerString() {
        long time = lastDamageTaken - firstDamageTaken;
        return DpsGUI.systemTimeToString(time);
    }

    public long getFightTimer() {
        return lastDamageTaken - firstDamageTaken;
    }

    public void entityDropped(long time) {
//        updates.add(status); // TODO fix time
    }

    /**
     * Player entity stats multiplier such as attack, exalts and other buffs.
     *
     * @return damage multiplier from player stats.
     */
    public float playerStatsMultiplier() {
        boolean weak = (stat.get(StatType.CONDITION_STAT).statValue & 0x40) != 0;
        boolean damaging = (stat.get(StatType.CONDITION_STAT).statValue & 0x40000) != 0;
        int attack = stat.get(StatType.ATTACK_STAT).statValue;
        float exaltDmgBonus = (float) stat.get(StatType.EXALTATION_BONUS_DAMAGE).statValue / 1000;

        if (weak) {
            return 0.5f;
        }
        float number = (attack + 25) * 0.02f;
        if (damaging) {
            number *= 1.25;
        }
        return number * exaltDmgBonus;
    }

    public void userProjectileHit(Entity attacker, Projectile projectile, long time) {
        if (projectile == null || projectile.getDamage() == 0) return;

        int[] conditions = new int[2];

        conditions[0] = stat.get(StatType.CONDITION_STAT) == null ? 0 : stat.get(StatType.CONDITION_STAT).statValue;
        conditions[1] = stat.get(StatType.NEW_CON_STAT) == null ? 0 : stat.get(StatType.NEW_CON_STAT).statValue;
        int defence = stat.get(StatType.DEFENSE_STAT) == null ? 0 : stat.get(StatType.DEFENSE_STAT).statValue;

        int dmg = Projectile.damageWithDefense(projectile.getDamage(), projectile.isArmorPiercing(), defence, conditions);

        if (dmg > 0) {
            Damage damage = new Damage(attacker, projectile, time, dmg);
            bossPhaseDamage(damage);
            addPlayerDmg(damage);
            if (firstDamageTaken == -1) {
                firstDamageTaken = time;
            }
            lastDamageTaken = time;
        }
    }

    public void genericDamageHit(Entity attacker, Projectile projectile, long time) {
        if (projectile == null || projectile.getDamage() == 0) return;
        Damage damage = new Damage(attacker, projectile, time);
        bossPhaseDamage(damage);
        addPlayerDmg(damage);
    }

    private void addPlayerDmg(Damage damage) {
        damageList.add(damage);
        if (damage.owner != null) {
            int id = damage.owner.id;
            Damage dmg = damagePlayer.computeIfAbsent(id, a -> new Damage(damage.owner));
            dmg.add(damage);
        }
    }

    private void bossPhaseDamage(Damage damage) {
        damage.oryx3GuardDmg = objectType == ORYX_THE_MAD_GOD && stat.get(StatType.ANIMATION_STAT) != null && (stat.get(StatType.ANIMATION_STAT).statValue == ORYX_THE_MAD_GOD_GUARD_ANIMATION || stat.get(StatType.ANIMATION_STAT).statValue == ORYX_THE_MAD_GOD_GUARD_EXALTED_ANIMATION);
        damage.walledGardenReflectors = objectType == FORGOTTEN_KING && stat.get(StatType.ANIMATION_STAT) != null && (stat.get(StatType.ANIMATION_STAT).statValue == FORGOTTEN_KING_REFLECTOR_ANIMATION && tomatoData.floorPlanCrystals() == 12);
        damage.chancellorDammahDmg = objectType == CHANCELLOR_DAMMAH && !dammahCountered;
    }

    public String name() {
        if (CharacterClass.isPlayerCharacter(objectType) && stat.get(StatType.NAME_STAT) != null) {
            return stat.get(StatType.NAME_STAT).stringStatValue.split(",")[0];
        }
        return name;
    }

    public String getStatName() {
        if (stat.get(StatType.NAME_STAT) == null) return null;
        return stat.get(StatType.NAME_STAT).stringStatValue;
    }

    public String getStatGuild() {
        if (stat.get(StatType.GUILD_NAME_STAT) == null) return null;
        return stat.get(StatType.GUILD_NAME_STAT).stringStatValue;
    }

    public boolean isSeasonal() {
        if (stat.get(StatType.SEASONAL) == null) return false;
        return stat.get(StatType.SEASONAL).statValue == 1;
    }

    public boolean isCrucible() {
        if (stat.get(StatType.CRUCIBLE_STAT) == null) return false;
        return !stat.get(StatType.CRUCIBLE_STAT).stringStatValue.isEmpty();
    }

    public ArrayList<Damage> getDamageList() {
        return damageList;
    }

    public List<Damage> getPlayerDamageList() {
        return Arrays.stream(damagePlayer.values().toArray(new Damage[0])).sorted(Comparator.comparingInt(Damage::getDamage).reversed()).collect(Collectors.toList());
    }

    public boolean isUser() {
        return isUser;
    }

    public void isPlayer() {
        isPlayer = true;
        baseStats = calculateBaseStats();
    }

    public void setUser(int charId) {
        isUser = true;
        this.charId = charId;
        baseStats = calculateBaseStats();
    }

    private int[] calculateBaseStats() {
        int[] base = new int[8];

        base[0] = stat.get(StatType.MAX_HP_STAT).statValue - stat.get(StatType.MAX_HP_BOOST_STAT).statValue;
        base[1] = stat.get(StatType.MAX_MP_STAT).statValue - stat.get(StatType.MAX_MP_BOOST_STAT).statValue;
        base[2] = stat.get(StatType.ATTACK_STAT).statValue - stat.get(StatType.ATTACK_BOOST_STAT).statValue;
        base[3] = stat.get(StatType.DEFENSE_STAT).statValue - stat.get(StatType.DEFENSE_BOOST_STAT).statValue;
        base[4] = stat.get(StatType.SPEED_STAT).statValue - stat.get(StatType.SPEED_BOOST_STAT).statValue;
        base[5] = stat.get(StatType.DEXTERITY_STAT).statValue - stat.get(StatType.DEXTERITY_BOOST_STAT).statValue;
        base[6] = stat.get(StatType.VITALITY_STAT).statValue - stat.get(StatType.VITALITY_BOOST_STAT).statValue;
        base[7] = stat.get(StatType.WISDOM_STAT).statValue - stat.get(StatType.WISDOM_BOOST_STAT).statValue;

        return base;
    }

    /**
     * Fame update from experience points.
     *
     * @param time
     */
    private void fame(long time) {
        long exp = Long.parseLong(stat.get(StatType.EXP_STAT).stringStatValue);
        FameTracker.trackFame(charId, exp, time);
        if (tomatoData.charMap != null) {
            long fame = (exp + 40071) / 2000;
            RealmCharacter r = tomatoData.charMap.get(charId);
            if (r != null) {
                r.fame = fame;
            }
        }
    }

    /**
     * Updates player stats when drinking potions.
     *
     * @param charId User character id that is loaded in.
     * @param stats  Current base stats of user character.
     */
    public void charStat(int charId, int[] stats) {
        if (tomatoData.charMap == null) return;
        RealmCharacter r = tomatoData.charMap.get(charId);

        if (r == null) return;

        if (r.hp != stats[0]) {
            r.hp = stats[0];
            CharacterStatMaxingGUI.updateRealmChars();
        } else if (r.mp != stats[1]) {
            r.mp = stats[1];
            CharacterStatMaxingGUI.updateRealmChars();
        } else if (r.atk != stats[2]) {
            r.atk = stats[2];
            CharacterStatMaxingGUI.updateRealmChars();
        } else if (r.def != stats[3]) {
            r.def = stats[3];
            CharacterStatMaxingGUI.updateRealmChars();
        } else if (r.spd != stats[4]) {
            r.spd = stats[4];
            CharacterStatMaxingGUI.updateRealmChars();
        } else if (r.dex != stats[5]) {
            r.dex = stats[5];
            CharacterStatMaxingGUI.updateRealmChars();
        } else if (r.vit != stats[6]) {
            r.vit = stats[6];
            CharacterStatMaxingGUI.updateRealmChars();
        } else if (r.wis != stats[7]) {
            r.wis = stats[7];
            CharacterStatMaxingGUI.updateRealmChars();
        }
    }

    public void addPlayerDrop(int dropId, long time) {
        int hp = hp();
        int max = maxHp();
        String name = name();
        PlayerRemoved pr = new PlayerRemoved(dropId, hp, max, name, time);
        playerDropped.put(dropId, pr);
    }

    public double distSqrd(WorldPosData p) {
        double x = pos.x - p.x;
        double y = pos.y - p.y;
        return x * x + y * y;
    }
}
