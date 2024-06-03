package packets.incoming;

import packets.Packet;
import packets.data.SlotObjectData;
import packets.reader.BufferReader;

public class ClaimChestRewardPacket extends Packet {

    public boolean accepted;
    public SlotObjectData slotObjectData;
    public byte selectedIdx;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        accepted = buffer.readBoolean();
        slotObjectData = new SlotObjectData().deserialize(buffer);
        selectedIdx = buffer.readByte();
    }

    @Override
    public String toString() {
        return "ClaimChestRewardPacket{" +
                "\n   accepted=" + accepted +
                "\n   slotObjectData=" + slotObjectData +
                "\n   selectedIdx=" + selectedIdx;
    }
}