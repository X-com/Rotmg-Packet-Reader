package packets.incoming;

import packets.Packet;
import packets.data.enums.PartyActionType;
import packets.reader.BufferReader;

/**
 * Unknown packet -52 / 204
 */
public class PartyActionResultPacket extends Packet {

    public int playerId;
    public PartyActionType actionId;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        playerId = buffer.readShort();
        actionId = PartyActionType.byOrdinal(buffer.readByte());
    }

    @Override
    public String toString() {
        return "PartyActionResult{" +
                "\n   playerId=" + playerId +
                "\n   actionId=" + actionId;
    }
}
