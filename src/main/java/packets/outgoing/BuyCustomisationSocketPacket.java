package packets.outgoing;

import packets.Packet;
import packets.data.ItemBuyData;
import packets.reader.BufferReader;

import java.util.Arrays;

public class BuyCustomisationSocketPacket extends Packet {

    public ItemBuyData[] items;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        items = new ItemBuyData[buffer.readShort()];
        for (int i = 0; i < items.length; i++) {
            items[i] = new ItemBuyData().deserialize(buffer);
        }
    }

    @Override
    public String toString() {
        return "BuyCustomisationSocketPacket{" +
                "\n   items=" + Arrays.toString(items);
    }
}
