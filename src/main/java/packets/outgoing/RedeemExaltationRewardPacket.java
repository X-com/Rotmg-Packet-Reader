package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

public class RedeemExaltationRewardPacket extends Packet {
    /**
     * Item id redeemed
     */
    public int ItemId;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        ItemId = buffer.readInt();
    }

    @Override
    public String toString() {
        return "RedeemExaltationRewardPacket{" +
                "\n   ItemId=" + ItemId;
    }
}
