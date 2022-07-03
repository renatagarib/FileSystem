package structures;

public class DEntry {
    private int sNodeIdentifier;
    private short entryLength;
    private FileType fileType;
    private byte fileNameLength;
    private byte[] fileName;

    public DEntry(int sNodeIdentifier, short entryLength, FileType fileType, byte fileNameLength, byte[] fileName) {
        this.sNodeIdentifier = sNodeIdentifier;
        this.entryLength = entryLength;
        this.fileType = fileType;
        this.fileNameLength = fileNameLength;
        this.fileName = fileName;
    }


}
