package packets.incoming;

import packets.Packet;
import packets.reader.BufferReader;

import java.util.Arrays;

/**
 * Sends all Character data, exalts and other info.
 */
public class NewCharacterInfoPacket extends Packet {

    public short size;
    public String characterXml;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        size = buffer.readShort();
        int i = buffer.getRemainingBytes();
        byte[] arr = buffer.readBytes(i);
        characterXml = new String(arr);
    }

    @Override
    public String toString() {
        return "NewCharacterInfoPacket{" +
                "\n   size=" + size +
                "\n   characterXml=" + characterXml;
    }
}