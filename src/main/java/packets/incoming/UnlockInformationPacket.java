package packets.incoming;

import packets.Packet;
import packets.reader.BufferReader;

/**
 * UnlockInformationPacket 109
 */
public class UnlockInformationPacket extends Packet {
    /**
     * Unknown
     */
    public int unlockType;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        unlockType = buffer.readInt();
    }

    @Override
    public String toString() {
        return "UnlockInformationPacket{" +
                "\n   unlockType=" + unlockType;
    }
}
