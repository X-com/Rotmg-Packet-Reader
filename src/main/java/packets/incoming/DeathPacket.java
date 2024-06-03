package packets.incoming;

import packets.Packet;
import packets.data.FameData;
import packets.reader.BufferReader;

import java.util.Arrays;

/**
 * Received when a player dies
 */
public class DeathPacket extends Packet {
    /**
     * The account id of the player who died
     */
    public String accountId;
    /**
     * The character id of the player who died
     */
    public int charId;
    /**
     * The cause of death
     */
    public String killedBy;
    /**
     * Gravestone type
     */
    public int gravestoneType;
    /**
     * Total death fame of the player
     */
    public int totalFame;
    /**
     * Death fame data
     */
    public FameData[] fameData;
    /**
     * Stats of the dead player
     */
    public String stats;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        accountId = buffer.readString();
        charId = buffer.readCompressedInt();
        killedBy = buffer.readString();
        gravestoneType = buffer.readInt();
        totalFame = buffer.readCompressedInt();
        fameData = new FameData[buffer.readCompressedInt()];
        for (int i = 0; i < fameData.length; i++) {
            fameData[i] = new FameData().deserialize(buffer);
        }
        stats = buffer.readString();
    }

    @Override
    public String toString() {
        return "DeathPacket{" +
                "\n   accountId=" + accountId +
                "\n   charId=" + charId +
                "\n   killedBy=" + killedBy +
                "\n   gravestoneType=" + gravestoneType +
                "\n   totalFame=" + totalFame +
                "\n   fameData=" + Arrays.toString(fameData) +
                "\n   stats=" + stats;
    }
}