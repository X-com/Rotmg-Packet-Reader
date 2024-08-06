package packets.incoming;

import packets.Packet;
import packets.reader.BufferReader;

import java.util.Arrays;

/**
 * Received in response to the `HelloPacket`
 */
public class MapInfoPacket extends Packet {
    /**
     * The width of the map
     */
    public int width;
    /**
     * The height of the map
     */
    public int height;
    /**
     * The name of the map
     */
    public String name;
    /**
     * > Unknown.
     */
    public String displayName;
    /**
     * The name of the realm
     */
    public String realmName;
    /**
     * The difficulty rating of the map
     */
    public float difficulty;
    /**
     * The seed value for the client's PRNG
     */
    public long seed;
    /**
     * > Unknown
     */
    public int background;
    /**
     * Whether or not players can teleport in the map
     */
    public boolean allowPlayerTeleport;
    /**
     * > Unknown
     */
    public boolean showDisplays;
    /**
     * unknown
     */
    public boolean noSave;
    /**
     * The int of players allowed in this map
     */
    public short maxPlayers;
    /**
     * The time the connection to the game was started
     */
    public long gameOpenedTime;
    /**
     * Build version
     */
    public String buildVersion;
    /**
     * unknown
     */
    public int unknownInt;
    /**
     * Background color
     */
    public int BGColor;
    /**
     * String of all modifiers the dungeon has.
     */
    public String dungeonModifiers;
    public String dungeonModifiers2;
    public String dungeonModifiers3;
    /**
     * Max score of the realm
     */
    public int maxRealmScore;
    /**
     * Current score of the realm
     */
    public int currentRealmScore;
    /**
     * Unknown
     */
    public byte unknownByte;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        width = buffer.readInt();
        height = buffer.readInt();
        name = buffer.readString();
        displayName = buffer.readString();
        realmName = buffer.readString();
        seed = buffer.readUnsignedInt();
        background = buffer.readInt();
        difficulty = buffer.readFloat();
        allowPlayerTeleport = buffer.readBoolean();
        noSave = buffer.readBoolean();
        showDisplays = buffer.readBoolean();
        maxPlayers = buffer.readShort();
        gameOpenedTime = buffer.readUnsignedInt();
        buildVersion = buffer.readString();
        if (buffer.getRemainingBytes() > 0) {
            BGColor = buffer.readInt();
        }
        unknownByte = buffer.readByte();
        if (buffer.getRemainingBytes() > 0) {
            dungeonModifiers = buffer.readString();
            dungeonModifiers2 = buffer.readString();
            dungeonModifiers3 = buffer.readString();
        }
        if (buffer.getRemainingBytes() > 7) {
            maxRealmScore = buffer.readInt();
            currentRealmScore = buffer.readInt();
        }
        System.out.println(this);
    }

    @Override
    public String toString() {
        return "MapInfoPacket{" +
                "\n   width=" + width +
                "\n   height=" + height +
                "\n   name=" + name +
                "\n   displayName=" + displayName +
                "\n   realmName=" + realmName +
                "\n   difficulty=" + difficulty +
                "\n   seed=" + seed +
                "\n   background=" + background +
                "\n   allowPlayerTeleport=" + allowPlayerTeleport +
                "\n   showDisplays=" + showDisplays +
                "\n   noSave=" + noSave +
                "\n   maxPlayers=" + maxPlayers +
                "\n   gameOpenedTime=" + gameOpenedTime +
                "\n   buildVersion=" + buildVersion +
                "\n   BGColor=" + BGColor +
                "\n   unknownByte=" + unknownByte +
                "\n   dungeonModifiers=" + dungeonModifiers +
                "\n   dungeonModifiers2=" + dungeonModifiers2 +
                "\n   dungeonModifiers3=" + dungeonModifiers3 +
                "\n   maxRealmScore=" + maxRealmScore +
                "\n   currentRealmScore=" + currentRealmScore;
    }
}