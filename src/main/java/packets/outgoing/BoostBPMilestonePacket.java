package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

public class BoostBPMilestonePacket extends Packet {

    public byte milestoneCount;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        milestoneCount = buffer.readByte();
    }

    @Override
    public String toString() {
        return "BoostBPMilestonePacket{" +
                "\n   milestoneCount=" + milestoneCount;
    }
}
