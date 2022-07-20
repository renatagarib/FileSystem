package application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


import exceptions.*;
import interfaces.FileManagementInterface;
import interfaces.VirtualDiskInspectionInterface;
import structures.*;

import static structures.FileType.DIRECTORY;
import static structures.FileType.valueOf;

public class FileManager implements FileManagementInterface, VirtualDiskInspectionInterface {

    private Path binaryFile;
    private SNode[] sNodes;
    private BitMap fileInfoControl;
    private DataBlock[] dataBlocks;
    private BitMap dataControl;


    public FileManager(String binaryFile, int n, int m) throws IOException {
        this.binaryFile = Paths.get(binaryFile);

        byte[] byteArray = readBinaryFile(this.binaryFile);

        if (byteArray.length == 0) {
            sNodes = new SNode[n];

            for (int i = 0; i < n; i++) {
                sNodes[i] = new SNode();
            }

            fileInfoControl = new BitMap(n/8);
            dataBlocks = new DataBlock[m];
            dataControl = new BitMap(m/8);

            addRootDirectory();

            for (int i = 0; i < m; i++) {
                dataBlocks[i] = new DataBlock();
            }

        } else {
            int placeInTheArray = 0;

            this.sNodes = new SNode[n];

            //create root node
            byte[] rootDirectory = new byte[28];
            System.arraycopy(byteArray, 0, rootDirectory, 0, rootDirectory.length);
            this.sNodes[0] = new SNode();
            this.sNodes[0].rootNode(rootDirectory);

            placeInTheArray += rootDirectory.length;

            for (int i = 1; i < n; i++) {
                byte[] sNode = new byte[28];
                System.arraycopy(byteArray, placeInTheArray, sNode, 0, sNode.length);
                this.sNodes[i] = new SNode(sNode);
                placeInTheArray += sNode.length;
            }

            byte[] fileInfoControl = new byte[n/8];
            System.arraycopy(byteArray, placeInTheArray, fileInfoControl, 0, fileInfoControl.length);
            this.fileInfoControl = new BitMap(fileInfoControl);
            placeInTheArray += fileInfoControl.length;

            this.dataBlocks = new DataBlock[m];

            for (int i = 0; i < m; i++) {
                byte[] dataBlock = new byte[128];
                System.arraycopy(byteArray, placeInTheArray, dataBlock, 0, dataBlock.length);
                this.dataBlocks[i] = new DataBlock(dataBlock);
                placeInTheArray += dataBlock.length;
            }

            byte[] dataControl = new byte[m/8];
            System.arraycopy(byteArray, placeInTheArray, dataControl, 0, dataControl.length);
            this.dataControl = new BitMap(dataControl);
        }
    }

    private void addRootDirectory() {
        //get the time of creation
        long time = System.currentTimeMillis();;

        //add the sNode to the sNodes array
        sNodes[0].reUseSNode(DIRECTORY, time, time, (short) 0, new int[] {0});

        //change the bitmap element of the sNode and dataBlock to 1
        fileInfoControl.addElement(0);
        dataControl.addElement(0);
    }

    private byte[] readBinaryFile(Path path) throws IOException {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            byte[] fileBytes = {};
            Files.write(path, fileBytes);
            return fileBytes;
        }
    }

    @Override
    public boolean addDirectory(String pathname, String filename) throws InvalidEntryException, VirtualFileNotFoundException {
        if (!pathname.contains("/"))
            throw new InvalidEntryException("Invalid pathname.");
        if (filename.length() > 122 || !filename.matches("^[a-zA-Z\\d.\\s_]+$"))
            throw new InvalidEntryException("Invalid name.");


        int sNode = fileInfoControl.findClearSpot();
        int[] dataBlock = dataControl.findClearSpots(1);

        //checks if there is a free SNode and a free data block
        if (sNode > 0 || dataBlock[0] > 0) {
            short entryLength = (short) (filename.length() + 6);

            //turn entryLength into a multiple of 16
            while (entryLength % 16 != 0) {
                entryLength ++;
            }

            //find the directory where the new directory will go
            int dirSNode = findDirectoryThroughPath(pathname);
            //if the directory isn't found
            if (dirSNode == -1)
                throw new InvalidEntryException("Directory not found in pathname.");

            //get the data blocks from the last directory in the path (where the new directory will be added)
            int[] dirDataBlocks = sNodes[dirSNode].getDataBlocks();

            //check for repeated filenames
            if (dataBlocks[dirDataBlocks[0]].lookForDEntry(filename) != -1)
                throw new InvalidEntryException("There is already a file named " + filename + " in the chosen directory.");

            //create a dEntry for the new directory
            DEntry dir = new DEntry(sNode, entryLength, DIRECTORY, (byte) filename.length(), filename);
            //check if the addition of the DEntry at the directory was successful
            if (dataBlocks[dirDataBlocks[0]].addDEntry(dir)) {

                long time = System.currentTimeMillis();;

                //reuse SNode for the new directory (so that the generation var add 1)
                sNodes[sNode].reUseSNode(DIRECTORY, time, time, (short) 0, dataBlock);

                //add DEntry into de directory's sNode
                sNodes[dirSNode].addDEntry(time, entryLength);

                //update bitmaps
                fileInfoControl.addElement(sNode);
                dataControl.addElement(dataBlock[0]);
                return true;
            }

            return false;
        }
        return false;
    }

    @Override
    public boolean addFile(String pathname, String filename, FileType type, int length) throws InvalidEntryException, VirtualFileNotFoundException {
        //catch exceptions before function execution
        if (!pathname.contains("/"))
            throw new InvalidEntryException("Invalid pathname.");
        if (filename.length() > 122 || !filename.matches("^[a-zA-Z\\d.\\s_]+$"))
            throw new InvalidEntryException("Invalid name.");
        if (type == DIRECTORY)
            throw new InvalidEntryException("To add a directory, choose addDirectory.");
        if (length > 512)
            throw new InvalidEntryException("File length exceeds the maximum length of 512 bytes.");

        int fileSNode = fileInfoControl.findClearSpot();
        int amount = (int) Math.ceil(length/128.0);
        int[] fileDataBlock = dataControl.findClearSpots(amount);


        if (fileSNode < 0 || fileDataBlock[0] < 0) {
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

        //create a dEntry for the new file
        DEntry file = new DEntry(fileSNode, entryLength, type, (byte) filename.length(), filename);
        if (dataBlocks[dirDataBlocks[0]].addDEntry(file)) {
            long time = System.currentTimeMillis();;

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
        if (!pathname.contains("/"))
            throw new InvalidEntryException("Invalid pathname.");
        if (filename.length() > 122 || !filename.matches("^[a-zA-Z\\d.\\s_]+$"))
            throw new InvalidEntryException("Invalid name.");

        int dirSNode = findDirectoryThroughPath(pathname);
        if (dirSNode == -1)
            throw new VirtualFileNotFoundException("Directory not found in pathname.");

        int[] dirDataBlocks = sNodes[dirSNode].getDataBlocks();

        int sNode = dataBlocks[dirDataBlocks[0]].lookForDEntry(filename);

        if (sNode == -1)
            throw new VirtualFileNotFoundException("File not found in directory.");

        if (sNodes[sNode].getFileType() == DIRECTORY && sNodes[sNode].getLength() > 0) {
            System.out.println("A Directory can only be deleted if it is empty");
            return false;
        } else {
            int[] db = sNodes[sNode].getDataBlocks();

            short entryLength = dataBlocks[dirDataBlocks[0]].getDEntryLength(sNode);

            long time = System.currentTimeMillis();;
            sNodes[dirSNode].deleteDEntry(time, entryLength);

            dataBlocks[dirDataBlocks[0]].deleteDEntry(sNode, entryLength);

            for (int entry : db) {
                dataControl.clearElement(entry);
            }
            fileInfoControl.clearElement(sNode);
            return true;
        }
    }

    @Override
    public String[] listDirectory(String pathname) throws InvalidEntryException, VirtualFileNotFoundException {
        if (!pathname.contains("/"))
            throw new InvalidEntryException("Invalid pathname.");

        int sNode = findDirectoryThroughPath(pathname);
        if (sNode == -1)
            throw new VirtualFileNotFoundException("Directory not found.");

        int[] dirDataBlocks = sNodes[sNode].getDataBlocks();

        return dataBlocks[dirDataBlocks[0]].toStringArray();
    }

    @Override
    public boolean parseCommandFile(String pathname) {

        try {
            FileReader fileReader = new FileReader(pathname);

            String line;

            BufferedReader commandFile = new BufferedReader(fileReader);

            while ((line = commandFile.readLine()) != null) {
                String[] commands = line.split(" ");

                switch (commands[0]) {
                    case "addFile":
                        FileType fileType = valueOf(commands[3].toUpperCase());
                        try {
                            addFile(commands[1], commands[2], fileType, Integer.parseInt(commands[4]));
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Comando: '" + line + "' nao esta no formato correto.");
                        }

                        break;
                    case "addDir":
                        try {
                            addDirectory(commands[1], commands[2]);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Comando: '" + line + "' nao esta no formato correto.");
                        }
                        break;
                    case "delete":
                        try {
                            deleteFile(commands[1], commands[2]);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Comando: '" + line + "' nao esta no formato correto.");
                        }

                        break;
                }
            }

            return true;

        } catch (IOException e) {
            System.out.println("Arquivo nao encontrado.");
            return false;
        } catch (VirtualFileNotFoundException | InvalidEntryException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean saveVirtualDisk() {

        int size = (sNodes.length * 28) + fileInfoControl.getSize() + (dataBlocks.length * 128) + dataControl.getSize();
        byte[] fileManagerIntoBytes = new byte[size];

        int placeInTheArray = 0;

        for (SNode sNode : sNodes) {
            byte[] sNodeInBytes;
            if (sNode.getFileType() == null) {
                sNodeInBytes = new byte[28];
            } else {
                sNodeInBytes = sNode.toByteArray();
            }
            System.arraycopy(sNodeInBytes, 0, fileManagerIntoBytes, placeInTheArray, sNodeInBytes.length);
            placeInTheArray += sNodeInBytes.length;
        }

        System.arraycopy(fileInfoControl.toByteArray(), 0, fileManagerIntoBytes, placeInTheArray, fileInfoControl.getSize());
        placeInTheArray += fileInfoControl.getSize();

        for (DataBlock dataBlock : dataBlocks) {
            byte[] dataBlockInBytes = dataBlock.toByteArray();
            System.arraycopy(dataBlockInBytes, 0, fileManagerIntoBytes, placeInTheArray, dataBlockInBytes.length);
            placeInTheArray += dataBlockInBytes.length;
        }

        System.arraycopy(dataControl.toByteArray(), 0, fileManagerIntoBytes, placeInTheArray, dataControl.getSize());

        try {
            Files.write(binaryFile, fileManagerIntoBytes);
            return true;
        } catch (IOException e) {
            return false;
        }

    }

    @Override
    public String getSNodeInfo(int snodeId) throws InvalidSNodeException {
        if (snodeId >= 0 && snodeId < sNodes.length)
            return "SNode " + snodeId + ":\n" +
                    sNodes[snodeId].toString();
        else
            throw new InvalidSNodeException("Invalid S Node ID.");
    }

    @Override
    public String getSnodeBitmap() {
        return "SNode Bitmap:\n" + fileInfoControl.toString();
    }

    @Override
    public String getDataBlockBitmap() {
        return "Data Block Bitmap:\n" + dataControl.toString();
    }

    private int findDirectoryThroughPath(String pathname) {
        String[] directories = pathname.split("/");

        //start at the root sNode
        int sNode = 0;
        //array for the data blocks from each sNode
        int[] dirDataBlocks;
        //iterate on the directories from the path
        if (directories.length >= 2) {
            for (int i = 1; i < directories.length; i++) {
                //get the data blocks from the sNode
                dirDataBlocks = sNodes[sNode].getDataBlocks();

                //look in the data block from the last directory the sNode for the next directory
                sNode = dataBlocks[dirDataBlocks[0]].lookForDEntry(directories[i]);
                //if the directory isn't into the last directory it returns -1
                if (sNode == -1) {
                    return -1; //the path is not right
                }
            }
        }
        return sNode;
    }
}
