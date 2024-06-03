package packets.incoming;

import packets.Packet;
import packets.data.PartyData;
import packets.reader.BufferReader;

import java.util.Arrays;

/**
 * Unknown packet -42 / 214
 */
public class PartyListMessagePacket extends Packet {

    byte count;
    public PartyData[] parties;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        count = buffer.readByte();
        parties = new PartyData[buffer.readShort()];
        for (int i = 0; i < parties.length; i++)
            parties[i] = new PartyData().deserialize(buffer);
    }

    @Override
    public String toString() {
        return "PartyListMessage{" +
                "\n   count=" + count +
                "\n   parties=" + Arrays.toString(parties);
    }
}
