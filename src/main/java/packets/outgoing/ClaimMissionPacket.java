package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

public class ClaimMissionPacket extends Packet {

    public int seasonId;
    public byte missionPositionalIdx;
    public byte requestId;
    public short mask;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        seasonId = buffer.readInt();
        missionPositionalIdx = buffer.readByte();
        requestId = buffer.readByte();
        mask = buffer.readShort();
    }

    @Override
    public String toString() {
        return "ClaimMissionPacket{" +
                "\n   seasonId=" + seasonId +
                "\n   missionPositionalIdx=" + missionPositionalIdx +
                "\n   requestId=" + requestId +
                "\n   mask=" + mask;
    }
}
