package packets.incoming;

import packets.Packet;
import packets.data.PartyPlayerData;
import packets.reader.BufferReader;

import java.util.Arrays;

/**
 * Unknown packet -46 / 210
 */
public class IncomingPartyMemberInfoPacket extends Packet {

    public int partyId;
    public short unknown;
    public byte maxSize;
    public PartyPlayerData[] partyPlayers;
    public String description;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        partyId = buffer.readInt();
        unknown = buffer.readShort();
        maxSize = buffer.readByte();
        partyPlayers = new PartyPlayerData[buffer.readShort()];
        for (int i = 0; i < partyPlayers.length; i++) {
            partyPlayers[i] = new PartyPlayerData().deserialize(buffer);
        }
        description = buffer.readString();
    }

    @Override
    public String toString() {
        return "IncomingPartyMemberInfo{" +
                "\n   partyId=" + partyId +
                "\n   unknown=" + unknown +
                "\n   maxSize=" + maxSize +
                "\n   partyPlayers=" + Arrays.toString(partyPlayers) +
                "\n   description=" + description;
    }
}
