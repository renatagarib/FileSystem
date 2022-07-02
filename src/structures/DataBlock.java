package structures;

import java.util.Arrays;

public class DataBlock {
    byte[] data;

    public DataBlock() {
        this.data = new byte[128];
    }

    public void clear() {
        Arrays.fill(data, (byte) 0);
    }
}
