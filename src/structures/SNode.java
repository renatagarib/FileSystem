package structures;

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

    public SNode(FileType fileType, long creationDate, long modificationDate, short length, int[] dataBlocks) {
        this.fileType = fileType;
        this.generation ++;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
        this.length = length;
        this.dataBlocks = dataBlocks;
    }

    public void reUseSNode(FileType fileType, long creationDate, long modificationDate, short length, int[] dataBlocks) {
        this.fileType = fileType;
        this.generation ++;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
        this.length = length;
        this.dataBlocks = dataBlocks;

    }

    public boolean addDEntry(long date, short size) {
        this.modificationDate = date;
        this.length += size;
        return true;
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
}
