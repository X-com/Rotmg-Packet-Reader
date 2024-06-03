package packets.data;

import packets.reader.BufferReader;

public class ItemBuyData {

    public byte category;
    public int objectId;
    public byte currency;

    public ItemBuyData deserialize(BufferReader buffer) {
        category = buffer.readByte();
        objectId = buffer.readInt();
        currency = buffer.readByte();
        return this;
    }

    @Override
    public String toString() {
        return "ItemBuyData{" +
                "\n   category=" + category +
                "\n   objectId=" + objectId +
                "\n   currency=" + currency;
    }
}
