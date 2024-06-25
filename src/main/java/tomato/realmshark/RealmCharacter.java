package tomato.realmshark;

import tomato.realmshark.enums.CharacterClass;

import java.util.Arrays;
import java.util.TreeMap;

/**
 * Basic data class to store Character info.
 */
public class RealmCharacter {
    // Dex 0
    // Spd 1
    // Vit 2
    // Wis 3
    // Def 4
    // Atk 5
    // Mana 6
    // Life 7
    public static TreeMap<Integer, int[]> exalts = new TreeMap<>();

    public int charId;
    public short classNum;
    public String classString;
    public int level;
    public int skin;
    public long exp;
    public long fame;
    public boolean seasonal;
    public boolean backpack;
    public boolean qs3;

    public int[] equipment;
    public String[] equipQS;
    public String date;

    public int hp;
    public int mp;
    public int atk;
    public int def;
    public int spd;
    public int dex;
    public int vit;
    public int wis;
    public String pcStats;
    public RealmCharacterStats charStats;

    public String petName;
    public String petCreatedOn;
    public int petSkin;
    public int petType;
    public int petInstanceId;
    public int petMaxAbilityPower;
    public int petRarity;
    public int[] petAbilitys;

    /**
     * Returns the player exalt loot drop bonus
     *
     * @param classId Class to receive exalt bonus.
     */
    public static int exaltLootBonus(int classId) {
        if (fullyExalted()) {
            return 35;
        }
        int bonus = 25;
        for (int c : CharacterClass.weaponClasses(classId)) {
            int[] ints = exalts.get(c);
            if (ints == null) return 0;

            for (int i : ints) {
                if (i < 5) {
                    return 0;
                } else if (i < 15 && bonus > 5) {
                    bonus = 5;
                } else if (i < 30 && bonus > 10) {
                    bonus = 10;
                } else if (i < 50 && bonus > 15) {
                    bonus = 15;
                } else if (i < 75 && bonus > 20) {
                    bonus = 20;
                }
            }
        }
        return bonus;
    }

    /**
     * Checks if account is fully exalted.
     *
     * @return True if account is fully exalted.
     */
    private static boolean fullyExalted() {
        if (exalts.size() < 18) return false;

        for (int[] e : exalts.values()) {
            for (int i : e) {
                if (i < 75) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Set character stats
     */
    public void updateCharStats(RealmCharacterStats c) {
        charStats = c;
    }

    /**
     * Simple setter for the class string from the class id.
     */
    public void setClassString() {
        classString = CharacterClass.getName(classNum);
    }

    /**
     * Decodes psStats into charStats
     */
    public void setCharacterStats() {
        charStats = new RealmCharacterStats();
        try {
            charStats.decode(pcStats);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "RealmCharacter{" +
                "\n   charId=" + charId +
                "\n   classNum=" + classNum +
                "\n   classString=" + classString +
                "\n   level=" + level +
                "\n   skin=" + skin +
                "\n   exp=" + exp +
                "\n   fame=" + fame +
                "\n   seasonal=" + seasonal +
                "\n   backpack=" + backpack +
                "\n   qs3=" + qs3 +
                "\n   equipment=" + Arrays.toString(equipment) +
                "\n   equipQS=" + Arrays.toString(equipQS) +
                "\n   date=" + date +
                "\n   hp=" + hp +
                "\n   mp=" + mp +
                "\n   atk=" + atk +
                "\n   def=" + def +
                "\n   spd=" + spd +
                "\n   dex=" + dex +
                "\n   vit=" + vit +
                "\n   wis=" + wis +
                "\n   pcStats=" + pcStats +
                "\n   charStats=" + charStats +
                "\n   petName=" + petName +
                "\n   petCreatedOn=" + petCreatedOn +
                "\n   petSkin=" + petSkin +
                "\n   petType=" + petType +
                "\n   petInstanceId=" + petInstanceId +
                "\n   petMaxAbilityPower=" + petMaxAbilityPower +
                "\n   petRarity=" + petRarity +
                "\n   petAbilitys=" + Arrays.toString(petAbilitys);
    }
}
