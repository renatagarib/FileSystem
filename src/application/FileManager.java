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

        //create a sNode for the root directory
        SNode rootNode = new SNode(DIRECTORY, time, time, (short) 1, new int[] {0});
        //add the sNode to the sNodes array
        sNodes[0] = rootNode;

        //change the bitmap element of the sNode and dataBlock to 1
        fileInfoControl.addElement(0);
        dataControl.addElement(0);
    }

    @Override
    public boolean addDirectory(String pathname, String filename) throws InvalidEntryException, VirtualFileNotFoundException {
        int dirSNode = fileInfoControl.findClearSpot();
        int dirDataBlock = dataControl.findClearSpot();

        //checks if there is a free SNode
        if (dirSNode > 0) { //return -1 if there isn't a free space
            //checks if there is a free DataBlock
            if (dirDataBlock > 0) { //return -1 if there isn't a free space
                String[] directories = pathname.split("/");
                short entryLength = (short) (filename.length() + 6);

                while (entryLength % 16 != 0) {
                    entryLength ++;
                }

                int sNode = 0;
                int[] dirDataBlocks;
                for (String directory : directories) {
                    dirDataBlocks = sNodes[sNode].getDataBlocks();
                    sNode = dataBlocks[dirDataBlocks[0]].lookForDEntry(directory);
                    if (sNode == -1) {
                        return false;
                    }
                }
                dirDataBlocks = sNodes[sNode].getDataBlocks();
                DEntry dir = new DEntry(dirSNode, entryLength, DIRECTORY, (byte) filename.length(), filename);
                if (dataBlocks[dirDataBlocks[0]].addDEntry(dir)) {
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
}
