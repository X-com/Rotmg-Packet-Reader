package packets.incoming;

import packets.Packet;
import packets.reader.BufferReader;
import packets.data.enums.NotificationEffectType;

/**
 * Received when a notification is received by the player
 */
public class NotificationPacket extends Packet {
    /**
     * Notification effect type
     */
    public NotificationEffectType effect;
    /**
     * Unknown
     */
    public byte extra;
    /**
     * The object id of the object which this status is for
     */
    public int objectId;
    /**
     * The notification message
     */
    public String message;
    /**
     * ... no idea
     */
    public int uiExtra;
    /**
     * Queueing message type
     */
    public int realmQueueMessageType;
    /**
     * Position in the queue when queueing for servers
     */
    public int queuePos;
    /**
     * The color of the notification text
     */
    public int color;
    /**
     * The picture type of the notification
     */
    public int pictureType;
    /**
     * Player id of the player calling portal
     */
    public int senderObjectId;
    /**
     * Player stars
     */
    public int numberOfStars;
    /**
     * Progress bar max value
     */
    public int progressMax;
    /**
     * Progress bar value
     */
    public short progressValue;
    /**
     * Emote type
     */
    public int emoteType;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        effect = NotificationEffectType.byOrdinal(buffer.readByte());
        extra = buffer.readByte();

        switch (effect) {
            case StatIncrease:
            case ServerMessage:
            case ErrorMessage:
            case StickyMessage:
            case TeleportationError:
                message = buffer.readString();
                return;
            case Global:
                message = buffer.readString();
                uiExtra = buffer.readShort();
                return;
            case Queue:
                realmQueueMessageType = buffer.readInt();
                queuePos = buffer.readShort();
                return;
            case ObjectText:
                message = buffer.readString();
                objectId = buffer.readInt();
                color = buffer.readInt();
                return;
            case PlayerDeath:
            case PortalOpened:
                message = buffer.readString();
                pictureType = buffer.readInt();
                return;
            case PlayerCallout:
                message = buffer.readString();
                senderObjectId = buffer.readInt();
                numberOfStars = buffer.readShort();
                return;
            case ProgressBar:
                if (extra == 0) {
                    return;
                } else if ((extra & 3) != 0) {
                    message = buffer.readString();
                }
                progressMax = buffer.readInt();
                progressValue = buffer.readShort();
                return;
            case Behavior:
                message = buffer.readString();
                pictureType = buffer.readInt();
                color = buffer.readInt();
                return;
            case Emote:
                objectId = buffer.readInt();
                emoteType = buffer.readInt();
                return;
            default:
        }
    }

    @Override
    public String toString() {
        return "NotificationPacket{" +
                "\n   effect=" + effect +
                "\n   extra=" + extra +
                "\n   objectId=" + objectId +
                "\n   message=" + message +
                "\n   uiExtra=" + uiExtra +
                "\n   realmQueueMessageType=" + realmQueueMessageType +
                "\n   queuePos=" + queuePos +
                "\n   color=" + color +
                "\n   pictureType=" + pictureType +
                "\n   senderObjectId=" + senderObjectId +
                "\n   numberOfStars=" + numberOfStars +
                "\n   progressMax=" + progressMax +
                "\n   progressValue=" + progressValue +
                "\n   emoteType=" + emoteType;
    }
}