package packets.incoming;

import packets.Packet;
import packets.data.StatsState;
import packets.reader.BufferReader;

public class StatsPacket extends Packet {

    public int charId;
    public StatsState state;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        charId = buffer.readCompressedInt();
        state = new StatsState().deserialize(buffer);
    }

    @Override
    public String toString() {
        return "StatsPacket{" +
                "\n   charId=" + charId +
                "\n   state=" + state;
    }
}
