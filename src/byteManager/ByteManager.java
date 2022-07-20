package byteManager;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class ByteManager {
    public static byte[] turnShortIntoBytes (short entry) {
        ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES);
        buffer.putShort(entry);
        //rewind it so the first byte is in the right
        buffer.rewind();
        return buffer.array();
    }

    public static short turnBytesIntoShort (byte n1, byte n2) {
        ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put(n1);
        buffer.put(n2);
        return buffer.getShort(0);
    }

    public static byte[] turnIntegerIntoBytes (int entry) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(entry);
        //rewind it so the first byte is in the right
        buffer.rewind();
        byte[] integerInBytes = buffer.array();

        //turn integer into unsigned integer
        return new byte[]{integerInBytes[2], integerInBytes[3]};
    }

    public static int turnBytesIntoUnsignedInteger(byte n1, byte n2) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put((byte) 0);
        buffer.put((byte) 0);
        buffer.put(n1);
        buffer.put(n2);
        return buffer.getInt(0);
    }

    public static byte[] turnLongIntoBytes(long entry) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(entry);
        buffer.rewind();
        return buffer.array();
    }

    public static long turnBytesIntoLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put(bytes);
        return buffer.getLong();
    }

    public static byte[] turnStringIntoBytes(String word) {
        return word.getBytes(StandardCharsets.UTF_8);
    }

    public static String turnBytesIntoString(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
