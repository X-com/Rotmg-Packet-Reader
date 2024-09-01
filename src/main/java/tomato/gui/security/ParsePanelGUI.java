package tomato.gui.security;

import assets.IdToAsset;
import assets.ImageBuffer;
import packets.data.enums.StatType;
import tomato.backend.data.Entity;
import tomato.gui.SmartScroller;
import tomato.realmshark.ParseEnchants;
import tomato.realmshark.enums.CharacterClass;
import util.PropertiesManager;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class ParsePanelGUI extends JPanel {

    private static ParsePanelGUI INSTANCE;

    private static Color seasonalColor = new Color(21, 220, 166);
    private static Color redColor = new Color(140, 64, 64);

    private static JPanel charPanel;
    private static HashMap<Integer, Player> playerDisplay;
    private static Font mainFont;
    private static String[] missingStrings = new String[]{"HP", "MP", "Ak", "Df", "Sd", "Dx", "Vt", "Ws"};
    private static String[] missingStringsLonger = new String[]{"HP", "MP", "Atk", "Def", "Spd", "Dex", "Vit", "Wis"};
    private static String[] equipmentNames = {"weapon", "ability", "armor", "ring"};
    private static int[] exaltedSkinIds = {
            9497, //Rogue
            9499, //Archer
            9501, //Wizard
            9503, //Priest
            9505, //Warrior
            9507, //Knight
            9509, //Paladin
            9511, //Assassin
            9513, //Necromancer
            9515, //Huntress
            9519, //Trickster
            9517, //Mystic
            9521, //Sorcerer
            9523, //Ninja
            9525, //Samurai
            9527, //Bard
            30721, //Summoner
            31238, //Kensei
    };

    private static final String DISABLE_FILTER = "Default";
    private JComboBox<String> filterComboBox;

    private static TreeMap<String, SecurityFilter> filters = new TreeMap<>();
    private static SecurityFilter currentFilter = null;
    private static boolean guiUpdateSuppression = false;

    public ParsePanelGUI() {
        INSTANCE = this;
        setLayout(new BorderLayout());

        playerDisplay = new HashMap<>();
        charPanel = new JPanel();

        charPanel.setLayout(new BoxLayout(charPanel, BoxLayout.Y_AXIS));
        validate();

        JScrollPane scroll = new JScrollPane(charPanel);
        scroll.getVerticalScrollBar().setUnitIncrement(40);
        new SmartScroller(scroll, 0);
        add(scroll, BorderLayout.CENTER);

        JPanel top = new JPanel();
        JButton filterButton = new JButton("Filter");
        filterButton.addActionListener(this::filter);

        filterComboBox = new JComboBox<>(new String[]{DISABLE_FILTER});
        filterComboBox.setPreferredSize(new Dimension(10000, 0));
        filterComboBox.addActionListener(this::comboAction);

        loadFilters();

        top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
        top.add(Box.createHorizontalGlue());
        top.add(filterButton);
        top.add(Box.createRigidArea(new Dimension(10, 0)));
        top.add(filterComboBox);

        add(top, BorderLayout.NORTH);

        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(1, 2));
        JButton buttonLeft = new JButton("Copy names to Clipboard");
        JButton buttonRight = new JButton("Copy all to Clipboard");
        buttonLeft.addActionListener(e -> clicked(false));
        buttonRight.addActionListener(e -> clicked(true));
        buttons.add(buttonLeft);
        buttons.add(buttonRight);
        add(buttons, BorderLayout.SOUTH);
    }

    private void loadFilters() {
        String f = PropertiesManager.getProperty("securityFilters");
        if (f != null) {
            String[] split = f.split("§");
            for (String s : split) {
                SecurityFilter sf = SecurityFilter.loadJson(s);
                if (sf == null) continue;
                filters.put(sf.name, sf);
                filterComboBox.addItem(sf.name);
            }
        }

        String selectedItem = PropertiesManager.getProperty("securityFilterName");
        if (setupFilter(selectedItem)) {
            filterComboBox.setSelectedItem(selectedItem);
        }
    }

    private boolean setupFilter(String selectedItem) {
        if (selectedItem == null) return false;
        for (SecurityFilter sf : filters.values()) {
            if (sf.name.equals(selectedItem)) {
                currentFilter = sf;
                return true;
            }
        }
        return false;
    }

    private void comboAction(ActionEvent actionEvent) {
        if (guiUpdateSuppression) return;
        JComboBox<String> combo = (JComboBox<String>) actionEvent.getSource();
        String selectedItem = String.valueOf(combo.getSelectedItem());
        if (setupFilter(selectedItem)) {
            PropertiesManager.setProperties("securityFilterName", selectedItem);
        } else {
            currentFilter = null;
            PropertiesManager.setProperties("securityFilterName", "");
        }
        update();
    }

    private void filter(ActionEvent actionEvent) {
        SecurityFilterGUI.open(this);
    }

    private void clicked(boolean full) {
        StringBuilder sb = new StringBuilder();
        if (full) sb.append("[\n");
        boolean first = true;
        for (Player player : playerDisplay.values()) {
            if (full) {
                if (!first) {
                    sb.append(",").append("\n");
                }
                first = false;
                sb.append(player);
            } else {
                sb.append(player.playerEntity.name()).append(", ");
            }
        }
        if (full) sb.append("\n").append("]");
        copyToClipboard(String.valueOf(sb));
    }

    private static void copyToClipboard(String s) {
        StringSelection stringSelection = new StringSelection(s);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    private void guiUpdate() {
        revalidate();
        repaint();
    }

    private static JPanel createMainBox(Player p, Entity player) {
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.gray), BorderFactory.createEmptyBorder(0, 20, 0, 20)));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        BufferedImage ig = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = ig.createGraphics();
        FontMetrics fm = g2d.getFontMetrics(mainFont);
        int y = Math.max(24, fm.getHeight());
        int width = 70;

        p.inv[0] = player.stat.get(StatType.INVENTORY_0_STAT).statValue;
        p.inv[1] = player.stat.get(StatType.INVENTORY_1_STAT).statValue;
        p.inv[2] = player.stat.get(StatType.INVENTORY_2_STAT).statValue;
        p.inv[3] = player.stat.get(StatType.INVENTORY_3_STAT).statValue;
        int skinId = player.stat.get(StatType.SKIN_ID).statValue;
        if (skinId == 0) skinId = player.objectType;

        mainPanel.add(Box.createHorizontalGlue());

        int[] statsMissing = statMissing(player);
        {
            width = pointItems(p, player, statsMissing, mainPanel, fm, y, width, skinId);
        }
        mainPanel.add(Box.createHorizontalStrut(5));

        {
            width = itemIcons(p, player, mainPanel, width);
        }
        mainPanel.add(Box.createHorizontalStrut(5));

        {
            width = statsMaxed(player, statsMissing, mainPanel, fm, y, width);
        }
        mainPanel.add(Box.createHorizontalStrut(5));

        {
            width = seasonCrucibleIcon(p, player, mainPanel, width);
        }
        mainPanel.add(Box.createHorizontalStrut(5));

        {
            width = nameLabel(player, mainPanel, fm, y, width, skinId);
        }
        mainPanel.add(Box.createHorizontalStrut(5));

        {
            width = guildLabel(player, mainPanel, fm, y, width);
        }

        mainPanel.add(Box.createHorizontalGlue());

        mainPanel.setMaximumSize(new Dimension(width, y));

        g2d.dispose();

        return mainPanel;
    }

    private static int pointItems(Player p, Entity player, int[] statsMissing, JPanel mainPanel, FontMetrics fm, int y, int width, int skinId) {
        if (currentFilter == null) return width;
        JPanel panel = new JPanel();
        int x = fm.stringWidth("-- / --") + 2;
        width += x;

        panel.setPreferredSize(new Dimension(x, y));
        panel.setMaximumSize(new Dimension(x, y));
        panel.setLayout(new BorderLayout());

        String missing = "";
        for (int i = 0; i < currentFilter.statMaxed.length; i++) {
            if (currentFilter.statMaxed[i] && statsMissing[i] > 0) {
                missing = "Stats not maxed<br>";
                break;
            }
        }

        int point = 0;
        for (int eid : exaltedSkinIds) {
            if (skinId == eid) {
                point += currentFilter.exaltSkinPoints;
                break;
            }
        }

        int classPoint = currentFilter.classPoint.get(player.objectType);
        for (int i = 0; i < 4; i++) {
            int item = p.inv[i];
            Integer ip = currentFilter.itemPoint.get(item);
            if (ip == null) {
                missing += "Under reqed: " + equipmentNames[i] + "<br>";
            } else {
                point += ip;
            }
        }

        JLabel points = new JLabel(point + " / " + classPoint);
        if (!missing.isEmpty()) {
            points.setToolTipText("<html>" + missing + "</html>");
            panel.setBackground(redColor);
        } else {
            if (point < classPoint) {
                panel.setBackground(redColor);
            }
        }
        points.setHorizontalAlignment(SwingConstants.CENTER);
        points.setFont(mainFont);

        panel.add(points);
        mainPanel.add(panel);
        return width;
    }

    private static int statsMaxed(Entity player, int[] statsMissing, JPanel mainPanel, FontMetrics fm, int y, int width) {
        JPanel panel = new JPanel();
//            panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        int x = fm.stringWidth("8 / 8") + 2;
        width += x;

        panel.setPreferredSize(new Dimension(x, y));
        panel.setMaximumSize(new Dimension(x, y));
        panel.setLayout(new BorderLayout());

        int stat = statsMaxed(player);
        JLabel stats = new JLabel(stat + " / 8");
        String toolTipStatString = getToolTipStatString(statsMissing);
        stats.setToolTipText(String.valueOf(toolTipStatString));
        stats.setHorizontalAlignment(SwingConstants.RIGHT);
        stats.setFont(mainFont);

        panel.add(stats);
        mainPanel.add(panel);
        return width;
    }

    private static int itemIcons(Player p, Entity player, JPanel mainPanel, int width) {
        JPanel panel = new JPanel();
//            panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        width += 100;

        panel.setPreferredSize(new Dimension(100, 24));
        panel.setMaximumSize(new Dimension(100, 24));
        panel.setLayout(new GridLayout(1, 4));
        for (int i = 0; i < 4; i++) {
            int eq = p.inv[i];
            p.icon[i] = new JLabel(ImageBuffer.getOutlinedIcon(eq, 20));
            p.itemName[i] = IdToAsset.objectName(eq);
            panel.add(p.icon[i]);
        }
        p.updateToolTipText();
        mainPanel.add(panel);
        return width;
    }

    private static int guildLabel(Entity player, JPanel mainPanel, FontMetrics fm, int y, int width) {
        JPanel panel = new JPanel();
//            panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        int x = fm.stringWidth("12345678901234567890123") + 2;
        width += x;

        panel.setPreferredSize(new Dimension(x, y));
        panel.setMaximumSize(new Dimension(x, y));
        panel.setLayout(new BorderLayout());

        try {
            String text = player.getStatGuild();
            JLabel characterLabel = new JLabel(text, JLabel.CENTER);

            characterLabel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.isControlDown()) {
                        openWebpage("https://www.realmeye.com/guild/" + player.getStatGuild().replace(" ", "%20"));
                    }
                }
            });

            characterLabel.setAlignmentX(JLabel.LEFT);
            panel.setAlignmentX(JLabel.LEFT);
            panel.setAlignmentX(LEFT_ALIGNMENT);
            characterLabel.setHorizontalAlignment(SwingConstants.LEFT);
            characterLabel.setFont(mainFont);
            panel.add(characterLabel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mainPanel.add(panel);
        return width;
    }

    private static int nameLabel(Entity player, JPanel mainPanel, FontMetrics fm, int y, int width, int skinId) {
        JPanel panel = new JPanel();
//            panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        int x = fm.stringWidth("12345678901234567890123") + 2;
        width += x;

        panel.setPreferredSize(new Dimension(x, y));
        panel.setMaximumSize(new Dimension(x, y));
        panel.setLayout(new BorderLayout());

        int level = player.stat.get(StatType.LEVEL_STAT).statValue;
        String text = player.name() + " [" + level + "]";
        JLabel characterLabel = new JLabel(text, ImageBuffer.getOutlinedIcon(skinId, 20), JLabel.CENTER);
        characterLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.isControlDown()) {
                    openWebpage("https://www.realmeye.com/player/" + player.name());
                } else {
                    copyToClipboard(player.name());
                }
            }
        });

        characterLabel.setAlignmentX(JLabel.LEFT);
        panel.setAlignmentX(JLabel.LEFT);
        panel.setAlignmentX(LEFT_ALIGNMENT);
        characterLabel.setHorizontalAlignment(SwingConstants.LEFT);
        characterLabel.setFont(mainFont);
        panel.add(characterLabel);
//            characterLabel.setToolTipText(exaltStats(c));
        mainPanel.add(panel);
        return width;
    }

    private static int seasonCrucibleIcon(Player p, Entity player, JPanel mainPanel, int width) {
        JPanel panel = new JPanel();
        JPanel p1 = new JPanel();
        p.crucible = new JPanel();

        int x = 10;
        width += x;

        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        Color bg = player.isSeasonal() ? seasonalColor : Color.WHITE;
        p1.setBackground(bg);
        p.crucible.setBackground(player.isCrucible() ? Color.RED : bg);
        panel.add(p1);
        panel.add(p.crucible);
        panel.setLayout(new GridLayout(2, 1));

        panel.setPreferredSize(new Dimension(10, 10));
        panel.setMaximumSize(new Dimension(10, 10));
        mainPanel.add(panel);
        return width;
    }

    /**
     * Opens websight with given URL.
     *
     * @param url Opens website with specific URL.
     */
    private static void openWebpage(String url) {
        try {
            Desktop desktop = Desktop.getDesktop();
            URI uri = new URI(url);
            desktop.browse(uri);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Computes the missing pots needed to max the character.
     */
    public static int[] statMissing(Entity player) {
        int[] stats = new int[8];
        stats[0] = (int) Math.ceil((CharacterClass.getLife(player.objectType) - player.baseStats[0]) / 5.0);
        stats[1] = (int) Math.ceil((CharacterClass.getMana(player.objectType) - player.baseStats[1]) / 5.0);
        stats[2] = CharacterClass.getAtk(player.objectType) - player.baseStats[2];
        stats[3] = CharacterClass.getDef(player.objectType) - player.baseStats[3];
        stats[4] = CharacterClass.getSpd(player.objectType) - player.baseStats[4];
        stats[5] = CharacterClass.getDex(player.objectType) - player.baseStats[5];
        stats[6] = CharacterClass.getVit(player.objectType) - player.baseStats[6];
        stats[7] = CharacterClass.getWis(player.objectType) - player.baseStats[7];

        return stats;
    }

    /**
     * Gets the tool tip stats string from array of stats.
     *
     * @param stats Array of stats.
     * @return Stats as tooltip string.
     */
    private static String getToolTipStatString(int[] stats) {
        return String.format("<html>Missing<br>%d :Life<br>%d :Mana<br>%d :Atk<br>%d :Def<br>%d :Spd<br>%d :Dex<br>%d :Vit<br>%d :Wis</html>", stats[0], stats[1], stats[2], stats[3], stats[4], stats[5], stats[6], stats[7]);
    }

    /**
     * Gets the characters maxed stat count.
     */
    public static int statsMaxed(Entity player) {
        int outof8 = 0;
        if (CharacterClass.getLife(player.objectType) == player.baseStats[0]) outof8++;
        if (CharacterClass.getMana(player.objectType) == player.baseStats[1]) outof8++;
        if (CharacterClass.getAtk(player.objectType) == player.baseStats[2]) outof8++;
        if (CharacterClass.getDef(player.objectType) == player.baseStats[3]) outof8++;
        if (CharacterClass.getSpd(player.objectType) == player.baseStats[4]) outof8++;
        if (CharacterClass.getDex(player.objectType) == player.baseStats[5]) outof8++;
        if (CharacterClass.getVit(player.objectType) == player.baseStats[6]) outof8++;
        if (CharacterClass.getWis(player.objectType) == player.baseStats[7]) outof8++;

        return outof8;
    }

    public static void addPlayer(int id, Entity entity) {
        Player p = new Player();
        p.id = id;
        p.playerEntity = entity;
        p.panel = createMainBox(p, entity);
        playerDisplay.put(id, p);
        charPanel.add(p.panel);

        INSTANCE.guiUpdate();
    }

    public static void removePlayer(int dropId) {
        Player p = playerDisplay.remove(dropId);
        if (p != null) {
            charPanel.remove(p.panel);
            INSTANCE.guiUpdate();
        }
    }

    public static void update(int id, Entity entity) {
        Player player = playerDisplay.get(id);
        if (player != null) {
            player.update(entity);
        }
    }

    public static void update() {
        charPanel.removeAll();
        for (Player p : playerDisplay.values()) {
            p.panel = createMainBox(p, p.playerEntity);
            charPanel.add(p.panel);
        }
        INSTANCE.guiUpdate();
    }

    public static void editFont(Font font) {
        mainFont = font;
        INSTANCE.updateFont(charPanel);
    }

    public static void clear() {
        playerDisplay.clear();
        charPanel.removeAll();
        INSTANCE.guiUpdate();
    }

    private void updateFont(Component c) {
        charPanel.removeAll();
        for (Player p : playerDisplay.values()) {
            p.panel = createMainBox(p, p.playerEntity);
            charPanel.add(p.panel);
        }
    }

    TreeMap<String, SecurityFilter> getFilters() {
        return filters;
    }

    public void filterUpdate() {
        if (currentFilter != null && !filters.containsKey(currentFilter.name)) {
            currentFilter = null;
            filterComboBox.setSelectedItem(DISABLE_FILTER);
        }
        guiUpdateSuppression = true;
        filterComboBox.removeAllItems();
        filterComboBox.addItem(DISABLE_FILTER);
        guiUpdateSuppression = false;
        for (SecurityFilter sf : filters.values()) {
            filterComboBox.addItem(sf.name);
            if (currentFilter != null && sf.name.equals(currentFilter.name)) {
                filterComboBox.setSelectedItem(currentFilter.name);
            }
        }
    }

    public static class Player {
        int[] inv = new int[4];
        JLabel[] icon = new JLabel[4];
        String[] itemName = new String[4];
        int id;
        Entity playerEntity;
        JPanel panel;
        JPanel crucible;

        public void update(Entity player) {
            setIcon(0, player.stat.get(StatType.INVENTORY_0_STAT).statValue);
            setIcon(1, player.stat.get(StatType.INVENTORY_1_STAT).statValue);
            setIcon(2, player.stat.get(StatType.INVENTORY_2_STAT).statValue);
            setIcon(3, player.stat.get(StatType.INVENTORY_3_STAT).statValue);
            crucible.setBackground(player.isCrucible() ? Color.RED : player.isSeasonal() ? seasonalColor : Color.WHITE);
            updateToolTipText();
        }

        private void setIcon(int i, int eq) {
            if (inv[i] == eq) return;
            inv[i] = eq;

            try {
                icon[i].setIcon(ImageBuffer.getOutlinedIcon(eq, 20));
//                icon[i].setToolTipText(String.format("<html>%s<br>%s</html>", IdToAsset.objectName(eq), enchant));
                itemName[i] = IdToAsset.objectName(eq);
            } catch (Exception e) {
                e.printStackTrace();
            }
            INSTANCE.updateUI();
        }

        private void updateToolTipText() {
            String[] enchant = ParseEnchants.extractEnchants(playerEntity);

            for (int i = 0; i < 4; i++) {
                icon[i].setToolTipText(String.format("<html>%s<br>%s</html>", itemName[i], enchant[i]));
            }
        }

        public String toString() {
            int type = playerEntity.objectType;
            String clazz = CharacterClass.getName(type);
            int level = playerEntity.stat.get(StatType.LEVEL_STAT).statValue;
            boolean seasonal = playerEntity.isSeasonal();
            boolean crucible = playerEntity.isCrucible();
            int stat = statsMaxed(playerEntity);
            int[] missing = statMissing(playerEntity);

            StringBuilder sb = new StringBuilder();
            sb.append("\t{\n");
            sb.append("\t\t").append("\"name\":\"").append(playerEntity.name()).append("\",\n");
            sb.append("\t\t").append("\"class\":\"").append(clazz).append("\",\n");
            sb.append("\t\t").append("\"level\":").append(level).append(",\n");
            sb.append("\t\t").append("\"guild\":\"").append(playerEntity.getStatGuild()).append("\",\n");
            sb.append("\t\t").append("\"seasonal\":").append(seasonal ? "true" : "false").append(",\n");
            sb.append("\t\t").append("\"crucible\":").append(crucible ? "true" : "false").append(",\n");

            sb.append("\t\t").append("\"equipment\":{\n");
            for (int i = 0; i < 4; i++) {
                sb.append("\t\t\t").append("\"" + equipmentNames[i] + "\":\"").append(IdToAsset.objectName(inv[i])).append("\",\n");
                sb.append("\t\t\t").append("\"" + equipmentNames[i] + "id\":").append(inv[i]).append(i != 3 ? "," : "").append("\n");
            }
            sb.append("\t\t").append("},\n");

            sb.append("\t\t").append("\"maxstats\":").append(stat).append(",\n");
            sb.append("\t\t").append("\"missingstats\":{\n");
            ArrayList<String> l = new ArrayList<>();
            for (int i = 0; i < missing.length; i++) {
                if (missing[i] == 0) continue;
                l.add(String.format("\t\t\t\"%s\":%d", missingStringsLonger[i], missing[i]));
            }
            for (int i = 0; i < l.size(); i++) {
                sb.append(l.get(i)).append(i < l.size() - 1 ? "," : "").append("\n");
            }
            sb.append("\t\t").append("}\n");

            sb.append("\t}");
            return sb.toString();
        }
    }
}
