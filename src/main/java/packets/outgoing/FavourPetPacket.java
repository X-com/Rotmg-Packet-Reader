package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

public class FavourPetPacket extends Packet {

    public int petId;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        petId = buffer.readInt();
    }

    @Override
    public String toString() {
        return "FavourPetPacket{" +
                "\n   petId=" + petId;
    }
}
