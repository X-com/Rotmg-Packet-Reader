package potato.model.data;

import packets.data.ObjectData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Pylons {
    private static ArrayList<Integer> pylonIds;

    static {
        int[] PYLON_IDS = {
                52973,
                52974,
                52975,
                52976,
                52977,
                52978,
                52979,
                52980,
                52981,
                52982,
                52983,
                52984,
                52985,
                52986,
                52987,
                52988,
                52989,
        };

        pylonIds = new ArrayList<>();
        for (int id : PYLON_IDS) {
            pylonIds.add(id);
        }
    }

    public static float[] PYLONS = {
            477.5f, 865.5f,
            488.5f, 685.5f,
            545.5f, 1102.5f,
            605.5f, 1446.5f,
            671.5f, 1435.5f,
            686.5f, 1758.5f,
            758.5f, 1574.5f,
            778.5f, 1083.5f,
            806.5f, 511.5f,
            830.5f, 1261.5f,
            860.5f, 1694.5f,
            916.5f, 803.5f,
            930.5f, 1898.5f,
            960.5f, 433.5f,
            988.5f, 949.5f,
            1020.5f, 1075.5f,
            1031.5f, 1444.5f,
            1036.5f, 1683.5f,
            1047.5f, 595.5f,
            1059.5f, 1557.5f,
            1072.5f, 151.5f,
            1121.5f, 950.5f,
            1132.5f, 311.5f,
            1168.5f, 1810.5f,
            1172.5f, 1394.5f,
            1246.5f, 533.5f,
            1291.5f, 1038.5f,
            1341.5f, 1391.5f,
            1344.5f, 1533.5f,
            1436.5f, 1200.5f,
            1462.5f, 878.5f,
            1532.5f, 1369.5f,
            1620.5f, 723.5f,
            1670.5f, 1547.5f,
            1688.5f, 1275.5f,
            1718.5f, 1090.5f,
            1851.5f, 1249.5f,
            1864.5f, 1599.5f,
            1992.5f, 1257.5f,
    };

    public static void removePylon(ObjectData od, HashMap<Integer, Entity> entityList) {
        if (pylonIds.contains(od.objectType)) {
            for (Map.Entry<Integer, Entity> e : entityList.entrySet()) {
                if (e.getValue().x == od.status.pos.x) {
                    entityList.remove(e.getKey());
                    return;
                }
            }
        }
    }
}
