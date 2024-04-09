package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

/**
 * Ability altering packet
 * Unknown packet -99 / 157
 */
public class AbilityChange extends Packet {
    /**
     * Unknown
     */
    public byte unknownByte;
    /**
     * Game time
     */
    public int time;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        unknownByte = buffer.readByte();
        time = buffer.readInt();
    }

    @Override
    public String toString() {
        return "UnknownPacket157{" +
                "\n   unknownByte=" + unknownByte +
                "\n   time=" + time;
    }
}
