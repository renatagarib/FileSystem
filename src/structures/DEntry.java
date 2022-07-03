package structures;

import java.nio.ByteBuffer;

public class DEntry {
    private int sNodeIdentifier;
    private short entryLength;
    private FileType fileType;
    private byte fileNameLength;
    private String fileName;

    public DEntry(int sNodeIdentifier, short entryLength, FileType fileType, byte fileNameLength, String fileName) {
        this.sNodeIdentifier = sNodeIdentifier;
        this.entryLength = entryLength;
        this.fileType = fileType;
        this.fileNameLength = fileNameLength;
        this.fileName = fileName;
    }

    public byte[] turnIntoBytes() {
        byte[] bytes = new byte[entryLength];

        int placeInTheArray = 0;
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(sNodeIdentifier);
        buffer.rewind();
        byte[] sNodeIdentifierInBytes = buffer.array();
        System.arraycopy(sNodeIdentifierInBytes, 0, bytes, placeInTheArray, sNodeIdentifierInBytes.length);

        placeInTheArray += sNodeIdentifierInBytes.length;

        buffer = ByteBuffer.allocate(Short.BYTES);
        buffer.putInt(entryLength);
        buffer.rewind();
        byte[] entryLengthInBytes = buffer.array();
        System.arraycopy(entryLengthInBytes, 0, bytes, placeInTheArray, entryLengthInBytes.length);

        placeInTheArray += entryLengthInBytes.length;

        buffer = ByteBuffer.allocate(1);
        buffer.putInt(fileType.id());
        buffer.rewind();
        byte[] fileTypeInBytes = buffer.array();
        System.arraycopy(fileTypeInBytes, 0, bytes, placeInTheArray, fileTypeInBytes.length);

        placeInTheArray += fileTypeInBytes.length;

        return bytes;
    }
}
