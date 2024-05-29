package packets.incoming;

import packets.Packet;
import packets.reader.BufferReader;

/**
 * Unknown packet -44 / 212
 */
public class PartyMemberAdded extends Packet {

    /**
     * Id of player that is invited
     */
    public int playerId;
    /**
     * Player name
     */
    public String name;
    /**
     * Player class Id
     */
    public int classId;
    /**
     * Player skin Id
     */
    public int skinId;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        playerId = buffer.readShort();
        name = buffer.readString();
        classId = buffer.readShort();
        skinId = buffer.readShort();
    }

    @Override
    public String toString() {
        return "UnknownPacket212{" +
                "\n   playerId=" + playerId +
                "\n   name=" + name +
                "\n   classId=" + classId +
                "\n   skinId=" + skinId;
    }
}
