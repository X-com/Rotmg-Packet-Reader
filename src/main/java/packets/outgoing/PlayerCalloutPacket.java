package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

public class PlayerCalloutPacket extends Packet {

    public byte caloutType;
    public int objectId;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        caloutType = buffer.readByte();
        objectId = buffer.readInt();
    }

    @Override
    public String toString() {
        return "PlayerCalloutPacket{" +
                "\n   caloutType=" + caloutType +
                "\n   objectId=" + objectId;
    }
}
