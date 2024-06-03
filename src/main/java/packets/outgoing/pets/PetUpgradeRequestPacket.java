package packets.outgoing.pets;

import packets.Packet;
import packets.reader.BufferReader;
import packets.data.enums.PaymentType;
import packets.data.enums.PetUpgradeType;
import packets.data.SlotObjectData;

import java.util.Arrays;

/**
 * Sent when you are feeding and fusing pets or upgrading your pet yard
 */
public class PetUpgradeRequestPacket extends Packet {
    /**
     * The upgrade transaction type
     */
    public PetUpgradeType petTransType;
    /**
     * The object ID of the first pet
     */
    public int pIdOne;
    /**
     * The object ID of the second pet
     */
    public int pIdTwo;
    /**
     * The owner's object ID
     */
    public int objectId;
    /**
     * The item which will be used to upgrade the pet
     */
    public SlotObjectData slotObject;
    /**
     * The type of currency which will be used to purchase the upgrade
     */
    public PaymentType paymentType;

    @Override
    public void deserialize(BufferReader buffer) throws Exception {
        petTransType = PetUpgradeType.byOrdinal(buffer.readByte());
        pIdOne = buffer.readInt();
        pIdTwo = buffer.readInt();
        objectId = buffer.readInt();
        slotObject = new SlotObjectData().deserialize(buffer);
        paymentType = PaymentType.byOrdinal(buffer.readByte());
    }

    @Override
    public String toString() {
        return "PetUpgradeRequestPacket{" +
                "\n   petTransType=" + petTransType +
                "\n   pIdOne=" + pIdOne +
                "\n   pIdTwo=" + pIdTwo +
                "\n   objectId=" + objectId +
                "\n   slotObject=" + slotObject +
                "\n   paymentType=" + paymentType;
    }
}