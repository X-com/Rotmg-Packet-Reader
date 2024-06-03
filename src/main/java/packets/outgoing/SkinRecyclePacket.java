package packets.outgoing;

import packets.Packet;
import packets.data.SlotObjectData;
import packets.reader.BufferReader;

public class SkinRecyclePacket extends Packet {

    public SlotObjectData slotObject;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        slotObject = new SlotObjectData().deserialize(buffer);
    }

    @Override
    public String toString() {
        return "SkinRecyclePacket{" +
                "\n   slotObject=" + slotObject;
    }
}
