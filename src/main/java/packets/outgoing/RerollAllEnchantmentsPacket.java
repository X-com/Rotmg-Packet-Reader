package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

public class RerollAllEnchantmentsPacket extends Packet {

    public short enchantmentId;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        enchantmentId = buffer.readShort();
    }

    @Override
    public String toString() {
        return "RerollAllEnchantmentsPacket{" +
                "\n   enchantmentId=" + enchantmentId;
    }
}
