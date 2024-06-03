package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

public class SetTrackedSeasonPacket extends Packet {

    public int seasonId;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        seasonId = buffer.readInt();
    }

    @Override
    public String toString() {
        return "SetTrackedSeasonPacket{" +
                "\n   seasonId=" + seasonId;
    }
}
