package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

/**
 * Sent in response to a `MapInfoPacket` to load a character into the map.
 */
public class LoadPacket extends Packet {
    /**
     * The id of the character to load.
     */
    public int charId;
    /**
     * Unknown boolean
     */
    public boolean isFromArena;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        charId = buffer.readInt();
        isFromArena = buffer.readBoolean();
    }

    @Override
    public String toString() {
        return "LoadPacket{" +
                "\n   charId=" + charId +
                "\n   isFromArena=" + isFromArena;
    }
}
