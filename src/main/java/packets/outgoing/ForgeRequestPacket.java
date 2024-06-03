package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;
import packets.data.SlotObjectData;

import java.util.Arrays;

/**
 * Forge packet sent when forging.
 */
public class ForgeRequestPacket extends Packet {
    public int resultItemType;
    public SlotObjectData[] dismantledItems;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        resultItemType = buffer.readInt();

        dismantledItems = new SlotObjectData[buffer.readInt()];
        for (int i = 0; i < dismantledItems.length; i++) {
            dismantledItems[i] = new SlotObjectData().deserialize(buffer);
        }
    }

    @Override
    public String toString() {
        return "ForgeRequestPacket{" +
                "\n   resultItemType=" + resultItemType +
                "\n   dismantledItems=" + Arrays.toString(dismantledItems);
    }
}
