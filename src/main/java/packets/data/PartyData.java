package packets.data;

import packets.reader.BufferReader;

import java.io.Serializable;

public class PartyData implements Serializable {

    public String descripition;
    public int partyId;
    public short powerlevel;
    public byte currentPlayers;
    public byte maxPlayers;
    public byte partyType;
    public byte privacy; //1 = public 2 = private
    public byte requiredMaxedStats;
    public byte server;

    public PartyData deserialize(BufferReader buffer) {
        descripition = buffer.readString();
        partyId = buffer.readInt();
        powerlevel = buffer.readShort();
        currentPlayers = buffer.readByte();
        maxPlayers = buffer.readByte();
        partyType = buffer.readByte();
        privacy = buffer.readByte();
        requiredMaxedStats = buffer.readByte();
        server = buffer.readByte();

        return this;
    }
}
