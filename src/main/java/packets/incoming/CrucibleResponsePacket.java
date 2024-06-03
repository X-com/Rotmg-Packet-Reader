package packets.incoming;

import packets.Packet;
import packets.reader.BufferReader;

import java.util.Arrays;

/**
 * Crucible Json String packet.
 */
public class CrucibleResponsePacket extends Packet {

    public int[] crucibleIds;
    public String[] crucibleJsons;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        crucibleIds = new int[buffer.readShort()];
        for (int i = 0; i < crucibleIds.length; i++) {
            crucibleIds[i] = buffer.readInt();
        }

        crucibleJsons = new String[buffer.readShort()];
        for (int i = 0; i < crucibleJsons.length; i++) {
            crucibleJsons[i] = buffer.readString();
        }
    }

    @Override
    public String toString() {
        return "CrucibleResponsePacket{" +
                "\n   crucibleIds=" + Arrays.toString(crucibleIds) +
                "\n   crucibleJsons=" + Arrays.toString(crucibleJsons);
    }
}
