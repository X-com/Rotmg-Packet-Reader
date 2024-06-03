package packets.incoming;

import packets.Packet;
import packets.reader.BufferReader;

/**
 * Received to instruct the client to connect to a new host
 */
public class ReconnectPacket extends Packet {
    /**
     * The name of the new host.
     */
    public String name;
    /**
     * The address of the new host
     */
    public String host;
    /**
     * The port of the new host
     */
    public int port;
    /**
     * Game ID
     */
    public int gameId;
    /**
     * The `gameId` to send in the next `HelloPacket`
     */
    public int keyTime;
    /**
     * The `key` to send in the next `HelloPacket`
     */
    public byte[] key;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        name = buffer.readString();
        host = buffer.readString();
        port = buffer.readUnsignedShort();
        gameId = buffer.readInt();
        keyTime = buffer.readInt();
        key = buffer.readByteArray();
    }
}