package structures;

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
        for (int i = 0; i < data.length - 1; i++) {
            if (data[i] != 0) {
                //first entry of a DEntry is the SNode, second is the length of the DEntry
                i += data[i + 1]; //jumps to the end of a DEntry
            } else {
                if (data.length - 1 >= entry.getEntryLength()) {
                    byte[] entryInBytes = entry.turnIntoBytes();
                    System.arraycopy(entryInBytes, 0, data, i, entryInBytes.length);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

}
