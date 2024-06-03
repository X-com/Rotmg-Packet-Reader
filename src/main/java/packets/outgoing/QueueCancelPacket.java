package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

/**
 * Sent when the clients position in the queue should be cancelled
 */
public class QueueCancelPacket extends Packet {

    public String guild;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        guild = buffer.readString();
    }

    @Override
    public String toString() {
        return "QueueCancelPacket{" +
                "\n   guild=" + guild;
    }
}