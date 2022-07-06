package application;

import java.time.LocalDateTime;
import java.time.ZoneId;

import exceptions.*;
import interfaces.FileManagementInterface;
import interfaces.VirtualDiskInspectionInterface;
import structures.BitMap;
import structures.DataBlock;
import structures.FileType;
import structures.SNode;

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
        SNode rootNode = new SNode(FileType.DIRECTORY, time, time, (short) 1, 0);
        //add the sNode to the sNodes array
        sNodes[0] = rootNode;

        //change the bitmap element of the sNode and dataBlock to 1
        fileInfoControl.addElement(0);
        dataControl.addElement(0);
    }

    @Override
    public boolean addDirectory(String pathname, String filename) throws InvalidEntryException, VirtualFileNotFoundException {
        String[] directories = pathname.split("/");

        if (directories.length == 0) {

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
