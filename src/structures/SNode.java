package structures;

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

    public byte getGeneration() {
        return generation;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public long getModificationDate() {
        return modificationDate;
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
}
