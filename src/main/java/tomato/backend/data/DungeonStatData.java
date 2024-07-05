package tomato.backend.data;

import assets.IdToAsset;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import packets.data.StatData;
import packets.data.enums.StatType;
import tomato.gui.stats.DungeonStats;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class DungeonStatData {

    private static final String FILE_NAME = "dungeon.stats";

    public TreeMap<String, DungeonInfo> data;
    public DungeonInfo info;

    public DungeonStatData() {
        data = new TreeMap<>();
    }

    public void updateEntityDamage(String dungeon, Entity mob) {
        if (mob == null || mob.objectType == 0) return;
        if (info == null) {
            info = data.computeIfAbsent(dungeon, d -> new DungeonInfo(dungeon));
        } else if (!info.name.equals(dungeon)) {
            System.out.println("dungeon mismatch entity damage");
            return;
        }
        int entityType = mob.objectType;
        info.addMob(entityType);

        DungeonStats.update(this, dungeon);
    }

    public void updateItems(String dungeon, Entity mob, Entity items) {
        if (items == null) return;
        if (info == null) {
            info = data.computeIfAbsent(dungeon, d -> new DungeonInfo(dungeon));
        } else if (!info.name.equals(dungeon)) {
            System.out.println("dungeon mismatch items");
            return;
        }
        int entityType = mob != null ? mob.objectType : 0;

//        StatData udata = items.stat.get(StatType.UNIQUE_DATA_STRING);
//        String[] enchants = null;
//        if (udata != null && udata.stringStatValue != null) {
//            enchants = udata.stringStatValue.split(",");
//        }
        for (int i = 0; i < 8; i++) {
            StatData sd = items.stat.get(StatType.INVENTORY_0_STAT.get() + i);
            if (sd == null || sd.statValue < 1) continue;
//            if (enchants != null && i < enchants.length && !enchants[i].isEmpty() && !enchants[i].equals("AAIE_f_9__3__f8=")) {
//            }

            int itemId = sd.statValue;

            info.addItems(entityType, itemId);
        }
        DungeonStats.update(this, dungeon);
    }

    public void updateDungeon(String dungeon, long time) {
        if (info != null && info.name.equals(dungeon)) {
            info.totalTime += time;
            info.enteredDungeon++;
            DungeonStats.update(this, dungeon);
            info = null;
            save();
        }
    }

    private void save() {
        Gson gson = new Gson();
        try {
            FileWriter writer = new FileWriter(FILE_NAME);
            gson.toJson(this, writer);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void load() {
        Gson gson = new Gson();
        File f = new File(FILE_NAME);
        if (!f.exists()) return;

        try {
            JsonReader reader = new JsonReader(new FileReader(f));
            DungeonStatData d = gson.fromJson(reader, DungeonStatData.class);
            if (d == null) return;
            data = d.data;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        DungeonStats.update(this, null);
    }

    public class DungeonInfo {
        private String name;
        private int enteredDungeon;
        private long totalTime;
        private TreeMap<Integer, Integer> entityDamaged = new TreeMap<>();
        private TreeMap<Integer, Loot> entityLoot = new TreeMap<>();

        public DungeonInfo(String name) {
            this.name = name;
        }

        public void addMob(int entity) {
            entityDamaged.merge(entity, 1, Integer::sum);
        }

        public void addItems(int entity, int itemId) {
            entityLoot.computeIfAbsent(entity, i -> new Loot()).add(itemId);
        }

        public int getEnteredDungeon() {
            return enteredDungeon;
        }

        public long getTotalTime() {
            return totalTime;
        }

        public String getName() {
            return name;
        }

        public Map<Integer, Integer> getEntityDamaged() {
            return entityDamaged;
        }

        public Loot getLoot(int id) {
            return entityLoot.get(id);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(name).append(" [").append(enteredDungeon).append("] ").append(totalTime).append("\n");
            for (Map.Entry<Integer, Integer> e : entityDamaged.entrySet()) {
                Integer k = e.getKey();
                sb.append("  ").append(IdToAsset.objectName(k)).append(":").append(e.getValue()).append("\n");
                Loot loot = entityLoot.get(k);
                if (loot != null) sb.append(loot);
            }

            Loot lootUnknown = entityLoot.get(0);
            if (lootUnknown != null) {
                sb.append("  ").append("Unknown").append("\n");
                sb.append(lootUnknown);
            }

            return sb.toString();
        }
    }

    public class Loot {
        private TreeMap<Integer, Integer> lootList = new TreeMap<>();

        public void add(int itemId) {
            lootList.merge(itemId, 1, Integer::sum);
        }

        public TreeMap<Integer, Integer> getItems() {
            return lootList;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<Integer, Integer> e : lootList.entrySet()) {
                sb.append("    ").append(IdToAsset.objectName(e.getKey())).append(":").append(e.getValue()).append("\n");
            }
            return sb.toString();
        }

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, DungeonInfo> e : data.entrySet()) {
            sb.append(e.getValue());
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        DungeonStatData data = new DungeonStatData();

        data.load();
        System.out.println(data);
        if (true) return;

        Entity e = new Entity(null, 0, 0);
        e.objectType = 100;
        Entity e2 = new Entity(null, 0, 0);
        e2.objectType = 102;
        data.updateEntityDamage("Temp Dung", e);
        data.updateEntityDamage("Temp Dung", e);
        data.updateEntityDamage("Temp Dung", e2);

        Entity items = new Entity(null, 0, 0);
        StatData d = new StatData();
        d.statValue = 1;
        items.stat.set(StatType.INVENTORY_0_STAT, d);
        StatData d2 = new StatData();
        d2.statValue = 2;
        items.stat.set(StatType.INVENTORY_1_STAT, d2);
        data.updateItems("Temp Dung", e, items);
        data.updateItems("Temp Dung", null, items);

        data.updateDungeon("Temp Dung", 1000);
        Entity e3 = new Entity(null, 0, 0);
        e3.objectType = 100;
        data.updateEntityDamage("Temp Dung 2", e3);
        data.updateDungeon("Temp Dung 2", 1234);

//        System.out.println(data);

//        String json = gson.toJson(data);
//        System.out.println(json);
        data.save();

//        Gson gson = new Gson();
//        data = gson.fromJson(json, DungeonStatData.class);
//        System.out.println(data);
    }
}
