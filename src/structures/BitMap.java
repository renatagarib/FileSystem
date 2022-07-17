package structures;

import java.util.Arrays;

public class BitMap {
    byte[] elements;

    public BitMap(int size) {
        elements = new byte[size];
    }

    public void addElement(int location) {
        int place = location/8; //add element to the byte
        elements[place] |= (1 << (7 - location % 8));
    }

    public void clearElement(int location) {
        int place = location/8; //clear the element
        elements[place] &= ~(1 << (7 - location % 8));
    }

    public byte getElement(int location) {
        int place = location/8; //locate a element 
        return (byte)((elements[place] >> (7 - location % 8)) & 1);
    }

    public int findClearSpot() { //try to find a clear element
        for (int i = 0; i < (elements.length * 8); i++) {
            if (getElement(i) == 0) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        StringBuilder bitmap = new StringBuilder();

        for (int i = 0; i < (elements.length * 8); i++) {
            bitmap.append(i).append(": ");
            if (getElement(i) == 0) {
                bitmap.append("free/n");
            } else {
                bitmap.append("occupied/n");
            }
        }

        return bitmap.toString();
    }
}
