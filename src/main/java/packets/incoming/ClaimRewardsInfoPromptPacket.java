package packets.incoming;

import packets.Packet;
import packets.reader.BufferReader;

import java.util.Arrays;

public class ClaimRewardsInfoPromptPacket extends Packet {

    public byte chestType;
    public int[] contents;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        chestType = buffer.readByte();
        contents = new int[buffer.readShort()];
        for (int i = 0; i < contents.length; i++) {
            contents[i] = buffer.readInt();
        }
    }

    @Override
    public String toString() {
        return "ClaimRewardsInfoPromptPacket{" +
                "\n   chestType=" + chestType +
                "\n   contents=" + Arrays.toString(contents);
    }
}
