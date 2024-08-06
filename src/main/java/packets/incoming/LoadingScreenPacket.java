package packets.incoming;

import packets.Packet;
import packets.reader.BufferReader;

/**
 * Displays loading screen when saving in vault
 */
public class LoadingScreenPacket extends Packet {
    /**
     * Is displaying loading screen
     */
    public boolean isLoading;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        isLoading = buffer.readBoolean();
    }

    @Override
    public String toString() {
        return "LoadingScreenPacket{" +
                "\n   isLoading=" + isLoading;
    }
}
