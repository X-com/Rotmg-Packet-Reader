package packets.incoming;

import packets.Packet;
import packets.reader.BufferReader;

/**
 * Unknown packet 190
 */
public class UnknownPacket190 extends Packet {
    /**
     * Unknown byte
     */
    public byte unknown;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        unknown = buffer.readByte();
    }

    @Override
    public String toString() {
        return "UnknownPacket190{" +
                "\n   unknown=" + unknown;
    }
}
