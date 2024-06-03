package packets.incoming;

import packets.Packet;
import packets.data.WorldPosData;
import packets.reader.BufferReader;

/**
 * Received to tell the player to display an effect such as an AOE grenade
 */
public class ShowEffectPacket extends Packet {
    /**
     * The type of effect to display
     */
    public byte effectType;
    /**
     * The objectId the effect is targeting
     */
    public int targetObjectId;
    /**
     * > Unknown. Probably the start position of the effect
     */
    public WorldPosData pos1;
    /**
     * > Unknown. Probably the end position of the effect
     */
    public WorldPosData pos2;
    /**
     * The color of the effect
     */
    public int color;
    /**
     * The duration of the effect
     */
    public float duration;
    /**
     * unknown
     */
    public byte unknownByte;

    private static final int EFFECT_BIT_COLOR = 1;
    private static final int EFFECT_BIT_POS1X = 2;
    private static final int EFFECT_BIT_POS1Y = 4;
    private static final int EFFECT_BIT_POS2X = 8;
    private static final int EFFECT_BIT_POS2Y = 16;
    private static final int EFFECT_BIT_DURATION = 32;
    private static final int EFFECT_BIT_ID = 64;
    private static final int UNKNOWN_BIT_ID = 128;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        pos1 = new WorldPosData();
        pos2 = new WorldPosData();
        effectType = buffer.readByte();
        byte bitmask = buffer.readByte();

        if ((bitmask & EFFECT_BIT_ID) != 0) {
            targetObjectId = buffer.readCompressedInt();
        } else {
            targetObjectId = 0;
        }
        if ((bitmask & EFFECT_BIT_POS1X) != 0) {
            pos1.x = buffer.readFloat();
        } else {
            pos1.x = 0.0f;
        }
        if ((bitmask & EFFECT_BIT_POS1Y) != 0) {
            pos1.y = buffer.readFloat();
        } else {
            pos1.y = 0.0f;
        }
        if ((bitmask & EFFECT_BIT_POS2X) != 0) {
            pos2.x = buffer.readFloat();
        } else {
            pos2.x = 0.0f;
        }
        if ((bitmask & EFFECT_BIT_POS2Y) != 0) {
            pos2.y = buffer.readFloat();
        } else {
            pos2.y = 0.0f;
        }
        if ((bitmask & EFFECT_BIT_COLOR) != 0) {
            color = buffer.readInt();
        } else {
            color = 0xFFFFFF;
        }
        if ((bitmask & EFFECT_BIT_DURATION) != 0) {
            duration = buffer.readFloat();
        } else {
            duration = 1.0f;
        }
        if ((bitmask & UNKNOWN_BIT_ID) != 0) {
            unknownByte = buffer.readByte();
        } else {
            unknownByte = 100;
        }
    }

    @Override
    public String toString() {
        return "ShowEffectPacket{" +
                "\n   effectType=" + effectType +
                "\n   targetObjectId=" + targetObjectId +
                "\n   pos1=" + pos1 +
                "\n   pos2=" + pos2 +
                "\n   color=" + color +
                "\n   duration=" + duration +
                "\n   unknownByte=" + unknownByte;
    }
}