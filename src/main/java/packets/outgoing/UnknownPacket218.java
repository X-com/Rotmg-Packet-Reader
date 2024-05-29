package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

/**
 * Unknown packet -38 / 218
 */
public class UnknownPacket218 extends Packet {

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        buffer.readBytes(buffer.getRemainingBytes());
    }
}
