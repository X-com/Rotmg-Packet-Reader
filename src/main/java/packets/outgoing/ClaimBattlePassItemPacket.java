package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

/**
 * Packet sent when redeeming battle pass items
 */
public class ClaimBattlePassItemPacket extends Packet {
    public String unknownString;
    public int unknown;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        unknownString = buffer.readString();
        unknown = buffer.readByte();
    }

    @Override
    public String toString() {
        return "ClaimBattlePassItemPacket{" +
                "\n   unknownString=" + unknownString +
                "\n   unknown=" + unknown;
    }
}
