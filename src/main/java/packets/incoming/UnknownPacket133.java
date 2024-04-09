package packets.incoming;

import packets.Packet;
import packets.reader.BufferReader;

/**
 * Unknown packet -123 / 133
 */
public class UnknownPacket133 extends Packet {
    /**
     * Unknown
     */
    public int unknownInt1;
    public int unknownInt2;
    public int unknownInt3;
    public short unknownShort;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        Thread.dumpStack();

        unknownInt1 = buffer.readInt();
        unknownInt2 = buffer.readInt();
        unknownInt3 = buffer.readInt();
        unknownShort = buffer.readShort();
    }

    @Override
    public String toString() {
        return "UnknownPacket133{" +
                "\n   unknownInt1=" + unknownInt1 +
                "\n   unknownInt2=" + unknownInt2 +
                "\n   unknownInt3=" + unknownInt3 +
                "\n   unknownShort=" + unknownShort;
    }
}
