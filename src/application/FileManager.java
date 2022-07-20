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

/**
* Classe FileManager
* Responsavel por Gerenciar os arquivos disponíveis.
*
* @author Gabriela Marcelino Pereira de Souza - N°USP 9379614
* @author Heitor Boccato - N°USP 10277130
* @author Renata Rona Garib - N°USP 11207950
*/
public class FileManager implements FileManagementInterface, VirtualDiskInspectionInterface {

    private Path binaryFile;
    private SNode[] sNodes;
    private BitMap fileInfoControl;
    private DataBlock[] dataBlocks;
    private BitMap dataControl;


    public FileManager(String binaryFile, int n, int m) throws IOException {
        //get the path to the file passed as a parameter
        this.binaryFile = Paths.get(binaryFile);

        //read the file passed as a parameter
        byte[] byteArray = readBinaryFile(this.binaryFile);

        //if the file is empty
        if (byteArray.length == 0) {
            //create a new sNode array the size of the parameter
            this.sNodes = new SNode[n];

            //instantiate all so the generation number is 0
            for (int i = 0; i < n; i++) {
                sNodes[i] = new SNode();
            }

            //create a bitmap to control the SNode array
            this.fileInfoControl = new BitMap(n/8);
            //create aa DataBlock array
            this.dataBlocks = new DataBlock[m];
            //create a bitmap to control de DataBlock array
            this.dataControl = new BitMap(m/8);

            //create the root directory
            addRootDirectory();

            //instantiate the data blocks in the array, so that they are an array of bytes 0
            for (int i = 0; i < m; i++) {
                dataBlocks[i] = new DataBlock();
            }

          //if the file is not empty
        } else {
            //create a variable to track where we are at the array, starting at the beginning
            int placeInTheArray = 0;

            //create a new sNode array the size of the parameter
            this.sNodes = new SNode[n];

            //create root node
            byte[] rootDirectory = new byte[28];
            System.arraycopy(byteArray, 0, rootDirectory, 0, rootDirectory.length);
            this.sNodes[0] = new SNode();
            this.sNodes[0].rootNode(rootDirectory);


            placeInTheArray += rootDirectory.length;

            //read the SNodes in the file, skipping the root
            for (int i = 1; i < n; i++) {
                byte[] sNode = new byte[28];
                System.arraycopy(byteArray, placeInTheArray, sNode, 0, sNode.length);
                //instantiate each sNode with the respective array of bytes
                this.sNodes[i] = new SNode(sNode);
                placeInTheArray += sNode.length;
            }

            //read the bitmap to control the SNode array
            byte[] fileInfoControl = new byte[n/8];
            System.arraycopy(byteArray, placeInTheArray, fileInfoControl, 0, fileInfoControl.length);
            this.fileInfoControl = new BitMap(fileInfoControl);
            placeInTheArray += fileInfoControl.length;

            //create aa DataBlock array
            this.dataBlocks = new DataBlock[m];

            //read the dataBlocks in the file
            for (int i = 0; i < m; i++) {
                byte[] dataBlock = new byte[128];
                System.arraycopy(byteArray, placeInTheArray, dataBlock, 0, dataBlock.length);
                this.dataBlocks[i] = new DataBlock(dataBlock);
                placeInTheArray += dataBlock.length;
            }

            //read the bitmap to control de DataBlock array
            byte[] dataControl = new byte[m/8];
            System.arraycopy(byteArray, placeInTheArray, dataControl, 0, dataControl.length);
            this.dataControl = new BitMap(dataControl);
        }
    }

    /**
	* Método addRootDirectory utilizando o tempo e o sNode para a criação dos diretorios raiz.
	*/
    private void addRootDirectory() {
        //get the time of creation
        long time = System.currentTimeMillis();

        //add the sNode to the sNodes array, adding the dataBlock 0 to it
        sNodes[0].reUseSNode(DIRECTORY, time, time, (short) 0, new int[] {0});

        //change the bitmap element of the sNode and dataBlock to 1
        fileInfoControl.addElement(0);
        dataControl.addElement(0);
    }

    /**
	* Método readBinaryFile utilizado para ler os arquivos binarios.
	* Path path
	*/
    private byte[] readBinaryFile(Path path) throws IOException {
        try {
            //read the file
            return Files.readAllBytes(path);
        } catch (IOException e) { //if the file is empty
            byte[] fileBytes = {}; // create an empty array of bytes
            Files.write(path, fileBytes); //create the empty file
            return fileBytes; //return the empty array
        }
    }

    /**
	* Método addDirectory utiliza as entradas do usuário String pathname, String filename
	* para criar um diretório.
	*/
    @Override
    public boolean addDirectory(String pathname, String filename) throws InvalidEntryException, VirtualFileNotFoundException {
        //catch exceptions before function execution
        //checks if the pathname is valid
        if (!pathname.contains("/"))
            throw new InvalidEntryException("Invalid pathname.");
        //checks if the filename is valid
        if (filename.length() > 122 || !filename.matches("^[a-zA-Z\\d.\\s_]+$"))
            throw new InvalidEntryException("Invalid name.");


        //find a clear spot for the new directory SNode
        int sNode = fileInfoControl.findClearSpot();
        //find a clear spot for the new directory dataBlock
        int[] dataBlock = dataControl.findClearSpots(1);

        //checks if there is a free SNode and a free data block
        if (sNode > 0 || dataBlock[0] > 0) {
            //get the entryName by adding the filename length plus 8 bytes to store the rest of the info
            short entryLength = (short) (filename.length() + 6);

            //turn entryLength into a multiple of 16
            while (entryLength % 16 != 0) {
                entryLength ++;
            }

            //find the parent directory where the new directory will go
            int dirSNode = findDirectoryThroughPath(pathname);
            //if the parent directory isn't found
            if (dirSNode == -1)
                throw new InvalidEntryException("Directory not found in pathname.");

            //get the data blocks from the parent directory
            int[] dirDataBlocks = sNodes[dirSNode].getDataBlocks();

            //check if there is already a file in the parent directory with the same name
            if (dataBlocks[dirDataBlocks[0]].lookForDEntry(filename) != -1)
                throw new InvalidEntryException("There is already a file named " + filename + " in the chosen directory.");

            //create a dEntry for the new directory
            DEntry dir = new DEntry(sNode, entryLength, DIRECTORY, (byte) filename.length(), filename);
            //check if the addition of the DEntry at the directory was successful
            if (dataBlocks[dirDataBlocks[0]].addDEntry(dir)) {

                long time = System.currentTimeMillis();;

                //reuse SNode for the new directory (so that the generation var add 1)
                sNodes[sNode].reUseSNode(DIRECTORY, time, time, (short) 0, dataBlock);

                //add DEntry into de the parent directory sNode
                sNodes[dirSNode].addDEntry(time, entryLength);

                //update bitmaps
                fileInfoControl.addElement(sNode);
                dataControl.addElement(dataBlock[0]);
                return true; // the directory was successfully added
            }
        }
        System.out.println("Storage is full.");
        return false; // the directory was not added
    }

    /**
	* Método addFile utiliza as entradas do usuário String pathname, String filename, FileType type, int length.
	* para adicionar um arquivo ao diretório.
	*/
    @Override
    public boolean addFile(String pathname, String filename, FileType type, int length) throws InvalidEntryException, VirtualFileNotFoundException {
        //catch exceptions before function execution
        //checks if the pathname is valid
        if (!pathname.contains("/"))
            throw new InvalidEntryException("Invalid pathname.");
        //checks if the file name is valid
        if (filename.length() > 122 || !filename.matches("^[a-zA-Z\\d.\\s_]+$"))
            throw new InvalidEntryException("Invalid name.");
        //checks if the fileType is valid
        if (type == DIRECTORY)
            throw new InvalidEntryException("To add a directory, choose addDirectory.");
        //check if the length is valid
        if (length > 512)
            throw new InvalidEntryException("File length exceeds the maximum length of 512 bytes.");

        //find a clear spot to the new file SNode
        int fileSNode = fileInfoControl.findClearSpot();
        //checks the amount of dataBlocks necessary for the new file
        int amount = (int) Math.ceil(length/128.0);
        //find clear spots for all the dataBlocks
        int[] fileDataBlock = dataControl.findClearSpots(amount);


        //if there isn't space for, at least, on of the data blocks return false
        if (fileSNode < 0 || fileDataBlock[0] < 0) {
            System.out.println("Storage is full.");
            return false;
        }


        //get the entryName by adding the filename length plus 8 bytes to store the rest of the info
        short entryLength = (short) (filename.length() + 6);

        //turn entryLength into a multiple of 16
        while (entryLength % 16 != 0) {
            entryLength ++;
        }

        //finds directory where the file will be stored
        int sNode = findDirectoryThroughPath(pathname);
        if (sNode == -1)
            throw new VirtualFileNotFoundException("Directory not found in pathname.");


        //get data blocks of the directory
        int[] dirDataBlocks = sNodes[sNode].getDataBlocks();

        //checks if there is already a file with the same name in the directory
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

    /**
	* Método deleteFile utiliza as entradas do usuário String pathname, String filename.
	* para deletar um arquivo ao diretório.
	*/
    @Override
    public boolean deleteFile(String pathname, String filename) throws InvalidEntryException, VirtualFileNotFoundException {
        //catch exceptions before function execution
        //checks if the pathname is valid
        if (!pathname.contains("/"))
            throw new InvalidEntryException("Invalid pathname.");
        //ckecks if the filename is valid
        if (filename.length() > 122 || !filename.matches("^[a-zA-Z\\d.\\s_]+$"))
            throw new InvalidEntryException("Invalid name.");

        //finds directory where the file is stored
        int dirSNode = findDirectoryThroughPath(pathname);
        if (dirSNode == -1)
            throw new VirtualFileNotFoundException("Directory not found in pathname.");

        //get the data blocks from the directory
        int[] dirDataBlocks = sNodes[dirSNode].getDataBlocks();

        //look for file in the directory data block
        int sNode = dataBlocks[dirDataBlocks[0]].lookForDEntry(filename);

        //if the file isn't found return false
        if (sNode == -1)
            throw new VirtualFileNotFoundException("File not found in directory.");

        //checks if the file that's going to be deleted is a directory
        //checks if the said directory if empty
        if (sNodes[sNode].getFileType() == DIRECTORY && sNodes[sNode].getLength() > 0) {
            //like in linux's file system, if the directory isn't empty, it can't be deleted
            System.out.println("A Directory can only be deleted if it is empty");
            return false;
        } else {
            //get the data blocks from the file
            int[] db = sNodes[sNode].getDataBlocks();

            //get the file's entryLength
            short entryLength = dataBlocks[dirDataBlocks[0]].getDEntryLength(sNode);

            long time = System.currentTimeMillis();
            //uptade directory's SNode with the deletion of the file
            sNodes[dirSNode].deleteDEntry(time, entryLength);

            //delete the file from the directory's data block
            dataBlocks[dirDataBlocks[0]].deleteDEntry(sNode, entryLength);

            //clear file's data blocks from the bitmap
            for (int entry : db) {
                dataControl.clearElement(entry);
                //clear file's data blocks
                dataBlocks[entry].clear();
            }
            //clear file's sNode from the bitmap
            fileInfoControl.clearElement(sNode);
            //clear file's sNode
            sNodes[sNode].clear();
            return true;
        }
    }

    /**
	* Método listDirectory utiliza as entradas do usuário String pathname.
	* para listar os arquivos do diretório.
	*/
    @Override
    public String[] listDirectory(String pathname) throws InvalidEntryException, VirtualFileNotFoundException {
        //catch exceptions before function execution
        //checks if the pathname is valid
        if (!pathname.contains("/"))
            throw new InvalidEntryException("Invalid pathname.");

        //finds sNode from the chosen directory
        int sNode = findDirectoryThroughPath(pathname);
        //if the sNode can't be found through the path return false
        if (sNode == -1)
            throw new VirtualFileNotFoundException("Directory not found.");

        //get directory's data block
        int[] dirDataBlocks = sNodes[sNode].getDataBlocks();

        //return the string array with the files inside the directory
        return dataBlocks[dirDataBlocks[0]].toStringArray();
    }

    /**
	* Método parseCommandFile utiliza as entradas do usuário String pathname.
	* para Analisar os aquivos de comando.
	*/
    @Override
    public boolean parseCommandFile(String pathname) {

        try {
            //get the path to the command file
            FileReader fileReader = new FileReader(pathname);

            String line;

            //read the file
            BufferedReader commandFile = new BufferedReader(fileReader);

            //go through each line of the file
            while ((line = commandFile.readLine()) != null) {
                //split the contents by 'white space'
                String[] commands = line.split(" ");

                //checks the first command
                switch (commands[0]) {
                    //if the command is addFile
                    case "addFile":
                        //gets the file type
                        FileType fileType = valueOf(commands[3].toUpperCase());
                        try {
                            //try adding a file with the arguments
                            addFile(commands[1], commands[2], fileType, Integer.parseInt(commands[4]));
                            //if there is invalid number of arguments
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Comando: '" + line + "' nao esta no formato correto.");
                        }

                        break;
                    //if the command is addDir
                    case "addDir":
                        try {
                            //try adding a directory with the arguments
                            addDirectory(commands[1], commands[2]);
                            //if there is invalid number of arguments
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Comando: '" + line + "' nao esta no formato correto.");
                        }
                        break;
                    //if the command is delete
                    case "delete":
                        try {
                            //try deleting a file with the arguments
                            deleteFile(commands[1], commands[2]);
                            //if there is invalid number of arguments
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Comando: '" + line + "' nao esta no formato correto.");
                        }

                        break;
                }
            }

            return true;

        } catch (IOException e) { //if the file can't be found
            System.out.println("Arquivo nao encontrado.");
            return false;
        } catch (VirtualFileNotFoundException | InvalidEntryException e) {
            throw new RuntimeException(e);
        }
    }

    /**
	* Método saveVirtualDisk utilizado para salvar no disco.
	*/
    @Override
    public boolean saveVirtualDisk() {

        //get the full size of the final file
        int size = (sNodes.length * 28) + fileInfoControl.getSize() + (dataBlocks.length * 128) + dataControl.getSize();
        //create a byte array to be written in the file
        byte[] fileManagerIntoBytes = new byte[size];

        int placeInTheArray = 0;

        //turns every sNode into bytes and add them in the array
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

        //turn the file control bitmap into and array of bytes and add it to the final array
        System.arraycopy(fileInfoControl.toByteArray(), 0, fileManagerIntoBytes, placeInTheArray, fileInfoControl.getSize());
        placeInTheArray += fileInfoControl.getSize();

        //turn every data block into bytes and add them in the final array
        for (DataBlock dataBlock : dataBlocks) {
            byte[] dataBlockInBytes = dataBlock.toByteArray();
            System.arraycopy(dataBlockInBytes, 0, fileManagerIntoBytes, placeInTheArray, dataBlockInBytes.length);
            placeInTheArray += dataBlockInBytes.length;
        }

        //turn the data control bitmap into and array of bytes and add it to the final array
        System.arraycopy(dataControl.toByteArray(), 0, fileManagerIntoBytes, placeInTheArray, dataControl.getSize());

        try {
            Files.write(binaryFile, fileManagerIntoBytes); //write the final array into the file
            return true;
        } catch (IOException e) {
            return false;
        }

    }

    /**
	* Método getSNodeInfo utiliza as entradas do usuário int snodeId.
	* para verificar o sNode.
	*/
    @Override
    public String getSNodeInfo(int snodeId) throws InvalidSNodeException {
        if (snodeId >= 0 && snodeId < sNodes.length)
            return "SNode " + snodeId + ":\n" +
                    sNodes[snodeId].toString();
        else
            throw new InvalidSNodeException("Invalid S Node ID.");
    }

    /**
	* Método getSNodeBitmap obtem o bitmap do sNode.
	* @return fileInfoControl.toString() - Referente ao bitmap solicitado
	*/
    @Override
    public String getSnodeBitmap() {
        return "SNode Bitmap:\n" + fileInfoControl.toString();
    }

    /**
	* Método getDataBlockBitmap obtem o bitmap dos blocos de dados.
	* @return dataControl.toString() - Referente ao bitmap de controle de dados
	*/
    @Override
    public String getDataBlockBitmap() {
        return "Data Block Bitmap:\n" + dataControl.toString();
    }

    /**
	* Método findDirectoryThroughPath utiliza a entrada do usuário String pathname.
	* para encontrar um diretório através do caminho existente.
	*/
    private int findDirectoryThroughPath(String pathname) {
        //split the path into directories
        String[] directories = pathname.split("/");

        //start at the root sNode
        int sNode = 0;
        //array for the data blocks from each sNode
        int[] dirDataBlocks;
        //iterate on the directories from the path
        if (directories.length >= 2) { // checks if there is more directories than the root
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