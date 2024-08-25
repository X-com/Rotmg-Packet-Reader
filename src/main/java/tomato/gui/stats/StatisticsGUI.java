package tomato.gui.stats;

import tomato.backend.data.TomatoData;

import javax.swing.*;
import java.awt.*;

public class StatisticsGUI extends JPanel {

    public StatisticsGUI(TomatoData data) {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        add(tabbedPane);

        FameTrackerGUI fameTracker = new FameTrackerGUI();
        tabbedPane.addTab("Fame", fameTracker);
        LootGUI loot = new LootGUI(data);
        tabbedPane.addTab("Loot", loot);
        DungeonStats dungeonStats = new DungeonStats();
        tabbedPane.addTab("Dungeon Stats", dungeonStats);
    }
}
