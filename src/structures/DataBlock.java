package structures;

import byteManager.ByteManager;

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
        Arrays.fill(data, (byte) 0);//turn all bytes in the data block to 0
        return true;
    }

    public boolean addDEntry(DEntry entry) {
        for (int i = 0; i < data.length - 6; i++) {
            if (data[i] + data[i+1] != 0) {
                //first entry of a DEntry is the SNode (1st and 2nd bytes), second is the length of the DEntry (3rd and 4th bytes)
                //transform the 3rd and 4th bytes into a short to find out when the next entry start
                short nextEntry = ByteManager.turnBytesIntoShort(data[i+2], data[i+3]);
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
    // 2 bytes -> sNode = i i+1
    // 2 bytes -> entryLength = i+2, i+3
    // 1 byte  -> fileType = i+4
    // 1 byte  -> fileNameLength = i+5
    // x bytes -> fileName i+6

    public int lookForDEntry(String filename) {
        short nextEntry = 1;
        for (int i = 0; i < data.length - 6; i += nextEntry) {
            //checks if the fileNameLength is the same as the parameter length
            if (data[i+5] == filename.length()) {

                //turns the fileName into a string
                String name = turnFileNameIntoString(data[i+5], i+6);
                //compares to the parameter
                if (name.equals(filename)) {
                    return ByteManager.turnBytesIntoUnsignedInteger(data[i], data[i+1]);
                }
            }
            //goes to the next entry
            nextEntry = ByteManager.turnBytesIntoShort(data[i+2], data[i+3]);
            if (nextEntry == 0) // if there isn't any, breaks
                break;
        }
        return -1; // return -1 if the file isn't found
    }

    public short getDEntryLength(int sNode) {
        for (int i = 0; i < data.length-6; i++) {

            int entrySNode = ByteManager.turnBytesIntoUnsignedInteger(data[i], data[i+1]);

            short entryLength = ByteManager.turnBytesIntoShort(data[i+2], data[i+3]);

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

        //find the entry to be deleted
        for (int i = 0; i < data.length - 6; i+=entryLength) {
            int entrySNode = ByteManager.turnBytesIntoUnsignedInteger(data[i], data[i+1]);

            if (entrySNode == sNode) {
                //get the entry in the array
                deletedEntryPlace = i + length;
                break;
            }

            //get the next entry
            entryLength = ByteManager.turnBytesIntoShort(data[i+2], data[i+3]);
            System.arraycopy(data, i, newData, i, entryLength);
        }

        //delete the entry from the array
        for (int i = deletedEntryPlace; i < data.length - 6; i += entryLength) {
            entryLength = ByteManager.turnBytesIntoShort(data[i+2], data[i+3]);
            System.arraycopy(data, i, newData, i - length, entryLength);
            if (entryLength == 0)
                break;
        }

        data = newData;
    }



    public String[] toStringArray() {
        // DEntry:
        // 2 bytes -> sNode -> i, i+1
        // 2 bytes -> entryLength -> i+2, i+3
        // 1 byte  -> fileType -> i+4
        // 1 byte  -> fileNameLength -> i+5
        // x bytes -> fileName

        ArrayList<String> entries = new ArrayList<>();
        short entryLength;

        for (int i = 0; i < data.length - 6; i += entryLength) {
            entryLength = ByteManager.turnBytesIntoShort(data[i+2], data[i+3]);
            if (entryLength != 0) {
                String name = turnFileNameIntoString(data[i+5], i+6);
                entries.add(name);
            } else {
                return entries.toArray(new String[0]);
            }
        }
        return entries.toArray(new String[0]);
    }

    public byte[] toByteArray() {
        return data;
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
