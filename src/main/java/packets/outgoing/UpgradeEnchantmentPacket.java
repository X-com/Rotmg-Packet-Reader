package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

public class UpgradeEnchantmentPacket extends Packet {

    public short unknown;
    public byte slotIdx;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        unknown = buffer.readShort();
        slotIdx = buffer.readByte();
    }

    @Override
    public String toString() {
        return "UpgradeEnchantmentPacket{" +
                "\n   unknown=" + unknown +
                "\n   slotIdx=" + slotIdx;
    }
}
