package tomato.gui.stats;

import assets.AssetMissingException;
import assets.IdToAsset;
import packets.data.StatData;
import packets.data.enums.StatType;
import packets.incoming.MapInfoPacket;
import tomato.backend.data.Entity;
import tomato.gui.TomatoGUI;
import tomato.realmshark.RealmCharacter;
import tomato.realmshark.enums.LootBags;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class LootGUI extends JPanel {

    private static LootGUI INSTANCE;

    private static JTextArea textArea;

    public LootGUI() {
        this.INSTANCE = this;

        setLayout(new BorderLayout());
        textArea = new JTextArea();
        add(TomatoGUI.createTextArea(textArea, false));
    }

    public static void update(MapInfoPacket map, Entity entity, Entity dropper, Entity player, long time) {
        INSTANCE.updateGui(map, entity, dropper, player, time);
    }

    private void updateGui(MapInfoPacket map, Entity entity, Entity dropper, Entity player, long time) {
        int exaltBonus = -1;
        long lootTime = 0;
        if (player != null) {
            exaltBonus = RealmCharacter.exaltLootBonus(player.objectType);
            lootTime = player.lootDropTime(time);
        }

        String mobName = "";
        String mapName = "";
        String dungeonBonus = "";
        String exaltString = "";
        String lootDropString = "";
        if (dropper != null) {
            mobName = dropper.name() + "[" + dropper.id + "] - ";
        }
        if (map != null) {
            mapName = map.name;
            dungeonBonus = dungeonBuff(map.dungeonModifiers3);
        }
        if (exaltBonus != -1) {
            exaltString = " Exalt: " + exaltBonus + "%";
        }
        if (lootTime > 0) {
            lootDropString = " LD-bonus ";
        }
        String s = time() + "  " + mapName + dungeonBonus + exaltString + lootDropString + " - " + mobName + LootBags.lootBagName(entity.objectType) + ": " + lootInfo(entity) + "\n";
        textArea.append(s);
    }

    public static String time() {
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd-HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.now();
        return dateTimeFormat.format(dateTime);
    }

    private String lootInfo(Entity entity) {
        StringBuilder s = new StringBuilder();
        boolean first = true;
        String[] enchants = null;

        StatData udata = entity.stat.get(StatType.UNIQUE_DATA_STRING);
        if (udata != null && udata.stringStatValue != null) {
            enchants = udata.stringStatValue.split(",");
        }

        for (int i = 0; i < 8; i++) {
            StatData sd = entity.stat.get(StatType.INVENTORY_0_STAT.get() + i);
            if (sd == null) continue;
            int statValue = sd.statValue;
            if (statValue < 1) continue;
            if (!first) s.append(" * ");
            first = false;
            try {
                s.append(IdToAsset.objectName(statValue));
            } catch (AssetMissingException var6) {
                var6.printStackTrace();
            }
            if (enchants != null && i < enchants.length && !enchants[i].isEmpty() && !enchants[i].equals("AAIE_f_9__3__f8=")) {
                s.append("[E]");
            }
            s.append("[").append(statValue).append("]");
        }
        return s.toString();
    }

    private static String dungeonBuff(String buffs) {
        String b = "";
        for (String s : buffs.split(";")) {
            if (s.contains("REWARDSBOOSTBOSS_")) {
                b += " Boss " + getaChar(s) * 15 + "% ";
            }
            if (s.contains("REWARDSBOOSTMINIONS_")) {
                b += " Minions " + (getaChar(s) + 2) * 50 + "% ";
            }
            if (s.contains("REWARDSDECREASEMINIONS_")) {
                b += " Minions " + (4 - getaChar(s)) * 25 + "% ";
            }
        }
        return b;
    }

    private static int getaChar(String s) {
        return s.charAt(s.length() - 1) - 48;
    }
}
