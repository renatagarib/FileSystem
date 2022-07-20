package structures;

import byteManager.ByteManager;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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
        //create an array to store the bytes
        byte[] bytes = new byte[entryLength];

        //create a variable to track where we are at the array, starting at the beginning
        int placeInTheArray = 0;

        //transform the NodeIdentifier into bytes
        byte[] sNodeIdentifierInBytes = ByteManager.turnIntegerIntoBytes(sNodeIdentifier);
        //add the bytes, starting at the second one to simulate unsigned integer,
        //into the array that's going to be returned
        System.arraycopy(sNodeIdentifierInBytes, 0, bytes, placeInTheArray, sNodeIdentifierInBytes.length);

        //update the new position adding the length of the added array
        placeInTheArray += sNodeIdentifierInBytes.length;

        //transform entryLength into bytes
        byte[] entryLengthInBytes = ByteManager.turnShortIntoBytes(entryLength);
        //add the bytes into the array that's going to be returned
        System.arraycopy(entryLengthInBytes, 0, bytes, placeInTheArray, entryLengthInBytes.length);

        //update the position
        placeInTheArray += entryLengthInBytes.length;

        //get the fileType id and store it in the array
        bytes[placeInTheArray] = fileType.id();
        placeInTheArray += 1;

        //get the fileNameLength and store it in the array
        bytes[placeInTheArray] = fileNameLength;
        placeInTheArray += 1;

        //transform the fileName in bytes using utf-8
        byte[] fileNameInBytes = ByteManager.turnStringIntoBytes(fileName);
        //store it into the array
        System.arraycopy(fileNameInBytes, 0, bytes, placeInTheArray, fileNameInBytes.length);

        return bytes;
    }

    public short getEntryLength() {
        return entryLength;
    }

}
