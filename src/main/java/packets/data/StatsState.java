package packets.data;

import packets.reader.BufferReader;

public class StatsState {

    public byte hp;
    public byte mp;
    public byte attack;
    public byte defense;
    public byte speed;
    public byte vitality;
    public byte wisdom;
    public byte dexterity;

    public StatsState deserialize(BufferReader buffer) {
        hp = buffer.readByte();
        mp = buffer.readByte();
        attack = buffer.readByte();
        defense = buffer.readByte();
        speed = buffer.readByte();
        vitality = buffer.readByte();
        wisdom = buffer.readByte();
        dexterity = buffer.readByte();

        return this;
    }

    @Override
    public String toString() {
        return "StatsState{" +
                "\n   hp=" + hp +
                "\n   mp=" + mp +
                "\n   attack=" + attack +
                "\n   defense=" + defense +
                "\n   speed=" + speed +
                "\n   vitality=" + vitality +
                "\n   wisdom=" + wisdom +
                "\n   dexterity=" + dexterity;
    }
}
