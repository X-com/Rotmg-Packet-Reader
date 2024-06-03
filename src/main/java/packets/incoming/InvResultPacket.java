package packets.incoming;

import packets.data.SlotObjectData;
import packets.Packet;
import packets.data.WorldPosData;
import packets.reader.BufferReader;

/**
 * > Unknown.
 */
public class InvResultPacket extends Packet {
    /**
     * Player time at the time of editing inventory
     */
    public boolean result;
    /**
     * Player positions at the time of editing inventory.
     */
    public byte resultType;
    /**
     * The slot the item in the inventory being transferred from.
     */
    public SlotObjectData slotFrom;
    /**
     * The slot the item in the inventory being transferred to.
     */
    public SlotObjectData slotTo;
    /**
     * Unknown Condition 1
     */
    public int condition1;
    /**
     * Unknown Condition 2
     */
    public int condition2;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        result = buffer.readBoolean();
        resultType = buffer.readByte();
        slotFrom = new SlotObjectData().deserialize(buffer);
        slotTo = new SlotObjectData().deserialize(buffer);
        condition1 = buffer.readInt();
        condition2 = buffer.readInt();
    }

    @Override
    public String toString() {
        return "InvResultPacket{" +
                "\n   result=" + result +
                "\n   resultType=" + resultType +
                "\n   slotFrom=" + slotFrom +
                "\n   slotTo=" + slotTo +
                "\n   condition1=" + condition1 +
                "\n   condition2=" + condition2;
    }
}