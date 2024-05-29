package packets.data.enums;

public enum InviteState {
    None(0),
    Pending(1),
    Cancelled(2),
    Accepted(3),
    Declined(4),
    PartyFull(5),
    Blackliste(6);

    private final int index;

    InviteState(int i) {
        index = i;
    }

    public int get() {
        return index;
    }

    public static InviteState byOrdinal(byte ord) {
        for (InviteState o : InviteState.values()) {
            if (o.index == ord) {
                return o;
            }
        }
        return null;
    }
}
