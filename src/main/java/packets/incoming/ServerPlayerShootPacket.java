package packets.incoming;

import packets.Packet;
import packets.reader.BufferReader;
import packets.data.WorldPosData;

/**
 * Received when another player shoots
 */
public class ServerPlayerShootPacket extends Packet {
    /**
     * The id of the bullet that was produced
     */
    public short bulletId;
    /**
     * The object id of the player who fired the projectile
     */
    public int ownerId;
    /**
     * The item id of the weapon used to fire the projectile
     */
    public int containerType;
    /**
     * The starting position of the projectile
     */
    public WorldPosData startingPos;
    /**
     * The angle at which the projectile was fired
     */
    public float angle;
    /**
     * The damage which will be dealt by the projectile
     */
    public short damage;
    /**
     * Summoner id of the summoned entity shooting
     */
    public int summonerId;
    /**
     * Bullet type
     */
    public byte bulletType;
    /**
     * Number of bullets from the spell used
     */
    public byte bulletCount;
    /**
     * The angle between the two neighboring bullets shot from the spell
     */
    public float anglesBetweenBullets;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        bulletId = buffer.readShort();
        ownerId = buffer.readInt();
        containerType = buffer.readInt();
        startingPos = new WorldPosData().deserialize(buffer);
        angle = buffer.readFloat();
        damage = buffer.readShort();
        summonerId = buffer.readInt();

        if (buffer.getRemainingBytes() > 0) {
            bulletType = buffer.readByte();
            if (buffer.getRemainingBytes() > 0) {
                bulletCount = buffer.readByte();
                if (buffer.getRemainingBytes() > 0) {
                    anglesBetweenBullets = buffer.readFloat();
                }
            }
        }
    }

    @Override
    public String toString() {
        return "ServerPlayerShootPacket{" +
                "\n   bulletId=" + bulletId +
                "\n   ownerId=" + ownerId +
                "\n   containerType=" + containerType +
                "\n   startingPos=" + startingPos +
                "\n   angle=" + angle +
                "\n   damage=" + damage +
                "\n   summonerId=" + summonerId +
                "\n   bulletType=" + bulletType +
                "\n   bulletCount=" + bulletCount +
                "\n   anglesBetweenBullets=" + anglesBetweenBullets;
    }
}