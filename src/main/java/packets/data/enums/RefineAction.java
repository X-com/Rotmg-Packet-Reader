package packets.data.enums;

public enum RefineAction {
    Upgrade(1),
    Downgrade(2),
    Reroll(3),
    Wipe(4);

    private final int index;

    RefineAction(int i) {
        index = i;
    }

    public static RefineAction byOrdinal(int ord) {
        for (RefineAction o : RefineAction.values()) {
            if (o.index == ord) {
                return o;
            }
        }
        return null;
    }
}
