package structures;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class DataBlock {
    byte[] data;

    public DataBlock() {
        this.data = new byte[128];
    }

    public DataBlock(byte[] data) {
        this.data = data;
    }

    public boolean clear() {
        Arrays.fill(data, (byte) 0);
        return true;
    }

    public boolean addDEntry(DEntry entry) {
        for (int i = 0; i < data.length - 6; i++) {
            if (data[i] + data[i+1] != 0) {
                //first entry of a DEntry is the SNode (1st and 2nd bytes), second is the length of the DEntry (3rd and 4th bytes)
                //transform the 3rd and 4th bytes into a short to find out when the next entry start
                short nextEntry = turnBytesIntoShort(data[i+2], data[i+3]);
                //add the entryLength to i to jump to the next entry, subtract 1 because of the for loop
                i += nextEntry - 1; //jumps to the end of a DEntry
            } else {
                //checks if there is space for the entry
                if (data.length - i >= entry.getEntryLength()) {
                    //transform the dEntry in bytes
                    byte[] entryInBytes = entry.turnIntoBytes();
                    //insert into the vacant space in the array
                    System.arraycopy(entryInBytes, 0, data, i, entryInBytes.length);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    // DEntry:
    // 2 bytes -> sNode
    // 2 bytes -> entryLength
    // 1 byte  -> fileType
    // 1 byte  -> fileNameLength = x
    // x bytes -> fileName

    public int lookForDEntry(String filename) {
        for (int i = 2; i < data.length - 6; i++) {

            //checks if the fileNameLength if the same as the parameter length
            if (data[i+3] == filename.length()) {

                String name = turnFileNameIntoString(data[i+3], i+4);
                if (name.equals(filename)) {
                    return turnBytesIntoUnsignedInteger(data[i-2], data[i-1]);
                }

                short nextEntry = turnBytesIntoShort(data[i], data[i+1]);

                i += nextEntry - 1;
            }
        }
        return -1;
    }

    public short getDEntryLength(int sNode) {
        for (int i = 0; i < data.length-6; i++) {

            int entrySNode = turnBytesIntoUnsignedInteger(data[i], data[i+1]);

            short entryLength = turnBytesIntoShort(data[i+2], data[i+3]);

            if (entrySNode == sNode) {
                return entryLength;
            }
            i += entryLength - 1;
        }
        return -1;
    }

    public void deleteDEntry(int sNode, short length) {
        short entryLength = 1;
        byte[] newData = new byte[data.length];
        int deletedEntryPlace = -1;

        for (int i = 0; i < data.length - 6; i+=entryLength) {
            int entrySNode = turnBytesIntoUnsignedInteger(data[i], data[i+1]);

            if (entrySNode == sNode) {
                deletedEntryPlace = i + length;
                break;
            }

            entryLength = turnBytesIntoShort(data[i+2], data[i+3]);
            System.arraycopy(data, i, newData, i, entryLength);
        }

        for (int i = deletedEntryPlace; i < data.length - 6; i += entryLength) {
            entryLength = turnBytesIntoShort(data[i+2], data[i+3]);
            System.arraycopy(data, i, newData, i - length, entryLength);
            if (entryLength == 0)
                break;
        }

        data = newData;
    }

    // DEntry:
    // 2 bytes -> sNode
    // 2 bytes -> entryLength -> i, i+1
    // 1 byte  -> fileType -> i+2
    // 1 byte  -> fileNameLength -> i+3
    // x bytes -> fileName

    public String[] toStringArray() {
        ArrayList<String> entries = new ArrayList<>();

        for (int i = 2; i < data.length - 6; i++) {
            if (data[i-1] != 0 || data[i-2] != 0) {
                short entryLength = turnBytesIntoShort(data[i], data[i+1]);

                String name = turnFileNameIntoString(data[i+3], i+4);
                entries.add(name);

                i += entryLength -1;
            } else {
                return entries.toArray(new String[0]);
            }
        }
        return entries.toArray(new String[0]);
    }

    private int turnBytesIntoUnsignedInteger(byte n1, byte n2) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.put((byte) 0);
        bb.put((byte) 0);
        bb.put(n1);
        bb.put(n2);
        return bb.getInt(0);
    }

    private short turnBytesIntoShort(byte n1, byte n2) {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.put(n1);
        bb.put(n2);
        return bb.getShort(0);
    }

    private String turnFileNameIntoString(byte length, int startInData) {
        byte[] nameInBytes = new byte[length];
        int character = startInData;
        for (int j = 0; j < length; j++) {
            nameInBytes[j] = data[character];
            character ++;
        }
       return new String(nameInBytes, StandardCharsets.UTF_8);
    }
}
