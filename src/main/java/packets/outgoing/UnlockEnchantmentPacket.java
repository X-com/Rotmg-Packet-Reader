package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

public class UnlockEnchantmentPacket extends Packet {

    public short unknown;
    public short enchantmentType;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        unknown = buffer.readShort();
        enchantmentType = buffer.readShort();
    }

    @Override
    public String toString() {
        return "UnlockEnchantmentPacket{" +
                "\n   unknown=" + unknown +
                "\n   enchantmentType=" + enchantmentType;
    }
}
