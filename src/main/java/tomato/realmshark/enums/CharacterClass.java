package tomato.realmshark.enums;

import java.util.TreeMap;
import java.util.TreeSet;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

import util.StringXML;

/**
 * Character class enum to get class name and stats from class id.
 */
public enum CharacterClass {
    Rogue(768, 750, 252, 55, 25, 65, 75, 40, 50, new int[]{768, 800, 804}),
    Archer(775, 750, 252, 75, 25, 55, 50, 40, 50, new int[]{775, 802, 796}),
    Wizard(782, 700, 385, 75, 25, 50, 75, 40, 60, new int[]{782, 801, 803}),
    Priest(784, 700, 385, 55, 25, 55, 60, 40, 75, new int[]{784, 805, 817}),
    Warrior(797, 800, 252, 75, 25, 50, 50, 75, 50, new int[]{797, 798, 799}),
    Knight(798, 800, 252, 50, 40, 50, 50, 75, 50, new int[]{797, 798, 799}),
    Paladin(799, 800, 252, 55, 30, 55, 55, 60, 75, new int[]{797, 798, 799}),
    Assassin(800, 750, 305, 65, 25, 65, 75, 40, 60, new int[]{768, 800, 804}),
    Necromancer(801, 700, 385, 75, 25, 50, 60, 40, 75, new int[]{782, 801, 803}),
    Huntress(802, 750, 305, 65, 25, 50, 60, 40, 50, new int[]{775, 802, 796}),
    Trickster(804, 750, 252, 65, 25, 75, 75, 40, 60, new int[]{768, 800, 804}),
    Mystic(803, 700, 385, 65, 25, 60, 65, 40, 75, new int[]{782, 801, 803}),
    Sorcerer(805, 700, 385, 70, 25, 60, 60, 75, 60, new int[]{784, 805, 817}),
    Ninja(806, 800, 252, 70, 25, 60, 70, 60, 70, new int[]{806, 785, 818}),
    Samurai(785, 800, 252, 75, 30, 55, 55, 60, 60, new int[]{806, 785, 818}),
    Bard(796, 750, 385, 55, 25, 55, 70, 45, 75, new int[]{775, 802, 796}),
    Summoner(817, 700, 385, 60, 25, 60, 75, 40, 75, new int[]{784, 805, 817}),
    Kensei(818, 800, 252, 65, 25, 60, 65, 60, 50, new int[]{806, 785, 818});

    private static final String PLAYERS_XML_PATH = "assets/xml/players.xml";

    private int life, mana, atk, def, spd, dex, vit, wis;
    private final int id;
    private final int[] weaponGroup;
    private int[] maxStats;

    public static final CharacterClass[] CHAR_CLASS_LIST;
    private static final TreeMap<Integer, CharacterClass> CHARACTER_CLASS = new TreeMap<>();
    private static final TreeMap<Integer, String> CLASS_NAME = new TreeMap<>();
    private static final TreeMap<Integer, int[]> CLASS_MAX_STATS = new TreeMap<>();
    private static final TreeMap<Integer, int[]> WEAPON_CLASSES = new TreeMap<>();
    private static final TreeSet<Integer> CHARACTER_IDS = new TreeSet<>();

    static {
        CHAR_CLASS_LIST = CharacterClass.values().clone();
        try {
            FileInputStream file = new FileInputStream(PLAYERS_XML_PATH);
            populateFromXML(new BufferedReader(new InputStreamReader(file)).lines().collect(Collectors.joining("\n")));

            for (CharacterClass o : CharacterClass.values()) {
                o.maxStats = new int[]{o.life, o.mana, o.atk, o.def, o.spd, o.dex, o.vit, o.wis};

                CHARACTER_IDS.add(o.id);
                CHARACTER_CLASS.put(o.id, o);
                CLASS_NAME.put(o.id, o.toString());
                WEAPON_CLASSES.put(o.id, o.weaponGroup);
                CLASS_MAX_STATS.put(o.id, o.maxStats);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    CharacterClass(int id, int life, int mana, int atk, int def, int spd, int dex, int vit, int wis, int[] weaponGroup) {
        this.id = id;
        this.life = life;
        this.mana = mana;
        this.atk = atk;
        this.def = def;
        this.spd = spd;
        this.dex = dex;
        this.vit = vit;
        this.wis = wis;
        this.weaponGroup = weaponGroup;
    }

    private static void populateFromXML(String rawXML) throws Exception {
        // Parse the XML using StringXML
        StringXML root = StringXML.getParsedXML(rawXML);

        // Map to group character ids by Equipment's first integer
        Map<Integer, List<Integer>> weaponGroups = new HashMap<>();
        Map<Integer, Integer> weaponCharacter = new HashMap<>();

        // Process each Object element
        for (StringXML object : root) {
            if (!object.name.equals("Object")) continue; // Skip non-Object nodes
            // Extract id and name
            String name = object.children.stream()
                    .filter(child -> child.name.equals("id"))
                    .map(child -> child.value)
                    .findFirst()
                    .orElse(null);

//            int id = object.children.stream()
//                    .filter(child -> child.name.equals("type"))
//                    .mapToInt(child -> Integer.decode(child.value))
//                    .findFirst()
//                    .orElseThrow(() -> new IllegalArgumentException("Missing type attribute"));

            CharacterClass cc = null;
            for(CharacterClass o : CharacterClass.CHAR_CLASS_LIST) {
                if(o.name().equals(name)) {
                    cc = o;
                    break;
                }
            }
            if(cc == null) continue;

            // Extract individual max stats
            int l = updateValue(object, "MaxHitPoints");
            if (cc.life != l) {
                cc.life = l;
            }
            int m = updateValue(object, "MaxMagicPoints");
            if (cc.mana != l) cc.mana = m;
            int a = updateValue(object, "Attack");
            if (cc.atk != l) cc.atk = a;
            int df = updateValue(object, "Defense");
            if (cc.def != l) cc.def = df;
            int s = updateValue(object, "Speed");
            if (cc.spd != l) cc.spd = s;
            int dx = updateValue(object, "Dexterity");
            if (cc.dex != l) cc.dex = dx;
            int v = updateValue(object, "HpRegen");
            if (cc.vit != l) cc.vit = v;
            int w = updateValue(object, "MpRegen");
            if (cc.wis != l) cc.wis = w;

//            // Extract Equipment and determine weapon group
//            String equipment = "";
//            for (StringXML child : object.children) {
//                if (child.name.equals("Equipment")) {
//                    for (StringXML stringXML : child.children) {
//                        equipment = stringXML.value;
//                        break;
//                    }
//                    break;
//                }
//            }
//            String[] equipmentItems = equipment.split(",");
//            int weaponId = Integer.decode(equipmentItems[0].trim());
//            weaponGroups.computeIfAbsent(weaponId, k -> new ArrayList<>()).add(id);
//            weaponCharacter.put(id, weaponId);

//            // Create CharacterClass instance
//            charClassList.add(new CharacterClass(id, name, life, mana, atk, def, spd, dex, vit, wis, new int[0])); // Weapon group will be set later
        }

//        // Update weapon groups for each CharacterClass
//        for (CharacterClass character : charClassList) {
//            List<Integer> group = weaponGroups.get(weaponCharacter.get(character.id));
//            if (group != null) {
//                character.weaponGroup = group.stream().mapToInt(Integer::intValue).toArray();
//            }
//        }
    }

    private static int updateValue(StringXML object, String tagName) {
        return object.children.stream()
                .filter(child -> child.name.equals(tagName))
                .map(child -> child.children.stream()
                        .filter(attr -> attr.name.equals("max"))
                        .mapToInt(attr -> Integer.parseInt(attr.value))
                        .findFirst()
                        .orElse(0))
                .findFirst()
                .orElse(0);
    }

    /**
     * Character class id to class name.
     *
     * @param id Class id
     * @return Class name
     */
    public static String getName(int id) {
        return CLASS_NAME.get(id);
    }

    /**
     * Get character max stats from class id.
     * Order: life, mana, atk, def, spd, dex, vit, wis
     *
     * @param id Class id
     * @return Class max stat array
     */
    public static int[] getStats(int id) {
        return CLASS_MAX_STATS.get(id);
    }

    /**
     * Getters for character class stats.
     *
     * @param id Class id
     * @return Class max stat
     */
    public static int getLife(int id) {
        return CHARACTER_CLASS.get(id).life;
    }

    /**
     * Checks if the objectType id is of player type.
     *
     * @param objectType Object id
     * @return Is player object type
     */
    public static boolean isPlayerCharacter(int objectType) {
        return CHARACTER_IDS.contains(objectType);
    }

    /**
     * Gets the same weapon classes as requested.
     *
     * @param classId Id of one class sharing the same weapon.
     * @return List of classes sharing same weapons.
     */
    public static int[] weaponClasses(int classId) {
        return WEAPON_CLASSES.get(classId);
    }

    public static int getMana(int id) {
        return CHARACTER_CLASS.get(id).mana;
    }

    public static int getAtk(int id) {
        return CHARACTER_CLASS.get(id).atk;
    }

    public static int getDef(int id) {
        return CHARACTER_CLASS.get(id).def;
    }

    public static int getSpd(int id) {
        return CHARACTER_CLASS.get(id).spd;
    }

    public static int getDex(int id) {
        return CHARACTER_CLASS.get(id).dex;
    }

    public static int getVit(int id) {
        return CHARACTER_CLASS.get(id).vit;
    }

    public static int getWis(int id) {
        return CHARACTER_CLASS.get(id).wis;
    }

    public int getId() {
        return id;
    }
}
