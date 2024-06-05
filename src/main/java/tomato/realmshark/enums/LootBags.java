package tomato.realmshark.enums;

import java.util.TreeMap;
import java.util.TreeSet;

public enum LootBags {
    BROWN(1280, "Brown"),
    SOULBOUND(1283, "Soulbound"),
    PINK(1286, "Pink"),
    PURPLE(1287, "Purple"),
    EGG(1288, "Egg Basket"),
    TEAL(1289, "Teal"),
    BLUE(1291, "Blue"),
    WHITE(1292, "White"),
    GOLD(1294, "Gold"),
    ORANGE(1295, "Orange"),
    RED(1708, "Red"),
    BOOSTED_WHITE(1296, "B.White"),
    BOOSTED_BROWN(1709, "B.Brown"),
    BOOSTED_PINK(1710, "B.Pink"),
    BOOSTED_PURPLE(1722, "B.Purple"),
    BOOSTED_EGG(1723, "B.Egg"),
    BOOSTED_GOLD(1724, "B.Gold"),
    BOOSTED_TEAL(1725, "B.Teal"),
    BOOSTED_BLUE(1726, "B.Blue"),
    BOOSTED_ORANGE(1727, "B.Orange"),
    BOOSTED_RED(1728, "B.Red");

    int id;
    String name;

    public static final TreeSet<Integer> lootDrop = new TreeSet<>();
    public static final TreeMap<Integer, String> lootBagName = new TreeMap<>();

    static {
        for (LootBags o : LootBags.values()) {
            if (o.id >= 1287) {
                lootDrop.add(o.id);
            }
            lootBagName.put(o.id, o.name);
        }
    }

    LootBags(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static boolean isLootDropBag(int id) {
        return lootDrop.contains(id);
    }

    public static String lootBagName(int id) {
        return lootBagName.get(id);
    }
}
