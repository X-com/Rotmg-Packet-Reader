package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

import java.util.Arrays;

public class CrucibleRequestPacket extends Packet {

    public int[] type;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        type = new int[buffer.readShort()];
        for (int i = 0; i < type.length; i++) {
            type[i] = buffer.readInt();
        }
    }

    @Override
    public String toString() {
        return "CrucibleRequestPacket{" +
                "\n   type=" + Arrays.toString(type);
    }
}
