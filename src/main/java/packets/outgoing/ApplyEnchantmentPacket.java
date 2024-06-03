package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

public class ApplyEnchantmentPacket extends Packet {

    public short unknown;
    public short enchantmentType;
    public boolean add;
    public byte enchantmentSlotIdx;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        unknown = buffer.readShort();
        enchantmentType = buffer.readShort();
        add = buffer.readBoolean();
        enchantmentSlotIdx = buffer.readByte();
    }

    @Override
    public String toString() {
        return "ApplyEnchantmentPacket{" +
                "\n   unknown=" + unknown +
                "\n   enchantmentType=" + enchantmentType +
                "\n   add=" + add +
                "\n   enchantmentSlotIdx=" + enchantmentSlotIdx;
    }
}
