package application;

import java.time.LocalDateTime;
import java.time.ZoneId;

import exceptions.*;
import interfaces.FileManagementInterface;
import interfaces.VirtualDiskInspectionInterface;
import structures.*;

import static structures.FileType.DIRECTORY;

public class FileManager implements FileManagementInterface, VirtualDiskInspectionInterface {

    private SNode[] sNodes;
    private BitMap fileInfoControl;
    private DataBlock[] dataBlocks;
    private BitMap dataControl;

    public FileManager(int n, int m) {
        sNodes = new SNode[n];

        for (SNode sNode : sNodes) {
            sNode = new SNode();
        }

        fileInfoControl = new BitMap(n/8);
        dataBlocks = new DataBlock[m];
        dataControl = new BitMap(m/8);

        addRootDirectory();

        for (int i = 0; i < m; i++) {
            dataBlocks[i] = new DataBlock();
        }
    }

    private void addRootDirectory() {
        //get the time of creation
        ZoneId zoneId = ZoneId.systemDefault();
        long time = LocalDateTime.now().atZone(zoneId).toEpochSecond();

        //add the sNode to the sNodes array
        sNodes[0].reUseSNode(DIRECTORY, time, time, (short) 0, new int[] {0});

        //change the bitmap element of the sNode and dataBlock to 1
        fileInfoControl.addElement(0);
        dataControl.addElement(0);
    }

    @Override
    public boolean addDirectory(String pathname, String filename) throws InvalidEntryException, VirtualFileNotFoundException {
        if (!pathname.contains("/"))
            throw new InvalidEntryException("Invalid pathname.");
        if (filename.length() > 122 || !filename.matches("^[a-zA-Z\\d.\\s_]+$"))
            throw new InvalidEntryException("Invalid name.");


        int dirSNode = fileInfoControl.findClearSpot();
        int dirDataBlock = dataControl.findClearSpot();

        //checks if there is a free SNode
        if (dirSNode > 0) { //return -1 if there isn't a free space
            //checks if there is a free DataBlock
            if (dirDataBlock > 0) { //return -1 if there isn't a free space
                short entryLength = (short) (filename.length() + 6);

                //turn entryLength into a multiple of 16
                while (entryLength % 16 != 0) {
                    entryLength ++;
                }

                //find the directory where the new directory will go
                int sNode = findDirectoryThroughPath(pathname);
                //if the directory isn't found
                if (sNode == -1)
                    throw new InvalidEntryException("Directory not found in pathname.");
                //get the data blocks from the last directory in the path (where the new directory will be added)
                int[] dirDataBlocks = sNodes[sNode].getDataBlocks();
                //check for repeated filenames
                if (dataBlocks[dirDataBlocks[0]].lookForDEntry(filename) != -1)
                    throw new InvalidEntryException("There is already a file named " + filename + " in the chosen directory.");
                //create a dEntry for the new directory
                DEntry dir = new DEntry(dirSNode, entryLength, DIRECTORY, (byte) filename.length(), filename);
                if (dataBlocks[dirDataBlocks[0]].addDEntry(dir)) {
                    ZoneId zoneId = ZoneId.systemDefault();
                    long time = LocalDateTime.now().atZone(zoneId).toEpochSecond();

                    //reuse SNode for the new directory (so that the generation var add 1)
                    sNodes[dirSNode].reUseSNode(DIRECTORY, time, time, (short) 0, new int[] {dirDataBlock});

                    //add DEntry into de directory's sNode
                    sNodes[sNode].addDEntry(time, entryLength);

                    //update bitmaps
                    fileInfoControl.addElement(dirSNode);
                    dataControl.addElement(dirDataBlock);
                    return true;
                } else {
                    return false;
                }

            }
        }
        return false;
    }

    @Override
    public boolean addFile(String pathname, String filename, FileType type, int length) throws InvalidEntryException, VirtualFileNotFoundException {
        if (!pathname.contains("/"))
            throw new InvalidEntryException("Invalid pathname.");
        if (filename.length() > 122 || !filename.matches("^[a-zA-Z\\d.\\s_]+$"))
            throw new InvalidEntryException("Invalid name.");
        if (type == DIRECTORY)
            throw new InvalidEntryException("To add a directory, choose addDirectory.");
        if (length > 512)
            throw new InvalidEntryException("File length exceeds the maximum length of 512 bytes.");

        int fileSNode = fileInfoControl.findClearSpot();
        int[] fileDataBlock = new int[(int) Math.ceil(length/128.0)];

        for (int i = 0; i < fileDataBlock.length; i++) {
            fileDataBlock[i] = dataControl.findClearSpot();
            if (fileDataBlock[i] == -1) {
                System.out.println("Storage is full.");
                return false;
            }
        }

        if (fileSNode < 0) {
            System.out.println("Storage is full.");
            return false;
        }


        short entryLength = (short) (filename.length() + 6);

        while (entryLength % 16 != 0) {
            entryLength ++;
        }

        int sNode = findDirectoryThroughPath(pathname);
        if (sNode == -1)
            throw new VirtualFileNotFoundException("Directory not found in pathname.");


        int[] dirDataBlocks = sNodes[sNode].getDataBlocks();

        if (dataBlocks[dirDataBlocks[0]].lookForDEntry(filename) != -1)
            throw new InvalidEntryException("There is already a file named " + filename + " in the chosen directory.");

        //create a dEntry for the new directory
        DEntry file = new DEntry(fileSNode, entryLength, type, (byte) filename.length(), filename);
        if (dataBlocks[dirDataBlocks[0]].addDEntry(file)) {
            ZoneId zoneId = ZoneId.systemDefault();
            long time = LocalDateTime.now().atZone(zoneId).toEpochSecond();

            //reuse SNode for the new directory (so that the generation var add 1)
            sNodes[fileSNode].reUseSNode(type, time, time, (short) length, fileDataBlock);

            //add DEntry into de directory's sNode
            sNodes[sNode].addDEntry(time, entryLength);

            //update bitmaps
            fileInfoControl.addElement(fileSNode);
            for (int dataBlock : fileDataBlock) {
                dataControl.addElement(dataBlock);
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean deleteFile(String pathname, String filename) throws InvalidEntryException, VirtualFileNotFoundException {
        return false;
    }

    @Override
    public String[] listDirectory(String pathname) throws InvalidEntryException, VirtualFileNotFoundException {
        return new String[0];
    }

    @Override
    public boolean parseCommandFile(String pathname) {
        return false;
    }

    @Override
    public boolean saveVirtualDisk() {
        return false;
    }

    @Override
    public String getSNodeInfo(int snodeId) throws InvalidSNodeException {
        return null;
    }

    @Override
    public String getSnodeBitmap() {
        return null;
    }

    @Override
    public String getDataBlockBitmap() {
        return null;
    }

    private int findDirectoryThroughPath(String pathname) {
        String[] directories = pathname.split("/");
        //start at the root sNode
        int sNode = 0;
        //array for the data blocks from each sNode
        int[] dirDataBlocks;
        //iterate on the directories from the path
        for (String directory : directories) {
            //get the data blocks from the sNode
            dirDataBlocks = sNodes[sNode].getDataBlocks();
            //look in the data block from the last directory the sNode for the next directory
            sNode = dataBlocks[dirDataBlocks[0]].lookForDEntry(directory);
            //if the directory isn't into the last directory it returns -1
            if (sNode == -1) {
                return -1; //the path is not right
            }
        }
        return sNode;
    }
}
