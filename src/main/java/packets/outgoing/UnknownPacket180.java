package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

import java.util.Arrays;

/**
 * Unknown packet -76 / 180
 */
public class UnknownPacket180 extends Packet {

    String unknownString;
    byte unknownByte;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        unknownString = buffer.readString();
        unknownByte = buffer.readByte();
    }

    @Override
    public String toString() {
        return "UnknownPacket180{" +
                "\n   unknownString=" + unknownString +
                "\n   unknownByte=" + unknownByte;
    }
}
