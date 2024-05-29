package packets.incoming;

import packets.Packet;
import packets.reader.BufferReader;

/**
 * Unknown packet -48 / 210
 */
public class IncomingPartyInvite extends Packet {

    public int partyId;
    public String inviterName;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        partyId = buffer.readInt();
        inviterName = buffer.readString();
    }

    @Override
    public String toString() {
        return "IncomingPartyInvite{" +
                "\n   partyId=" + partyId +
                "\n   inviterName=" + inviterName;
    }
}
