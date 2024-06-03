package packets.outgoing;

import packets.Packet;
import packets.data.WorldPosData;
import packets.reader.BufferReader;

/**
 * Packet confirmed Kensei dash to specific coordinates
 */
public class DashPacket extends Packet {
    public int time;
    public WorldPosData start;
    public WorldPosData end;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        time = buffer.readInt();
        start = new WorldPosData().deserialize(buffer);
        end = new WorldPosData().deserialize(buffer);
    }

    @Override
    public String toString() {
        return "DashPacket{" +
                "\n   time=" + time +
                "\n   start=" + start +
                "\n   end=" + end;
    }
}
