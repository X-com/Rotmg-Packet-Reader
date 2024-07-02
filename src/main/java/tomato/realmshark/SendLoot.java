package tomato.realmshark;

import com.google.gson.*;
import packets.data.StatData;
import packets.data.WorldPosData;
import packets.data.enums.StatType;
import packets.incoming.MapInfoPacket;
import tomato.backend.data.Entity;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class SendLoot {

    private static WebSocket webSocket;

    static {
        try {
            webSocket = new WebSocket("ws://38.45.66.65:6005");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendLoot(MapInfoPacket map, Entity bag, Entity dropper, Entity player, long time) {
        webSocket.con();

        int bagId = -1;
        WorldPosData pos = bag.pos;
        String dungeon = "";
        JsonArray mods = new JsonArray();
        int mob = -1;
        int sharedLoot = -1;
        JsonArray items = new JsonArray();
        int exaltBonus = -1;
        boolean lootDrop = false;
        boolean isSeasonal = false;


        if (bag != null) {
            bagId = bag.objectType;

            String[] enchants = null;
            StatData udata = bag.stat.get(StatType.UNIQUE_DATA_STRING);
            if (udata != null && udata.stringStatValue != null) {
                enchants = udata.stringStatValue.split(",");
            }

            for (int i = 0; i < 8; i++) {
                StatData sd = bag.stat.get(StatType.INVENTORY_0_STAT.get() + i);
                if (sd == null || sd.statValue < 1) continue;
                JsonObject item = new JsonObject();
                item.addProperty("id", sd.statValue);
                if (enchants != null && i < enchants.length && !enchants[i].isEmpty() && !enchants[i].equals("AAIE_f_9__3__f8=")) {
                    int enchId = ParseEnchants.getEnchantId(enchants[i]);
                    item.addProperty("e", enchId);
                }
                items.add(item);
            }
        }
        if (player != null) {
            exaltBonus = RealmCharacter.exaltLootBonus(player.objectType);
            lootDrop = player.lootDropTime(time) > 0;

            StatData sesn = player.stat.get(StatType.SEASONAL.get());
            if (sesn != null) {
                if (sesn.statValue == 1) {
                    isSeasonal = true;
                }
            }
        }
        if (map != null) {
            dungeon = map.name;
            int[] dungeonMods = ParseDungeon.getModIds(map.dungeonModifiers3);
            for (int i = 0; i < dungeonMods.length; i++) {
                mods.add(dungeonMods[i]);
            }
        }
        if (dropper != null) {
            mob = dropper.objectType;
            sharedLoot = dropper.playersRemainAtKill();
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("bag", bagId);
        jsonObject.addProperty("pos", String.format("%f,%f", pos.x, pos.y));
        jsonObject.addProperty("dung", dungeon);
        jsonObject.add("mods", mods);

        jsonObject.addProperty("mob", mob);
        jsonObject.addProperty("share", sharedLoot);
        jsonObject.add("items", items);

        jsonObject.addProperty("exalt", exaltBonus);
        jsonObject.addProperty("ld", lootDrop);
        jsonObject.addProperty("seas", isSeasonal);

//        System.out.println(jsonObject);

        byte[] out = jsonObject.toString().getBytes(StandardCharsets.UTF_8);

        webSocket.sendBytes(out);
    }

    public static void main(String[] args) {
        webSocket.con();
//        String s = "{\"bagId\":1287,\"dungeon\":\"Spider Den\",\"dungeonMods\":\"BONUSCONSUMABLES;ENERGIZEDMINIONS_1;|D\",\"mob\":2358,\"sharedLoot\":1,\"items\":\"1799:2773[UT - Engraving: RELATIVE_SPEED_BONUS_1(320)]:2655:2745[UT - Engraving: RELATIVE_SPEED_BONUS_1(320)]:6141:1799:1799:1799\",\"exaltBonus\":35,\"lootDrop\":false,\"isSeasonal\":true}";
//        String jj = JSONParser.quote(s);
//
//        JsonObject j = new JsonObject();
//        System.out.println(jj);

        int bagId = -1;
        WorldPosData pos = new WorldPosData();
        String dungeon = "Realm of the Mad God";
        int[] dungeonMods = ParseDungeon.getModIds("BONUSCONSUMABLES;ENERGIZEDMINIONS_1;|D");
        int mob = 17735;
        int sharedLoot = 1;
        int exaltBonus = 35;
        boolean lootDrop = false;
        boolean isSeasonal = true;

        JsonObject jsonObject = new JsonObject();
        JsonArray items = new JsonArray();
        for (int i = 0; i < 8; i++) {
            JsonObject item = new JsonObject();
            item.addProperty("id", 1234);
            item.addProperty("e", 1234);
            items.add(item);
        }

        jsonObject.addProperty("bag", bagId);
        jsonObject.addProperty("pos", String.format("%s,%s", pos.x, pos.y));
        jsonObject.addProperty("dung", dungeon);
        JsonArray mods = new JsonArray();
        for (int i = 0; i < dungeonMods.length; i++) {
            mods.add(dungeonMods[i]);
        }
        jsonObject.add("mods", mods);

        jsonObject.addProperty("mob", mob);
        jsonObject.addProperty("share", sharedLoot);
        jsonObject.add("items", items);

        jsonObject.addProperty("exalt", exaltBonus);
        jsonObject.addProperty("ld", lootDrop);
        jsonObject.addProperty("seas", isSeasonal);

        System.out.println(jsonObject.toString());

//        String s = "{\"bagId\":1287,\"dungeon\":\"Realm of the Mad God\",\"dungeonMods\":\"\",\"mob\":17735,\"sharedLoot\":1,\"items\":\"2783[UT - Engraving: ONHIT_DAMAGING_1(49)]\",\"exaltBonus\":35,\"lootDrop\":false,\"isSeasonal\":true}";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(jsonObject);
        System.out.println(jsonOutput);

        byte[] out = jsonObject.toString().getBytes(StandardCharsets.UTF_8);

        webSocket.sendBytes(out);
    }
}