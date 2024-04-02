package packets.incoming;

import packets.Packet;
import packets.reader.BufferReader;

import java.util.Arrays;

/**
 * Unknown packet -75 / 181
 */
public class UnknownPacket181 extends Packet {

    //[0, 0, 0, 6, -75, 1]
    byte unknown;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        unknown = buffer.readByte();
    }

    @Override
    public String toString() {
        return "UnknownPacket181{" +
                "\n   unknown=" + unknown;
    }
}
