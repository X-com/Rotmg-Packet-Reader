package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

public class CustomMapListPacket extends Packet {

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
    }

    @Override
    public String toString() {
        return "CustomMapListPacket{}";
    }
}
