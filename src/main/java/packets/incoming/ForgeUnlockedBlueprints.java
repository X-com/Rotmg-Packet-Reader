package packets.incoming;

import packets.Packet;
import packets.reader.BufferReader;

import java.util.Arrays;

/**
 * Received when the player enters the nexus
 */
public class ForgeUnlockedBlueprints extends Packet {
    /**
     * Seasonal forge
     */
    public byte seasonalForge;
    /**
     * The itemIds of unlocked blueprints in an array
     */
    public int[] unlockedBlueprints;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        seasonalForge = buffer.readByte();
        unlockedBlueprints = new int[buffer.readCompressedInt()];
        for (int i = 0; i < unlockedBlueprints.length; i++) {
            unlockedBlueprints[i] = buffer.readCompressedInt();
        }
    }

    @Override
    public String toString() {
        return "ForgeUnlockedBlueprints{" +
                "\n   unknownByte=" + seasonalForge +
                "\n   unlockedBlueprints=" + Arrays.toString(unlockedBlueprints);
    }
}