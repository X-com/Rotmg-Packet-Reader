package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

public class SetGraveStonePacket extends Packet {

    public int graveStoneType;
    public int tier;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        graveStoneType = buffer.readInt();
        tier = buffer.readInt();
    }

    @Override
    public String toString() {
        return "SetGraveStonePacket{" +
                "\n   graveStoneType=" + graveStoneType +
                "\n   tier=" + tier;
    }
}
