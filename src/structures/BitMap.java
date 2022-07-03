package structures;

public class BitMap {
    byte[] elements;

    public BitMap(int size) {
        elements = new byte[size];
    }

    public void addElement(int location) {
        int place = location/8;
        elements[place] |= (1 << (7 - location % 8));
    }

    public void clearElement(int location) {
        int place = location/8;
        elements[place] &= ~(1 << (7 - location % 8));
    }

    public byte getElement(int location) {
        int place = location/8;
        return (byte)((elements[place] >> (7 - location % 8)) & 1);
    }

    public int findClearSpot() {
        for (int i = 0; i < (elements.length * 8); i++) {
            if (getElement(i) == 0) {
                return i;
            }
        }
        return -1;
    }
}
