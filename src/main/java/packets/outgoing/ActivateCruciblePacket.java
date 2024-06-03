package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

public class ActivateCruciblePacket extends Packet {

    public String crucibleId;
    public boolean activate;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        crucibleId = buffer.readString();
        activate = buffer.readBoolean();
    }

    @Override
    public String toString() {
        return "ActivateCruciblePacket{" +
                "\n   crucibleId=" + crucibleId +
                "\n   activate=" + activate;
    }
}
