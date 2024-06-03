package packets.incoming;

import packets.Packet;
import packets.data.Party;
import packets.reader.BufferReader;

import java.util.Arrays;

/**
 * Unknown packet -42 / 214
 */
public class PartyListMessagePacket extends Packet {

    byte count;
    public Party[] parties;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        count = buffer.readByte();
        parties = new Party[buffer.readShort()];
        for (int i = 0; i < parties.length; i++)
            parties[i] = new Party().deserialize(buffer);
    }

    @Override
    public String toString() {
        return "PartyListMessage{" +
                "\n   count=" + count +
                "\n   parties=" + Arrays.toString(parties);
    }
}
