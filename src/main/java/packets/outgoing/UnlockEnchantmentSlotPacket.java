package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

public class UnlockEnchantmentSlotPacket extends Packet {

    public short enchantmentId;
    public byte slotIdx;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        enchantmentId = buffer.readShort();
        slotIdx = buffer.readByte();
    }

    @Override
    public String toString() {
        return "UnlockEnchantmentSlotPacket{" +
                "\n   enchantmentId=" + enchantmentId +
                "\n   slotIdx=" + slotIdx;
    }
}
