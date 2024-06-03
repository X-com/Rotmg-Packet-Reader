package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

/**
 * Sent to acknowledge a `GotoPacket`.
 */
public class GotoAckPacket extends Packet {
    /**
     * The current client time.
     */
    public int time;
    /**
     * Reset
     */
    private boolean reset;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        time = buffer.readInt();
        reset = buffer.readBoolean();
    }

    @Override
    public String toString() {
        return "GotoAckPacket{" +
                "\n   time=" + time +
                "\n   reset=" + reset;
    }
}
