package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

public class BuyEmotePacket extends Packet {

    public int emoteType;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        emoteType = buffer.readInt();
    }

    @Override
    public String toString() {
        return "BuyEmotePacket{" +
                "\n   emoteType=" + emoteType;
    }
}
