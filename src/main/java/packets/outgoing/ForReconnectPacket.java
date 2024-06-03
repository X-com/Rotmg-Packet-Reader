package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

public class ForReconnectPacket extends Packet {

    public String reconnectInfo;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        reconnectInfo = buffer.readString();
    }
}
