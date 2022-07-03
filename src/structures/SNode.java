package structures;

public class SNode {
    private FileType fileType;
    private byte generation;
    private long creationDate;
    private long modificationDate;
    private short length;
    private int dataBlock_01;
    private int dataBlock_02;
    private int dataBlock_03;
    private int dataBlock_04;

    public SNode(FileType fileType, long creationDate, long modificationDate, short length, int dataBlock_01) {
        this.fileType = fileType;
        this.generation = 0;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
        this.length = length;
        this.dataBlock_01 = dataBlock_01;
    }

    public SNode(FileType fileType, byte generation, long creationDate, long modificationDate, short length, int dataBlock_01, int dataBlock_02) {
        this.fileType = fileType;
        this.generation = generation;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
        this.length = length;
        this.dataBlock_01 = dataBlock_01;
        this.dataBlock_02 = dataBlock_02;
    }

    public SNode(FileType fileType, byte generation, long creationDate, long modificationDate, short length, int dataBlock_01, int dataBlock_02, int dataBlock_03) {
        this.fileType = fileType;
        this.generation = generation;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
        this.length = length;
        this.dataBlock_01 = dataBlock_01;
        this.dataBlock_02 = dataBlock_02;
        this.dataBlock_03 = dataBlock_03;
    }

    public SNode(FileType fileType, byte generation, long creationDate, long modificationDate, short length, int dataBlock_01, int dataBlock_02, int dataBlock_03, int dataBlock_04) {
        this.fileType = fileType;
        this.generation = generation;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
        this.length = length;
        this.dataBlock_01 = dataBlock_01;
        this.dataBlock_02 = dataBlock_02;
        this.dataBlock_03 = dataBlock_03;
        this.dataBlock_04 = dataBlock_04;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public byte getGeneration() {
        return generation;
    }

    public void setGeneration(byte generation) {
        this.generation = generation;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public long getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(long modificationDate) {
        this.modificationDate = modificationDate;
    }

    public short getLength() {
        return length;
    }

    public void setLength(short length) {
        this.length = length;
    }

    public int getDataBlock_01() {
        return dataBlock_01;
    }

    public void setDataBlock_01(int dataBlock_01) {
        this.dataBlock_01 = dataBlock_01;
    }

    public int getDataBlock_02() {
        return dataBlock_02;
    }

    public void setDataBlock_02(int dataBlock_02) {
        this.dataBlock_02 = dataBlock_02;
    }

    public int getDataBlock_03() {
        return dataBlock_03;
    }

    public void setDataBlock_03(int dataBlock_03) {
        this.dataBlock_03 = dataBlock_03;
    }

    public int getDataBlock_04() {
        return dataBlock_04;
    }

    public void setDataBlock_04(int dataBlock_04) {
        this.dataBlock_04 = dataBlock_04;
    }
}
