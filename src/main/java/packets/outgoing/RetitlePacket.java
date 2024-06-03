package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

public class RetitlePacket extends Packet {

    public int prefix;
    public int suffix;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        prefix = buffer.readInt();
        suffix = buffer.readInt();
    }

    @Override
    public String toString() {
        return "RetitlePacket{" +
                "\n   prefix=" + prefix +
                "\n   suffix=" + suffix;
    }
}
