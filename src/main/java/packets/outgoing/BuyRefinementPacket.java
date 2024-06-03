package packets.outgoing;

import packets.Packet;
import packets.data.SlotObjectData;
import packets.data.enums.RefineAction;
import packets.reader.BufferReader;

public class BuyRefinementPacket extends Packet {

    public SlotObjectData slotObjectData;
    public RefineAction action;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        slotObjectData = new SlotObjectData().deserialize(buffer);
        action = RefineAction.byOrdinal(buffer.readInt());
    }

    @Override
    public String toString() {
        return "BuyRefinementPacket{" +
                "\n   slotObjectData=" + slotObjectData +
                "\n   action=" + action;
    }
}
