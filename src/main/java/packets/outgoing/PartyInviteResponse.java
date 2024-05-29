package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

/**
 * Unknown packet -47 / 209
 */
public class PartyInviteResponse extends Packet {

    public int partyId;
    public byte acceptInvite;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        partyId = buffer.readInt();
        acceptInvite = buffer.readByte();
    }

    @Override
    public String toString() {
        return "PartyInviteResponse{" +
                "\n   partyId=" + partyId +
                "\n   acceptInvite=" + acceptInvite;
    }
}
