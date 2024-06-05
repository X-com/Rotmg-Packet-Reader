package tomato.gui.stats;

import assets.AssetMissingException;
import assets.IdToAsset;
import packets.data.ObjectStatusData;
import packets.data.StatData;
import packets.incoming.MapInfoPacket;
import tomato.backend.data.Entity;
import tomato.gui.TomatoGUI;
import tomato.realmshark.enums.LootBags;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LootGUI extends JPanel {

    private static LootGUI INSTANCE;

    private static JTextArea textArea;

    public LootGUI() {
        this.INSTANCE = this;

        setLayout(new BorderLayout());
        textArea = new JTextArea();
        add(TomatoGUI.createTextArea(textArea, false));
    }

    public static void update(MapInfoPacket map, Entity entity) {
        INSTANCE.updateGui(map, entity);
    }

    private void updateGui(MapInfoPacket map, Entity entity) {
        String s1 = map == null ? "" : map.displayName;
        if (s1.equals("{s.rotmg}")) s1 = "Nexus";
        String s = time() + "  " + s1 + " - " + LootBags.lootBagName(entity.objectType) + ": " + lootInfo(entity) + "\n";
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

        for (int i = 0; i < 8; i++) {
            StatData sd = entity.stat.get(8 + i);
            if (sd == null) continue;
            int statValue = sd.statValue;
            if (statValue < 1) continue;
            if (!first) s.append(" * ");
            first = false;
            try {
                s.append(IdToAsset.objectName(statValue)).append("[").append(statValue).append("]");
            } catch (AssetMissingException var6) {
                var6.printStackTrace();
            }
        }
        return s.toString();
    }
}
