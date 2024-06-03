package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

public class AEmpty extends Packet {

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
    }
}
