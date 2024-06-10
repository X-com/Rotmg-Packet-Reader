package tomato.backend.data;

import assets.AssetMissingException;
import assets.IdToAsset;
import packets.Packet;
import packets.data.ObjectData;
import packets.data.StatData;
import packets.data.WorldPosData;
import packets.data.enums.NotificationEffectType;
import packets.data.enums.StatType;
import packets.incoming.*;
import packets.outgoing.EnemyHitPacket;
import packets.outgoing.PlayerShootPacket;
import tomato.gui.TomatoGUI;
import tomato.gui.character.*;
import tomato.gui.chat.ChatGUI;
import tomato.gui.dps.DpsGUI;
import tomato.gui.keypop.KeypopGUI;
import tomato.gui.mydmg.MyDamageGUI;
import tomato.gui.security.ParsePanelGUI;
import tomato.gui.stats.LootGUI;
import tomato.realmshark.HttpCharListRequest;
import tomato.realmshark.RealmCharacter;
import tomato.realmshark.RealmCharacterStats;
import tomato.realmshark.enums.CharacterClass;
import tomato.realmshark.enums.LootBags;
import util.RNG;

import java.awt.*;
import java.io.IOException;
import java.util.*;

/**
 * Main data class storing all incoming packet data regarding an instance the user is in.
 * Resets the data after leaving the instance.
 */
public class TomatoData {
    private String token;
    public MapInfoPacket map;
    protected int worldPlayerId;
    protected int charId;
    public long time;
    public long timePc;
    private long timePcFirst;
    public Entity player;
    public Entity pet;
    protected final int[][] mapTiles = new int[2048][2048];
    protected final HashMap<Integer, Entity> entityList = new HashMap<>();
    protected final HashMap<Integer, Entity> playerList = new HashMap<>();
    public final HashMap<Integer, Entity> playerListUpdated = new HashMap<>();
    protected final Projectile[] projectiles = new Projectile[512];
    protected RNG rng;
    protected HashSet<Integer> crystalTracker = new HashSet<>();
    private HashMap<Integer, Entity> entityHitList = new HashMap<>();
    public VaultData regularVault = new VaultData();
    public VaultData seasonalVault = new VaultData();
    public boolean vaultDataRecievedSeasonal, vaultDataRecievedRegular, characterDataRecieved;
    public ArrayList<RealmCharacter> chars;
    public HashMap<Integer, RealmCharacter> charMap;
    public ArrayList<DpsData> dpsData = new ArrayList<>();
    protected ArrayList<NotificationPacket> deathNotifications = new ArrayList<>();
    protected final HashMap<Integer, Entity> dropList = new HashMap<>();
    private ArrayList<Packet> dpsPacketLog = new ArrayList<>();
    private boolean petyard;
    private RealmCharacterStats currentCharacterStats;
    private final TreeSet<Integer> lootBags = new TreeSet<>();
    private int lootTickToggle = 0;
    private final ArrayList<Entity>[] lootTickContainer = new ArrayList[]{new ArrayList<>(), new ArrayList<>()};
    private final ArrayList<Entity> killedEntitys = new ArrayList<>();


    /**
     * Sets the current realm.
     *
     * @param map New realm to be set.
     */
    public void setNewRealm(MapInfoPacket map) {
        clear();
        ParsePanelGUI.clear();
        this.map = map;
        rng = new RNG(map.seed);
    }

    /**
     * Sets the current realm users character id.
     *
     * @param objectId ID of the object in the world.
     * @param charId   Current character id loaded.
     * @param str
     */
    public void setUserId(int objectId, int charId, String str) {
        this.worldPlayerId = objectId;
        this.charId = charId;
        updateDungeonStats(charId, str);
    }

    public void webRequest() {
        if (map.displayName.equals("Pet Yard")) {
            petyard = true;
            CharacterPetsGUI.clearPets();
            charListHttpRequest();
        } else if (map.displayName.equals("Daily Quest Room")) {
            charListHttpRequest();
        }
    }

    private void updateDungeonStats(int charId, String str) {
        if (currentCharacterStats == null) {
            currentCharacterStats = new RealmCharacterStats();
        }
        currentCharacterStats.decode(str);

        if (charMap == null) {
            return;
        }
        RealmCharacter r = charMap.get(charId);
        if (r == null || r.charStats == null) {
            return;
        }
        if (!Arrays.equals(currentCharacterStats.dungeonStats, r.charStats.dungeonStats)) {
            r.charStats = new RealmCharacterStats();
            r.charStats.decode(str);
            CharacterStatsGUI.updateRealmChars();
            CharacterCollectionGUI.updateRealmChars();
        }
    }

    /**
     * Gets the dungeion completion stats of the currently played character.
     * Note! only available if the instance have been changed at least ones from starting the app.
     *
     * @return Currently playing character stats.
     */
    public RealmCharacterStats getCurrentDungeonStats() {
        if (currentCharacterStats == null) return null;
        return currentCharacterStats;
    }

    /**
     * Sets the time of the server.
     *
     * @param serverRealTimeMS Server time in milliseconds.
     */
    public void setTime(long serverRealTimeMS) {
        time = serverRealTimeMS;
        timePc = System.currentTimeMillis();
        if (timePcFirst == -1) timePcFirst = timePc;
    }

    /**
     * Main update packet.
     *
     * @param p Update packet
     */
    public void update(UpdatePacket p) {
        for (int i = 0; i < p.tiles.length; i++) {
            mapTiles[p.tiles[i].x][p.tiles[i].y] = p.tiles[i].type;
        }
        for (int i = 0; i < p.newObjects.length; i++) {
            ObjectData object = p.newObjects[i];
            entityUpdate(object);
        }
        for (int i = 0; i < p.drops.length; i++) {
            int dropId = p.drops[i];
            crystalTracker.remove(dropId);
            Entity e = entityList.get(dropId);
            dropList.put(dropId, e);
            if (e != null) {
//                e.entityDropped(timePc);
                if (isPlayerEntity(e.objectType)) {
                    for (Map.Entry<Integer, Entity> dropCheck : entityHitList.entrySet()) {
                        int k = dropCheck.getKey();
                        if (!dropList.containsKey(k)) {
                            dropCheck.getValue().addPlayerDrop(dropId, timePc);
                        }
                    }
                }
            }
            if (entityHitList.containsKey(dropId)) {
                killedEntitys.add(e);
            }

            playerListUpdated.remove(dropId);
            ParsePanelGUI.removePlayer(dropId);
        }
    }

    /**
     * Adds an entity to the entity lists as well as updates objects.
     *
     * @param object Entity object to be added or updated
     */
    private void entityUpdate(ObjectData object) {
        int id = object.status.objectId;
        Entity entity = entityList.computeIfAbsent(id, idd -> new Entity(this, idd, timePc));
        int idType = object.objectType;
        entity.entityUpdate(idType, object.status, timePc);

        if (petyard) {
            addPet(object);
        } else if (isCrystal(idType)) {
            crystalTracker.add(id);
        } else if (isLootBag(idType) && !lootBags.contains(id)) {
            lootBags.add(id);
            lootTickContainer[lootTickToggle].add(entity);
        } else if (isPlayerEntity(idType)) {
            playerList.put(id, entity);
            playerListUpdated.put(id, entity);
            if (id == worldPlayerId) {
                player = entity;
                entity.setUser(charId);
                MyDamageGUI.updatePlayer(player);
            } else {
                entity.isPlayer();
            }
            ParsePanelGUI.addPlayer(id, entity);
        }
    }

    /**
     * Method to connect loot bag drops to mobs that drop them.
     * If an entity drops a tick after loot bag shows up and is
     * close enough it is most likely the entity that dropped the bag.
     */
    private void lootTick() {
        lootTickToggle ^= 1;
        if(!lootTickContainer[lootTickToggle].isEmpty()) {
            for(Entity bag : lootTickContainer[lootTickToggle]) {
                double dist = 0;
                Entity mob = null;
                for (Entity k : killedEntitys) {
                    double d = bag.distSqrd(k.pos);
                    if(mob == null) {
                        dist = d;
                        mob = k;
                    } else if (d < dist) {
                        dist = d;
                        mob = k;
                    }
                }
                LootGUI.update(map, bag, mob, player, timePc);
            }
            lootTickContainer[lootTickToggle].clear();
        }
        if(!killedEntitys.isEmpty()) {
            killedEntitys.clear();
        }
    }

    private void addPet(ObjectData object) {
        CharacterPetsGUI.addPet(object);
    }

    /**
     * Checks if id of shatters king boss crystal type.
     *
     * @param id Entity id.
     * @return True if id matches a crystal.
     */
    private boolean isCrystal(int id) {
        return id == 46721 || id == 46771 || id == 29501 || id == 33656;
    }

    /**
     * Checks if id is a loot drop bag
     *
     * @param id Entity id type.
     * @return True if id is loot bag
     */
    private boolean isLootBag(int id) {
        return LootBags.isLootDropBag(id);
    }

    /**
     * Determines the floor pattern in shatters king fight.
     *
     * @return Gives a mask id indicating the crystal colors in the king fight.
     */
    public int floorPlanCrystals() {
        int mask = 0;
        for (int i : crystalTracker) {
            if (i == 46721) {
                mask |= 1;
            } else if (i == 46771) {
                mask |= 2;
            } else if (i == 29501) {
                mask |= 4;
            } else if (i == 33656) {
                mask |= 8;
            }
        }
        return mask;
    }

    /**
     * Checks if objectType is a player entity.
     *
     * @param objectType ID of the object
     * @return True if ID matches a player entity.
     */
    private boolean isPlayerEntity(int objectType) {
        return CharacterClass.isPlayerCharacter(objectType);
    }

    /**
     * Entity updates and server time from new tick packet.
     *
     * @param p New tick packet.
     */
    public void updateNewTick(NewTickPacket p) {
        setTime(p.serverRealTimeMS);
        for (int i = 0; i < p.status.length; i++) {
            int id = p.status[i].objectId;
            Entity entity = entityList.computeIfAbsent(id, idd -> new Entity(this, idd, timePc));
            entity.updateStats(p.status[i], timePc);
        }
        lootTick();
    }

    /**
     * Creates a new projectile from the outgoing packet.
     *
     * @param p Projectile info.
     */
    public void playerShoot(PlayerShootPacket p) {
        projectiles[p.bulletId] = new Projectile(rng, player, p.weaponId, p.projectileId);
    }

    /**
     * Handles special projectile creations from the outgoing packet.
     *
     * @param p Projectile info
     */
    public void serverPlayerShoot(ServerPlayerShootPacket p) {
        if (p.bulletCount > 1) {
            Projectile projectile = new Projectile(p.damage, p.containerType, p.summonerId);
            for (int j = p.bulletId; j < p.bulletId + p.bulletCount; j++) {
                projectiles[j % 256 + 256] = projectile;
            }
        } else if (p.bulletId > 255 && p.bulletId < 512) {
            Projectile projectile = new Projectile(p.damage, p.containerType, p.summonerId);
            projectiles[p.bulletId] = projectile;
        }
    }

    /**
     * Handles entity's being hit by users projectiles.
     *
     * @param p Info about what entity was hit by what projectile.
     */
    public void enemtyHit(EnemyHitPacket p) {
        Projectile projectile = projectiles[p.bulletId];
        int id = p.targetId;
        Entity target = entityList.computeIfAbsent(id, idd -> new Entity(this, idd, timePc));
        int shooterId = p.shooterID;
        if (projectile.getSummonerId() != 0) {
            shooterId = projectile.getSummonerId();
        }
        Entity attacker = playerList.get(shooterId);
        target.userProjectileHit(attacker, projectile, timePc);
        if (!entityHitList.containsKey(id)) {
            entityHitList.put(id, target);
        }
    }

    /**
     * Info related to damage taken on entity's.
     *
     * @param p Info on entity taking damage, amount and by what player.
     */
    public void damage(DamagePacket p) {
        if (p.damageAmount > 0) {
            Projectile projectile = new Projectile(p.damageAmount);
            int id = p.targetId;
            Entity target = entityList.computeIfAbsent(id, idd -> new Entity(this, idd, timePc));
            Entity attacker = playerList.get(p.objectId);
            target.genericDamageHit(attacker, projectile, timePc);
            if (!entityHitList.containsKey(id) && !CharacterClass.isPlayerCharacter(id)) {
                entityHitList.put(id, target);
            }
        }
    }

    /**
     * Dungeons that should not be logged.
     *
     * @param dungName Map data name of the instance.
     * @return Dungeon that should be logged.
     */
    private static boolean isLoggedDungeon(String dungName) {
        switch (dungName) {
            case "{s.vault}":  // vault
            case "Daily Quest Room": // quest room
            case "Pet Yard": // pet yard
            case "{s.guildhall}": // guild hall
            case "{s.nexus}": // nexus
            case "Grand Bazaar": // bazaar
                return false;
            default:
                return true;
        }
    }

    /**
     * Clears all data as instance is changing.
     */
    public void clear() {
        worldPlayerId = -1;
        charId = -1;
        time = -1;
        if (map != null && isLoggedDungeon(map.displayName)) {
            dpsData.add(new DpsData(map, entityHitList, deathNotifications, dungeonTime(), timePcFirst, dpsPacketLog));
            DpsGUI.updateLabel();
        }
        dpsPacketLog = new ArrayList<>();
        timePc = -1;
        timePcFirst = -1;
        rng = null;
        player = null;
        entityList.clear();
        playerList.clear();
        crystalTracker.clear();
        playerListUpdated.clear();
        dropList.clear();
        lootBags.clear();
        deathNotifications = new ArrayList<>();
        entityHitList = new HashMap<>();
        for (int[] row : mapTiles) {
            Arrays.fill(row, 0);
        }
        for (Projectile p : projectiles) {
            if (p != null) p.clear();
        }
        petyard = false;
    }

    public Entity[] getEntityHitList() {
        return entityHitList.values().toArray(new Entity[0]);
    }

    public void exaltUpdate(ExaltationUpdatePacket p) {
        int[] exalts = RealmCharacter.exalts.get((int) p.objType);
        if (exalts == null) return;
        int[] update = new int[]{p.dexterityProgress, p.speedProgress, p.vitalityProgress, p.wisdomProgress, p.defenseProgress, p.attackProgress, p.manaProgress, p.healthProgress};
        if (!Arrays.equals(exalts, update)) {
            RealmCharacter.exalts.put((int) p.objType, update);
            CharacterExaltGUI.updateExalts();
        }
    }

    public void vaultPacketUpdate(VaultContentPacket p) {
        if (player != null) {
            if (player.stat.get(StatType.SEASONAL).statValue == 1) {
                vaultDataRecievedSeasonal = true;
                seasonalVault.vaultPacketUpdate(p);
            } else {
                vaultDataRecievedRegular = true;
                regularVault.vaultPacketUpdate(p);
            }
            CharacterPanelGUI.vaultDataUpdate();
        }
    }

    public void characterListUpdate(ArrayList<RealmCharacter> chars) {
        characterDataRecieved = true;
        this.chars = chars;
        charMap = new HashMap<>();
        for (RealmCharacter r : chars) {
            charMap.put(r.charId, r);
        }
        seasonalVault.clearChar();
        regularVault.clearChar();
        for (RealmCharacter c : chars) {
            if (c.seasonal) {
                seasonalVault.updateCharInventory(c);
            } else {
                regularVault.updateCharInventory(c);
            }
        }
        RealmCharacter currentChar = charMap.get(charId);
        if (charId != -1 && currentChar != null) {
            makePet(currentChar);
            MyDamageGUI.updatePet(pet);
        }
        CharacterPanelGUI.updateRealmChars();
        LootGUI.updateExaltStats();
    }

    private void makePet(RealmCharacter currentChar) {
        pet = new Entity(this, -1, time);

        pet.stat.set(StatType.SKIN_ID, new StatData());
        pet.stat.set(StatType.PET_TYPE_STAT, new StatData());
        pet.stat.set(StatType.PET_NAME_STAT, new StatData());
        pet.stat.set(StatType.PET_RARITY_STAT, new StatData());
        pet.stat.set(StatType.PET_INSTANCE_ID_STAT, new StatData());
        pet.stat.set(StatType.PET_MAX_ABILITY_POWER_STAT, new StatData());
        pet.stat.set(StatType.PET_FIRST_ABILITY_POINT_STAT, new StatData());
        pet.stat.set(StatType.PET_FIRST_ABILITY_POWER_STAT, new StatData());
        pet.stat.set(StatType.PET_FIRST_ABILITY_TYPE_STAT, new StatData());
        pet.stat.set(StatType.PET_SECOND_ABILITY_POINT_STAT, new StatData());
        pet.stat.set(StatType.PET_SECOND_ABILITY_POWER_STAT, new StatData());
        pet.stat.set(StatType.PET_SECOND_ABILITY_TYPE_STAT, new StatData());
        pet.stat.set(StatType.PET_THIRD_ABILITY_POINT_STAT, new StatData());
        pet.stat.set(StatType.PET_THIRD_ABILITY_POWER_STAT, new StatData());
        pet.stat.set(StatType.PET_THIRD_ABILITY_TYPE_STAT, new StatData());

        pet.stat.get(StatType.SKIN_ID).statValue = currentChar.petSkin;
        pet.stat.get(StatType.PET_TYPE_STAT).statValue = currentChar.petType;
        pet.stat.get(StatType.PET_NAME_STAT).stringStatValue = currentChar.petName;
        pet.stat.get(StatType.PET_RARITY_STAT).statValue = currentChar.petRarity;
        pet.stat.get(StatType.PET_INSTANCE_ID_STAT).statValue = currentChar.petInstanceId;
        pet.stat.get(StatType.PET_MAX_ABILITY_POWER_STAT).statValue = currentChar.petMaxAbilityPower;

        pet.stat.get(StatType.PET_FIRST_ABILITY_POINT_STAT).statValue = currentChar.petAbilitys[0];
        pet.stat.get(StatType.PET_FIRST_ABILITY_POWER_STAT).statValue = currentChar.petAbilitys[1];
        pet.stat.get(StatType.PET_FIRST_ABILITY_TYPE_STAT).statValue = currentChar.petAbilitys[2];
        pet.stat.get(StatType.PET_SECOND_ABILITY_POINT_STAT).statValue = currentChar.petAbilitys[3];
        pet.stat.get(StatType.PET_SECOND_ABILITY_POWER_STAT).statValue = currentChar.petAbilitys[4];
        pet.stat.get(StatType.PET_SECOND_ABILITY_TYPE_STAT).statValue = currentChar.petAbilitys[5];
        pet.stat.get(StatType.PET_THIRD_ABILITY_POINT_STAT).statValue = currentChar.petAbilitys[6];
        pet.stat.get(StatType.PET_THIRD_ABILITY_POWER_STAT).statValue = currentChar.petAbilitys[7];
        pet.stat.get(StatType.PET_THIRD_ABILITY_TYPE_STAT).statValue = currentChar.petAbilitys[8];
    }

    /**
     * Handles character data by sending char list request to rotmg servers while in the daily quest room.
     * This is done here given pet yard and daily quest instance is the only instances where the char list
     * request can be done without being rejected by rotmg servers.
     * <p>
     * token Current client token string used in http request packet.
     */
    public void charListHttpRequest() {
        try {
            String httpString = HttpCharListRequest.getChartList(token);
            ArrayList<RealmCharacter> charList = HttpCharListRequest.getCharList(httpString);
            if (charList != null) characterListUpdate(charList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stores the token for char list requests.
     *
     * @param token Current client token.
     */
    public void updateToken(String token) {
        this.token = token;
    }

    /**
     * Incoming text data.
     *
     * @param p Text info.
     */
    public void text(TextPacket p) {
        if (p.text.equals("I SAID DO NOT INTERRUPT ME! For this I shall hasten your end!")) {
            Entity e = entityList.get(p.objectId);
            if (e != null) {
                e.dammahCountered = true;
            }
        }
        ChatGUI.updateChat(p);
    }

    /**
     * Handles notification packets.
     *
     * @param packet Notification packet
     */
    public void notification(NotificationPacket packet) {
        if (packet.effect == NotificationEffectType.PlayerDeath) {
            deathNotifications.add(packet);
        }
        KeypopGUI.packet(this, packet);
    }

    public ArrayList<NotificationPacket> getDeathNotifications() {
        return deathNotifications;
    }

    public long dungeonTime() {
        return timePc - timePcFirst;
    }

    public void logPacket(Packet packet) {
        dpsPacketLog.add(packet);
    }
}
