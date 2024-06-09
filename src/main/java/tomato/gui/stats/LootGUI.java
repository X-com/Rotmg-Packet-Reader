package tomato.gui.stats;

import assets.AssetMissingException;
import assets.IdToAsset;
import assets.ImageBuffer;
import packets.data.StatData;
import packets.data.enums.StatType;
import packets.incoming.MapInfoPacket;
import tomato.backend.data.Entity;
import tomato.gui.SmartScroller;
import tomato.gui.TomatoGUI;
import tomato.gui.security.ParsePanelGUI;
import tomato.realmshark.ParseEnchants;
import tomato.realmshark.RealmCharacter;
import tomato.realmshark.enums.CharacterStatistics;
import tomato.realmshark.enums.LootBags;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class LootGUI extends JPanel {

    private static LootGUI INSTANCE;

    private static JPanel lootPanel;
    private static JTextArea textArea;

    public LootGUI() {
        this.INSTANCE = this;
        setLayout(new BorderLayout());

        lootPanel = new JPanel();

        lootPanel.setLayout(new BoxLayout(lootPanel, BoxLayout.Y_AXIS));
        validate();

        JScrollPane scroll = new JScrollPane(lootPanel);
        scroll.getVerticalScrollBar().setUnitIncrement(40);
        new SmartScroller(scroll, 0);
        add(scroll, BorderLayout.CENTER);
    }

    public static void update(MapInfoPacket map, Entity entity, Entity dropper, Entity player, long time) {
        INSTANCE.updateGui(map, entity, dropper, player, time);
    }

    private void updateGui(MapInfoPacket map, Entity entity, Entity dropper, Entity player, long time) {
//        int exaltBonus = -1;
//        long lootTime = 0;
//        if (player != null) {
//            exaltBonus = RealmCharacter.exaltLootBonus(player.objectType);
//            lootTime = player.lootDropTime(time);
//        }
//
//        String mobName = "";
//        String mapName = "";
//        String dungeonBonus = "";
//        String exaltString = "";
//        String lootDropString = "";
//        if (dropper != null) {
//            mobName = dropper.name() + "[" + dropper.id + "] - ";
//        }
//        if (map != null) {
//            mapName = map.name;
//            dungeonBonus = dungeonBuff(map.dungeonModifiers3);
//        }
//        if (exaltBonus != -1) {
//            exaltString = " Exalt: " + exaltBonus + "%";
//        }
//        if (lootTime > 0) {
//            lootDropString = " LD-bonus ";
//        }
//        String s = time() + "  " + mapName + dungeonBonus + exaltString + lootDropString + " - " + mobName + LootBags.lootBagName(entity.objectType) + ": " + lootInfo(entity) + "\n";
//        textArea.append(s);

        JPanel panel = createMainBox(map, entity, dropper, player, time);
        lootPanel.add(panel, 0);

        INSTANCE.guiUpdate();
    }

    private void guiUpdate() {
        revalidate();
        repaint();
    }

    private static JPanel createMainBox(MapInfoPacket map, Entity entity, Entity dropper, Entity player, long time) {
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.gray), BorderFactory.createEmptyBorder(0, 20, 0, 20)));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        int y = 24;
        int width = 30;

        int exaltBonus = -1;
        long lootTime = 0;
        if (player != null) {
            exaltBonus = RealmCharacter.exaltLootBonus(player.objectType);
            lootTime = player.lootDropTime(time);
        }

        mainPanel.add(Box.createHorizontalGlue());

        width = displayBagDungMob(map, entity, dropper, mainPanel, width, exaltBonus, lootTime);
        mainPanel.add(Box.createHorizontalStrut(30));
        width = displayBagLootIcons(entity, mainPanel, width);

        mainPanel.add(Box.createHorizontalGlue());

        mainPanel.setMaximumSize(new Dimension(width, y));

        return mainPanel;
    }

    private static int displayBagDungMob(MapInfoPacket map, Entity entity, Entity dropper, JPanel mainPanel, int width, int exaltBonus, long lootTime) {
        JPanel panel = new JPanel();
        width += 75;

        panel.setPreferredSize(new Dimension(75, 24));
        panel.setMaximumSize(new Dimension(75, 24));
        panel.setLayout(new GridLayout(1, 3));

        displayBagIcon(entity, exaltBonus, lootTime, panel);
        displayDungeonIcon(map, panel);
        displayMobIcon(dropper, panel);

        mainPanel.add(panel);
        return width;
    }

    private static int displayBagLootIcons(Entity entity, JPanel mainPanel, int width) {
        JPanel panel = new JPanel();
//            panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        width += 200;

        panel.setPreferredSize(new Dimension(200, 24));
        panel.setMaximumSize(new Dimension(200, 24));
        panel.setLayout(new GridLayout(1, 8));

        String[] enchants = null;
        StatData udata = entity.stat.get(StatType.UNIQUE_DATA_STRING);
        if (udata != null && udata.stringStatValue != null) {
            enchants = udata.stringStatValue.split(",");
        }

        for (int i = 0; i < 8; i++) {
            StatData sd = entity.stat.get(StatType.INVENTORY_0_STAT.get() + i);
            if (sd == null || sd.statValue < 1) {
                JPanel comp = new JPanel();
                comp.setMinimumSize(new Dimension(24, 24));
                panel.add(comp);
            }
            int statValue = sd.statValue;
            try {
                JLabel icon = new JLabel(ImageBuffer.getOutlinedIcon(statValue, 20));
                String itemName = IdToAsset.objectName(statValue);
                if (enchants != null && i < enchants.length && !enchants[i].isEmpty() && !enchants[i].equals("AAIE_f_9__3__f8=")) {
                    String e = ParseEnchants.parse(enchants[i]);
                    if (!e.isEmpty()) {
                        itemName += "<br>" + e;
                    }
                }
                icon.setToolTipText("<html>" + itemName + "</html>");
                panel.add(icon);
            } catch (AssetMissingException var6) {
                var6.printStackTrace();
            }
        }

        mainPanel.add(panel);
        return width;
    }

    private static void displayBagIcon(Entity entity, int exaltBonus, long lootTime, JPanel panel) {
        int bag = entity.objectType;
        try {
            JLabel icon = new JLabel(ImageBuffer.getOutlinedIcon(bag, 20));
            String name = IdToAsset.objectName(bag);
            if (exaltBonus != -1) {
                name += "<br>Exalt Bonus: " + exaltBonus + "%";
            }
            if (lootTime > 0) {
                name += "<br>Loot drop bonus 50%";
            }
            icon.setToolTipText("<html>" + name + "</html>");

            panel.add(icon);
        } catch (AssetMissingException e) {
            e.printStackTrace();
        }
    }

    private static void displayMobIcon(Entity dropper, JPanel panel) {
        int mob = 100;
        if (dropper != null) {
            mob = dropper.objectType;
        }
        try {
            JLabel icon = new JLabel(ImageBuffer.getOutlinedIcon(mob, 20));
            String name = "Unknown";
            if (mob != 100) {
                name = IdToAsset.objectName(mob);
            }
            icon.setToolTipText(name);
            panel.add(icon);
        } catch (AssetMissingException e) {
            e.printStackTrace();
        }
    }

    private static void displayDungeonIcon(MapInfoPacket map, JPanel panel) {
        int dungeon = 100;
        String dungeonName = "Unknown";
        String dungeonModifiers = "";
        if (map != null) {
            dungeonName = map.name;
            dungeonModifiers = dungeonBuff(map.dungeonModifiers3);

            CharacterStatistics dungeonIndex = CharacterStatistics.statByName(map.name);
            if (dungeonIndex != null) {
                dungeon = dungeonIndex.getSpriteId();
            }
        }

        if (dungeonName.equals("Realm of the Mad God")) dungeon = 1796;
        JLabel icon = new JLabel(ImageBuffer.getOutlinedIcon(dungeon, 20));
        if (!dungeonModifiers.isEmpty()) {
            dungeonName += "<br>" + dungeonModifiers;
        }
        icon.setToolTipText("<html>" + dungeonName + "</html>");
        panel.add(icon);
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
