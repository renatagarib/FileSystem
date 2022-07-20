package structures;

import java.util.Arrays;

public class BitMap {
    byte[] elements;

    public BitMap(int size) {
        elements = new byte[size];
    }

    public BitMap(byte[] bytes) {
        elements = bytes;
    }

    public void addElement(int location) {
        int place = location/8; //byte which the element will go;
        elements[place] |= (1 << (7 - location % 8)); //change to 1 the bit referent to the element
    }

    public void clearElement(int location) {
        int place = location/8; //byte which the element is;
        elements[place] &= ~(1 << (7 - location % 8));//change to 0 the bit referent to the element
    }

    public byte getElement(int location) {
        int place = location/8; //byte which the element is;
        return (byte)((elements[place] >> (7 - location % 8)) & 1);//return the value of the bit referent to the element
    }

    public int findClearSpot() { //try to find a clear element
        for (int i = 0; i < (elements.length * 8); i++) {
            if (getElement(i) == 0) {
                return i; //return the location
            }
        }
        return -1; //if there isn't any return -1
    }

    public int[] findClearSpots(int amount) { //try to find multiple clear elements
        int[] spots = new int[amount];

        int placeInTheArray = 0;
        for (int i = 0; i < (elements.length * 8); i++) {
            if (getElement(i) == 0) {
                spots[placeInTheArray] = i;
                placeInTheArray ++;

                if (placeInTheArray >= amount)
                    break;
            }
        }

        if (placeInTheArray != amount) {
            for (int i = 0; i < amount; i++) {
                spots[i] = -1;
            }
        }
        return spots;
    }

    public int getSize() {
        return elements.length;
    }


    @Override
    public String toString() {
        StringBuilder bitmap = new StringBuilder();

        for (int i = 0; i < (elements.length * 8); i++) {
            bitmap.append(i).append(": ");
            if (getElement(i) == 0) {
                bitmap.append("free\n");
            } else {
                bitmap.append("occupied\n");
            }
        }

        return bitmap.toString();
    }

    public byte[] toByteArray() {
        return elements;
    }
}
