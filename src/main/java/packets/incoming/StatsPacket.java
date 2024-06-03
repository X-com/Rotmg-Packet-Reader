package packets.incoming;

import packets.Packet;
import packets.data.StatsStateData;
import packets.reader.BufferReader;

public class StatsPacket extends Packet {

    public int charId;
    public StatsStateData state;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        charId = buffer.readCompressedInt();
        state = new StatsStateData().deserialize(buffer);
    }

    @Override
    public String toString() {
        return "StatsPacket{" +
                "\n   charId=" + charId +
                "\n   state=" + state;
    }
}
