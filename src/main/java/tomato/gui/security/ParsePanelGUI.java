package tomato.gui.security;

import assets.AssetMissingException;
import assets.IdToAsset;
import assets.ImageBuffer;
import tomato.backend.data.Entity;
import tomato.gui.SmartScroller;
import tomato.realmshark.enums.CharacterClass;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class ParsePanelGUI extends JPanel {

    private static ParsePanelGUI INSTANCE;

    private static int HEIGHT = 24;

    private static JPanel charPanel;
    private static HashMap<Integer, Player> playerDisplay;
    private static Font mainFont;
    private static String[] missingStrings = new String[]{"HP", "MP", "Ak", "Df", "Sd", "Dx", "Vt", "Ws"};

    public ParsePanelGUI() {
        INSTANCE = this;
        setLayout(new BorderLayout());

        playerDisplay = new HashMap<>();
        charPanel = new JPanel();

        charPanel.setLayout(new BoxLayout(charPanel, BoxLayout.Y_AXIS));

        validate();

        JScrollPane scroll = new JScrollPane(charPanel);
        scroll.getVerticalScrollBar().setUnitIncrement(40);
        new SmartScroller(scroll);
        add(scroll, BorderLayout.CENTER);

        JButton button = new JButton("Copy to Clipboard");
        button.addActionListener(e -> clicked());
        add(button, BorderLayout.SOUTH);
    }

    private void clicked() {
        StringBuilder sb = new StringBuilder();
        for (Player player : playerDisplay.values()) {
            sb.append(player).append("\n");
        }
        StringSelection stringSelection = new StringSelection(sb.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    private void guiUpdate() {
        validate();
        repaint();
    }

    private static JPanel createMainBox(Player p, Entity player) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), BorderFactory.createEmptyBorder(0, 20, 0, 20)));
//        panel.setPreferredSize(new Dimension(370, HEIGHT));
//        panel.setMaximumSize(new Dimension(370, HEIGHT));
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        JPanel left = leftPanel(player);
        panel.add(left);

        JPanel inv = equipment(p, player);
        panel.add(inv);

        JPanel stat = rightPanel(player);
        panel.add(stat);

        return panel;
    }

    private static JPanel leftPanel(Entity player) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(110, HEIGHT));
        panel.setLayout(new BorderLayout());

        try {
            int eq = player.stat.SKIN_ID.statValue;
            if (eq == 0) eq = player.objectType;
            BufferedImage img = ImageBuffer.getImage(eq);
            ImageIcon icon = new ImageIcon(img.getScaledInstance(20, 20, Image.SCALE_DEFAULT));
            int level = player.stat.LEVEL_STAT.statValue;
            JLabel characterLabel = new JLabel(player.name() + " [" + level + "]", icon, JLabel.CENTER);
            characterLabel.setAlignmentX(JLabel.LEFT);
            panel.setAlignmentX(JLabel.LEFT);
            panel.setAlignmentX(LEFT_ALIGNMENT);
            characterLabel.setHorizontalAlignment(SwingConstants.LEFT);
            characterLabel.setFont(mainFont);
            panel.add(characterLabel);
//            characterLabel.setToolTipText(exaltStats(c));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return panel;
    }

    private static JPanel rightPanel(Entity player) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        int stat = statsMaxed(player);
        JLabel stats = new JLabel(stat + " / 8");
        int[] stats1 = statMissing(player);
        String toolTipStatString = getToolTipStatString(stats1);
        stats.setToolTipText(java.lang.String.valueOf(toolTipStatString));
        stats.setHorizontalAlignment(SwingConstants.RIGHT);
        stats.setFont(mainFont);

        panel.add(stats);

        return panel;
    }

    private static JPanel equipment(Player p, Entity player) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(100, HEIGHT));
        panel.setLayout(new GridLayout(1, 4));
//        panelEquip.setBorder(BorderFactory.createLineBorder(Color.black));
//        panelEquip.setPreferredSize(new Dimension(110, 33));
        p.inv[0] = player.stat.INVENTORY_0_STAT.statValue;
        p.inv[1] = player.stat.INVENTORY_1_STAT.statValue;
        p.inv[2] = player.stat.INVENTORY_2_STAT.statValue;
        p.inv[3] = player.stat.INVENTORY_3_STAT.statValue;
        for (int i = 0; i < 4; i++) {
            int eq = p.inv[i];
            try {
                BufferedImage img;
                if (eq == -1) {
                    img = ImageBuffer.getEmptyImg();
                } else {
                    img = ImageBuffer.getImage(eq);
                }
                p.icon[i] = new JLabel(new ImageIcon(img.getScaledInstance(20, 20, Image.SCALE_DEFAULT)));
                p.icon[i].setToolTipText(IdToAsset.objectName(eq));
                panel.add(p.icon[i]);
            } catch (Exception e) {
            }
        }

        return panel;
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
     * Gets the tool tip stats string from array of stats.
     *
     * @param missing Array of stats.
     * @return Stats as tooltip string.
     */
    private static String getMissingString(int[] missing) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            if (missing[i] != 0) sb.append(missingStrings[i]).append(":").append(missing[i]).append(" ");
        }
        return sb.toString();
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

    public static void clear() {
        playerDisplay.clear();
        charPanel.removeAll();
        INSTANCE.guiUpdate();
    }

    public static void editFont(Font font) {
        mainFont = font;
        INSTANCE.updateFont(charPanel);
    }

    private void updateFont(Component c) {
        if (c instanceof JPanel) {
            for (Component d : ((JPanel) c).getComponents()) {
                updateFont(d);
            }
        } else if (c instanceof JLabel) {
            c.setFont(mainFont);
        }
    }

    public static class Player {
        int[] inv = new int[4];
        JLabel[] icon = new JLabel[4];
        int id;
        Entity playerEntity;
        JPanel panel;

        public void update(Entity player) {
            setIcon(0, player.stat.INVENTORY_0_STAT.statValue);
            setIcon(1, player.stat.INVENTORY_1_STAT.statValue);
            setIcon(2, player.stat.INVENTORY_2_STAT.statValue);
            setIcon(3, player.stat.INVENTORY_3_STAT.statValue);
        }

        private void setIcon(int i, int eq) {
            if (inv[i] == eq) return;
            inv[i] = eq;
            try {
                BufferedImage img;
                if (eq == -1) {
                    img = ImageBuffer.getEmptyImg();
                } else {
                    img = ImageBuffer.getImage(eq);
                }
                icon[i].setIcon(new ImageIcon(img.getScaledInstance(20, 20, Image.SCALE_DEFAULT)));
                icon[i].setToolTipText(IdToAsset.objectName(eq));
            } catch (Exception e) {
            }
            INSTANCE.updateUI();
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            int level = playerEntity.stat.LEVEL_STAT.statValue;
            sb.append(playerEntity.name()).append(" (").append(level).append(") [");

            for (int i = 0; i < 4; i++) {
                try {
                    if (i != 0) sb.append(" / ");
                    sb.append(IdToAsset.objectName(inv[i]));
                } catch (AssetMissingException ignored) {
                }
            }
            sb.append("] [");

            int stat = statsMaxed(playerEntity);
            sb.append(stat).append(" / 8] ");
            int[] missing = statMissing(playerEntity);
            sb.append(getMissingString(missing));

            return sb.toString();
        }
    }
}
