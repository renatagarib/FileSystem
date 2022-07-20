package structures;

import byteManager.ByteManager;

import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class SNode {
    private FileType fileType;
    private byte generation;
    private long creationDate;
    private long modificationDate;
    private short length;
    private int[] dataBlocks;

    public SNode() {
        generation = 0;
    }

    public SNode(byte[] bytes) {

    }

    public void reUseSNode(FileType fileType, long creationDate, long modificationDate, short length, int[] dataBlocks) {
        this.fileType = fileType;
        this.generation ++;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
        this.length = length;
        this.dataBlocks = dataBlocks;

    }

    public void addDEntry(long date, short size) {
        this.modificationDate = date;
        this.length += size;
    }

    public void deleteDEntry(long date, short size) {
        this.modificationDate = date;
        this.length -= size;
    }

    public FileType getFileType() {
        return fileType;
    }

    public short getLength() {
        return length;
    }

    public int[] getDataBlocks() {
        return dataBlocks;
    }

    @Override
    public String toString() {
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

        return "File Type: " + fileType + "\n" +
                "Generation: " + generation + "\n" +
                "Creation Date: " + df.format(creationDate) + "\n" +
                "Modification Date: " + df.format(modificationDate) + "\n" +
                "Length: " + length + "\n" +
                "Data Blocks: " + Arrays.toString(dataBlocks);
    }


    public byte[] toByteArray() {
        // 1 byte  -> fileType          -> 0
        // 1 byte  -> generation        -> 1
        // 8 bytes -> creationDate      -> 2 - 9
        // 8 bytes -> modificationDate  -> 10 - 17
        // 2 bytes -> length            -> 18 - 19
        // 2 bytes -> dataBlock1        -> 20 - 21
        // 2 bytes -> dataBlock2        -> 22 - 23
        // 2 bytes -> dataBlock3        -> 24 - 25
        // 2 bytes -> dataBlock4        -> 26 - 27
        //total = 28 bytes

        byte[] inBytes = new byte[28];

        int placeInTheArray = 0;

        inBytes[placeInTheArray] = fileType.id();
        placeInTheArray ++;

        inBytes[placeInTheArray] = generation;
        placeInTheArray++;

        byte[] time = ByteManager.turnLongIntoBytes(creationDate);
        System.arraycopy(time, 0, inBytes, placeInTheArray, time.length);
        placeInTheArray += time.length;

        time = ByteManager.turnLongIntoBytes(modificationDate);
        System.arraycopy(time, 0, inBytes, placeInTheArray, time.length);
        placeInTheArray += time.length;

        byte[] lengthInBytes = ByteManager.turnShortIntoBytes(length);
        System.arraycopy(lengthInBytes, 0, inBytes, placeInTheArray, lengthInBytes.length);
        placeInTheArray += lengthInBytes.length;

        for (int i = 0; i < 4; i++) {
            if (i < dataBlocks.length) {
                byte[] dataBlockInBytes = ByteManager.turnIntegerIntoBytes(dataBlocks[i]);
                System.arraycopy(dataBlockInBytes, 0, inBytes, placeInTheArray, dataBlockInBytes.length);
                placeInTheArray += dataBlockInBytes.length;
            } else {
                byte[] missingDataBlockInBytes = ByteManager.turnIntegerIntoBytes(0);
                System.arraycopy(missingDataBlockInBytes, 0, inBytes, placeInTheArray, missingDataBlockInBytes.length);
                placeInTheArray += missingDataBlockInBytes.length;
            }
        }
        return inBytes;
    }
}
