package tomato.gui.stats;

import javax.swing.*;
import java.awt.*;

public class StatisticsGUI extends JPanel {

    public StatisticsGUI() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        add(tabbedPane);

        FameTrackerGUI fameTracker = new FameTrackerGUI();
        tabbedPane.addTab("Fame", fameTracker);
        LootGUI loot = new LootGUI();
        tabbedPane.addTab("Loot", loot);
        DungeonStats dungeonStats = new DungeonStats();
        tabbedPane.addTab("Dungeon Stats", dungeonStats);
    }
}
