package tomato.gui.stats;

import assets.IdToAsset;
import assets.ImageBuffer;
import packets.data.StatData;
import packets.data.enums.StatType;
import packets.incoming.MapInfoPacket;
import tomato.backend.data.Entity;
import tomato.gui.SmartScroller;
import tomato.gui.dps.IconDpsGUI;
import tomato.realmshark.*;
import tomato.realmshark.enums.CharacterStatistics;
import tomato.realmshark.enums.LootBags;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LootGUI extends JPanel {

    private static LootGUI INSTANCE;

    private static boolean cleared = false;
    private static boolean update = false;
    private static JPanel lootPanel;
    private static JTextArea textArea;
    private static Font mainFont;
    private static int lootDrops;
    private boolean disableLootSharing = false;

    public LootGUI() {
        lootDrops = 0;
        this.INSTANCE = this;
        setLayout(new BorderLayout());

        lootPanel = new JPanel();

        lootPanel.setLayout(new BoxLayout(lootPanel, BoxLayout.Y_AXIS));

        lootPanel.add(new JLabel("Change instance to get loot info."));
        validate();

        JScrollPane scroll = new JScrollPane(lootPanel);
        scroll.getVerticalScrollBar().setUnitIncrement(40);
        new SmartScroller(scroll, 0);
        add(scroll, BorderLayout.CENTER);
    }

    public static void update(MapInfoPacket map, Entity bag, Entity dropper, Entity player, long time) {
        INSTANCE.updateGui(map, bag, dropper, player, time);
    }

    public static void updateExaltStats() {
        update = true;
        if (!cleared) {
            cleared = true;
            lootPanel.removeAll();
            INSTANCE.guiUpdate();
        }
    }

    private void updateGui(MapInfoPacket map, Entity bag, Entity dropper, Entity player, long time) {
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
//        String s = time() + "  " + mapName + dungeonBonus + exaltString + lootDropString + " - " + mobName + LootBags.lootBagName(bag.objectType) + ": " + lootInfo(bag) + "\n";
//        textArea.append(s);

        if (player == null || !update) return;
        if (Sound.playWhiteBagSound && isWhiteBag(bag)) {
            Sound.whitebag.play();
        }
        if (Sound.playOrangeBagSound && isOrangeBag(bag)) {
            Sound.orangebag.play();
        }
        JPanel panel = createMainBox(map, bag, dropper, player, time);
        lootPanel.add(panel, 0);
        INSTANCE.guiUpdate();
        if (!disableLootSharing) {
            SendLoot.sendLoot(map, bag, dropper, player, time);
        }
    }

    private boolean isWhiteBag(Entity bag) {
        int id = bag.objectType;
        return id == LootBags.WHITE.getId() || id == LootBags.BOOSTED_WHITE.getId();
    }

    private boolean isOrangeBag(Entity bag) {
        int id = bag.objectType;
        return id == LootBags.ORANGE.getId() || id == LootBags.BOOSTED_ORANGE.getId();
    }

    private void guiUpdate() {
        revalidate();
        repaint();
    }

    private static JPanel createMainBox(MapInfoPacket map, Entity bag, Entity dropper, Entity player, long time) {
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

        width = displayCount(mainPanel, width);
        mainPanel.add(Box.createHorizontalStrut(10));
        width = displayTime(mainPanel, width);
        mainPanel.add(Box.createHorizontalStrut(10));
        width = displayBagDungMob(map, bag, player, dropper, mainPanel, width, exaltBonus, lootTime);
        mainPanel.add(Box.createHorizontalStrut(30));
        width = displayBagLootIcons(bag, mainPanel, width);

        mainPanel.add(Box.createHorizontalGlue());

        mainPanel.setMaximumSize(new Dimension(width, y));

        return mainPanel;
    }

    private static int displayBagDungMob(MapInfoPacket map, Entity entity, Entity player, Entity dropper, JPanel mainPanel, int width, int exaltBonus, long lootTime) {
        JPanel panel = new JPanel();
        width += 100;

        panel.setPreferredSize(new Dimension(100, 24));
        panel.setMaximumSize(new Dimension(100, 24));
        panel.setLayout(new GridLayout(1, 4));

        displayBagIcon(entity, lootTime, panel);
        displayPlayerIcon(map, player, exaltBonus, panel);
        displayDungeonIcon(map, panel);
        displayMobIcon(dropper, panel);

        mainPanel.add(panel);
        return width;
    }

    private static int displayCount(JPanel mainPanel, int width) {
        JPanel o = new JPanel();
        int pannelSize = IconDpsGUI.getStringSize("#1234");
        width += pannelSize;

        o.setPreferredSize(new Dimension(pannelSize, 24));
        o.setMaximumSize(new Dimension(pannelSize, 24));
        o.setLayout(new BorderLayout());
        lootDrops++;

        try {
            String text = "#" + lootDrops;
            JLabel timeLabel = new JLabel(text, JLabel.CENTER);

            timeLabel.setAlignmentX(JLabel.LEFT);
            o.setAlignmentX(JLabel.LEFT);
            o.setAlignmentX(LEFT_ALIGNMENT);
            timeLabel.setHorizontalAlignment(SwingConstants.LEFT);
            timeLabel.setFont(mainFont);
            o.add(timeLabel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mainPanel.add(o);
        return width;
    }

    private static int displayTime(JPanel mainPanel, int width) {
        JPanel o = new JPanel();
        int pannelSize = IconDpsGUI.getStringSize("HH:mm:ss");
        width += pannelSize;

        o.setPreferredSize(new Dimension(pannelSize, 24));
        o.setMaximumSize(new Dimension(pannelSize, 24));
        o.setLayout(new BorderLayout());

        try {
            String text = timeShort();
            JLabel timeLabel = new JLabel(text, JLabel.CENTER);

            timeLabel.setAlignmentX(JLabel.LEFT);
            o.setAlignmentX(JLabel.LEFT);
            o.setAlignmentX(LEFT_ALIGNMENT);
            timeLabel.setHorizontalAlignment(SwingConstants.LEFT);
            timeLabel.setFont(mainFont);
            o.add(timeLabel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mainPanel.add(o);
        return width;
    }

    private static void displayPlayerIcon(MapInfoPacket map, Entity player, int exaltBonus, JPanel panel) {
        int picon = 100;
        boolean isSeasonal = false;
        String name = "Unknown";
        if (map != null) {
            StatData sd = player.stat.get(StatType.SKIN_ID.get());
            StatData sesn = player.stat.get(StatType.SEASONAL.get());
            if (sd != null) {
                picon = sd.statValue;
                if (picon == 0) picon = player.objectType;
                name = IdToAsset.objectName(picon);
            }
            if (sesn != null) {
                if (sesn.statValue == 1) {
                    isSeasonal = true;
                }
            }
        }
        JLabel icon = new JLabel(ImageBuffer.getOutlinedIcon(picon, 20));
        if (exaltBonus != -1) {
            name += "<br>Exalt Bonus: " + exaltBonus + "%";
        }
        if (isSeasonal) {
            name += "<br>Seasonal";
        }
        icon.setToolTipText("<html>" + name + "</html>");

        panel.add(icon);
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
        }

        mainPanel.add(panel);
        return width;
    }

    private static void displayBagIcon(Entity entity, long lootTime, JPanel panel) {
        int bag = entity.objectType;
        JLabel icon = new JLabel(ImageBuffer.getOutlinedIcon(bag, 20));
        String name = time();
        name += "<br>" + IdToAsset.objectName(bag);
        if (lootTime > 0) {
            name += "<br>Loot drop bonus 50%";
        }
        icon.setToolTipText("<html>" + name + "</html>");

        panel.add(icon);
    }

    private static void displayMobIcon(Entity dropper, JPanel panel) {
        int mob = 100;
        int sharedLoot = 0;
        if (dropper != null) {
            mob = dropper.objectType;
            sharedLoot = dropper.playersRemainAtKill();
        }
        JLabel icon = new JLabel(ImageBuffer.getOutlinedIcon(mob, 20));
        String name = "Unknown";
        if (mob != 100) {
            name = IdToAsset.objectName(mob);
        }
        if (sharedLoot != 0) {
            name += "<br>Shared loot: " + sharedLoot + " players";
        }
        icon.setToolTipText("<html>" + name + "</html>");
        panel.add(icon);
    }

    private static void displayDungeonIcon(MapInfoPacket map, JPanel panel) {
        int dungeon = 100;
        String dungeonName = "Unknown";
        String dungeonModifiers = "";
        if (map != null) {
            dungeonName = map.name;
            dungeonModifiers = dungeonBuff(map.dungeonModifiers3);
            dungeon = ParseDungeon.getPortalId(dungeonName);
            if (dungeon == -1) {
                CharacterStatistics cs = CharacterStatistics.statByName(dungeonName);
                if (cs != null) {
                    dungeon = cs.getSpriteId();
                } else {
                    dungeon = 100;
                }
            }
        }

        JLabel icon = new JLabel(ImageBuffer.getOutlinedIcon(dungeon, 20));
        if (!dungeonModifiers.isEmpty()) {
            dungeonName += "<br>" + dungeonModifiers;
        }
        icon.setToolTipText("<html>" + dungeonName + "</html>");
        panel.add(icon);
    }

    public static String timeShort() {
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.now();
        return dateTimeFormat.format(dateTime);
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
            s.append(IdToAsset.objectName(statValue));
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

    public static void lootSharing(boolean b) {
        INSTANCE.disableLootSharing = b;
    }

//    private void updateFont(Component c) {
//        charPanel.removeAll();
//        for (ParsePanelGUI.Player p : playerDisplay.values()) {
//            p.panel = createMainBox(p, p.playerEntity);
//            charPanel.add(p.panel);
//        }
//    }
//
//    public static void editFont(Font font) {
//        mainFont = font;
//        INSTANCE.updateFont(charPanel);
//    }
}
