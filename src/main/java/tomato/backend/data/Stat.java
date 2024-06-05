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

    public void setStats(StatData[] sd) {
        for (StatData s : sd) {
            stats[s.statTypeNum] = s;
        }
    }

    public StatData get(StatType type) {
        return stats[type.get()];
    }

    public StatData get(int id) {
        return stats[id];
    }

    public void set(StatType type, StatData sd) {
        stats[type.get()] = sd;
    }
}
