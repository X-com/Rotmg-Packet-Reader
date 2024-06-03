package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

public class CreepHitPacket extends Packet {

    public int time;
    public short bulletId;
    public int objectId;
    public int targetId;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        time = buffer.readInt();
        bulletId = buffer.readShort();
        objectId = buffer.readInt();
        targetId = buffer.readInt();
    }

    @Override
    public String toString() {
        return "CreepHitPacket{" +
                "\n   time=" + time +
                "\n   bulletId=" + bulletId +
                "\n   objectId=" + objectId +
                "\n   targetId=" + targetId;
    }
}
