package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

public class UpgradeEnchanterPacket extends Packet {

    public byte dustType;
    public byte currencyType;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        dustType = buffer.readByte();
        currencyType = buffer.readByte();
    }

    @Override
    public String toString() {
        return "UpgradeEnchanterPacket{" +
                "\n   dustType=" + dustType +
                "\n   currencyType=" + currencyType;
    }
}
