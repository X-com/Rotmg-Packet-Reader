package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

public class ResetEnchantmentRerollCountPacket extends Packet {

    public short enchantmentId;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        enchantmentId = buffer.readShort();
    }

    @Override
    public String toString() {
        return "ResetEnchantmentRerollCountPacket{" +
                "\n   enchantmentId=" + enchantmentId;
    }
}
