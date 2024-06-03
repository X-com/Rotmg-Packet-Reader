package packets.outgoing;

import packets.Packet;
import packets.reader.BufferReader;

public class CreatePartyMessagePacket extends Packet {

    public String description;
    public short powerlevel;
    public byte partySize;
    public byte activity;
    public byte maxedStats;
    public byte serverdropdownlist;
    public byte privacy;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        description = buffer.readString();
        powerlevel = buffer.readShort();
        partySize = buffer.readByte();
        activity = buffer.readByte();
        maxedStats = buffer.readByte();
        serverdropdownlist = buffer.readByte();
        privacy = buffer.readByte();
    }

    @Override
    public String toString() {
        return "CreatePartyMessagePacket{" +
                "\n   description=" + description +
                "\n   powerlevel=" + powerlevel +
                "\n   partySize=" + partySize +
                "\n   activity=" + activity +
                "\n   maxedStats=" + maxedStats +
                "\n   serverdropdownlist=" + serverdropdownlist +
                "\n   privacy=" + privacy;
    }
}
