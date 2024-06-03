package packets.outgoing;

import packets.Packet;
import packets.data.enums.PartyActionType;
import packets.reader.BufferReader;

/**
 * Party action response packet -49 / 207
 */
public class PartyActionPacket extends Packet {

    public int playerId;
    public PartyActionType actionId;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        playerId = buffer.readShort();
        actionId = PartyActionType.byOrdinal(buffer.readByte());
    }

    @Override
    public String toString() {
        return "PartyAction{" +
                "\n   playerId=" + playerId +
                "\n   actionId=" + actionId;
    }
}
