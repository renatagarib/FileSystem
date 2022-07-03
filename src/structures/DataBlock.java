package structures;

import java.util.ArrayList;
import java.util.Arrays;

public class DataBlock {
    byte[] data;
    ArrayList<DEntry> dEntries = new ArrayList<>();

    public DataBlock() {
        this.data = new byte[128];
    }

    public DataBlock(byte[] data) {
        this.data = data;
    }

    public boolean clear() {
        Arrays.fill(data, (byte) 0);
        dEntries.clear();
        return true;
    }

    public boolean addDEntry(DEntry dEntry) {
        dEntries.add(dEntry);

    }
}
