package packets.data;

import packets.reader.BufferReader;

import java.io.Serializable;

public class PartyPlayer implements Serializable {

    public short id;
    public String name;
    public short objectId;
    public short unknown;

    public PartyPlayer deserialize(BufferReader buffer) {
        id = buffer.readShort();
        name = buffer.readString();
        objectId = buffer.readShort();
        unknown = buffer.readShort();

        return this;
    }

    @Override
    public String toString() {
        return "PartyPlayer{" +
                "\n   id=" + id +
                "\n   name=" + name +
                "\n   objectId=" + objectId +
                "\n   unknown=" + unknown;
    }
}
