package packets.incoming;

import packets.Packet;
import packets.reader.BufferReader;

import java.util.Arrays;

/**
 * Unknown packet -75 / 181
 */
public class UnknownPacket181 extends Packet {

    //[0, 0, 0, 6, -75, 1]
    boolean unknown;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        unknown = buffer.readBoolean();
        System.out.println(this);
    }

    @Override
    public String toString() {
        return "UnknownPacket181{" +
                "\n   unknown=" + unknown;
    }
}
