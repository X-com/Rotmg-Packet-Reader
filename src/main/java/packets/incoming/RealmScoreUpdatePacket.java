package packets.incoming;

import packets.Packet;
import packets.reader.BufferReader;

public class RealmScoreUpdatePacket extends Packet {

    public int score;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        score = buffer.readInt();
    }

    @Override
    public String toString() {
        return "RealmScoreUpdatePacket{" +
                "\n   score=" + score;
    }
}
