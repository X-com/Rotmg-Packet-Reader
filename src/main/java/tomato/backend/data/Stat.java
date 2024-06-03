package tomato.backend.data;

import packets.data.StatData;
import packets.data.enums.StatType;

import java.io.Serializable;

public class Stat implements Serializable {

    StatData[] stats = new StatData[200];

    public Stat() {
    }

    public Stat(StatData[] s) {
        setStats(s);
    }

    public void setStats(StatData[] stats) {
        for (StatData sd : stats) {
            stats[sd.statTypeNum] = sd;
        }
    }

    public StatData get(StatType type) {
        return stats[type.get()];
    }

    public void set(StatType type, StatData sd) {
        stats[type.get()] = sd;
    }
}
