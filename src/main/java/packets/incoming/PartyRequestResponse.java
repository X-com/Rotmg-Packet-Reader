package packets.incoming;

import packets.Packet;
import packets.data.enums.InviteState;
import packets.data.enums.PartyActionType;
import packets.reader.BufferReader;

/**
 * Unknown packet -39 / 219
 */
public class PartyRequestResponse extends Packet {

    public String playerName;
    public short classId;
    public short skinId;
    public InviteState state;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        playerName = buffer.readString();
        classId = buffer.readShort();
        skinId = buffer.readShort();
        state = InviteState.byOrdinal(buffer.readByte());
    }

    @Override
    public String toString() {
        return "PartyRequestResponse{" +
                "\n   playerName=" + playerName +
                "\n   classId=" + classId +
                "\n   skinId=" + skinId +
                "\n   state=" + state;
    }
}
