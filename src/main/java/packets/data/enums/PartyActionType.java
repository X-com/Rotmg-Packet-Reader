package packets.data.enums;

import java.io.Serializable;

public enum PartyActionType implements Serializable {
    None(0),
    Failed(1),
    Kicked(2),
    KickNotFound(3),
    PromotedToLeader(4),
    PromoteNotFound(5),
    LeftParty(6);

    private final int index;

    PartyActionType(int i) {
        index = i;
    }

    public int get() {
        return index;
    }

    public static PartyActionType byOrdinal(byte ord) {
        for (PartyActionType o : PartyActionType.values()) {
            if (o.index == ord) {
                return o;
            }
        }
        return null;
    }
}
