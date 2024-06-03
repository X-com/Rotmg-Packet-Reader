package packets.incoming;

import packets.Packet;
import packets.reader.BufferReader;

/**
 * > Unknown
 */
public class NewCharacterInfoPacket extends Packet {

    public String characterXml;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        characterXml = buffer.readString();
    }

    @Override
    public String toString() {
        return "NewCharacterInfoPacket{" +
                "\n   characterXml=" + characterXml;
    }
}