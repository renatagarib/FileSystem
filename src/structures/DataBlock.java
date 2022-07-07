package structures;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
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
                ByteBuffer bb = ByteBuffer.allocate(2);
                bb.order(ByteOrder.BIG_ENDIAN);
                bb.put(data[i+2]);
                bb.put(data[i+3]);
                short nextEntry = bb.getShort(0);
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
                byte[] nameInBytes = new byte[data[i+3]];
                int character = i+4;
                for (int j = 0; j < data[i+3]; j++) {
                    nameInBytes[j] = data[character];
                    character ++;
                }

                String name = new String(nameInBytes, StandardCharsets.UTF_8);
                if (name.equals(filename)) {
                    ByteBuffer bb = ByteBuffer.allocate(4);
                    bb.order(ByteOrder.BIG_ENDIAN);
                    bb.put((byte) 0);
                    bb.put((byte) 0);
                    bb.put(data[i-2]);
                    bb.put(data[i-1]);
                    return bb.getInt(0);
                }

                ByteBuffer bb = ByteBuffer.allocate(2);
                bb.order(ByteOrder.BIG_ENDIAN);
                bb.put(data[i]);
                bb.put(data[i+1]);
                short nextEntry = bb.getShort(0);

                i += nextEntry - 1;
            }
        }
        return -1;
    }
}
