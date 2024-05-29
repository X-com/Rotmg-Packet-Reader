package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

/**
 * Unknown packet -41 / 215
 */
public class PartyJoinRequest extends Packet {

    public int partyId;
    public byte unknown;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        partyId = buffer.readInt();
        unknown = buffer.readByte();
    }

    @Override
    public String toString() {
        return "PartyJoinRequest{" +
                "\n   partyId=" + partyId +
                "\n   unknown=" + unknown;
    }
}
