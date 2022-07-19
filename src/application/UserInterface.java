package application;

import java.util.Arrays;
import java.util.Scanner;
import exceptions.*;
import structures.FileType;

public class UserInterface {

    public static void main(String[] args) throws InvalidEntryException, InvalidSNodeException, VirtualFileNotFoundException{

        FileManager gerenteDeArquivo = new FileManager(Integer.parseInt(args[0]), Integer.parseInt(args[1]));

        String input;
        String[] comando;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Para começar digite um comando ou 'ajuda' para listar os comandos possíveis.");
        System.out.print(">");
        input = scanner.nextLine();
        while (!input.equals("sair")) {

            comando = input.split(" ");

            switch (comando[0]) {
                case "addDirectory":
                    try {
                        if (conferirArgumentos(comando, 2))
                            if (gerenteDeArquivo.addDirectory(comando[1], comando[2]))
                                System.out.println("Diretorio " + comando[2] + " adicionado com sucesso.");
                            else
                                System.out.println("Falha ao adicionar diretorio " + comando[2] + ".");
                    } catch (InvalidEntryException | VirtualFileNotFoundException e) {
                        System.out.println(e);
                    }

                    break;
                case "addFile":
                    if (conferirArgumentos(comando, 4))
                        try {
                            if (gerenteDeArquivo.addFile(comando[1], comando[2], FileType.valueOf(comando[3].toUpperCase()), Integer.parseInt(comando[4])))
                                System.out.println("Arquivo " + comando[2] + " adicionado com sucesso.");
                            else
                                System.out.println("Falha ao adicionar arquivo " + comando[2] + ".");
                        } catch (IllegalArgumentException e) {
                            System.out.println("Types of files:\n" +
                                                "UNKNOWN\n" +
                                                "REGULAR\n" +
                                                "CHARACTER_DEVICE\n" +
                                                "BLOCK_DEVICE\n" +
                                                "FIFO\n" +
                                                "SOCKET\n" +
                                                "SYMBOLIC_LINK");
                        } catch (InvalidEntryException | VirtualFileNotFoundException e) {
                            System.out.println(e);
                        }

                    break;
                case "listDirectory":
                    if (conferirArgumentos(comando, 1)) {
                        try {
                            String[] files =  gerenteDeArquivo.listDirectory(comando[1]);

                            String[] path = comando[1].split("/");

                            StringBuilder result;
                            if (path.length <= 1)
                                result = new StringBuilder("Diretorio '/':\n");
                            else
                                result = new StringBuilder("Diretorio " + path[path.length - 1] + ":\n");

                            for (String file : files) {
                                result.append(file).append("\n");
                            }
                            System.out.println(result);

                        } catch (InvalidEntryException | VirtualFileNotFoundException e) {
                            System.out.println(e);
                        }

                    }
                    break;
                case "deleteFile":
                    if (conferirArgumentos(comando, 2)) {
                        try {
                            if (gerenteDeArquivo.deleteFile(comando[1], comando[2]))
                                System.out.println("Objeto "+ comando[2] +" excluido com sucesso.");

                        } catch (InvalidEntryException | VirtualFileNotFoundException e) {
                            System.out.println(e);
                        }

                    }
                    break;
                case "parseCommandFile":
                    if (conferirArgumentos(comando, 1)) {
                        if (gerenteDeArquivo.parseCommandFile(comando[1]))
                            System.out.println("Arquivo de comandos lido com sucesso.");
                        else
                            System.out.println("Falha ao ler arquivo de comandos.");
                    }
                    break;
                case "saveVirtualDisk":
                    if (conferirArgumentos(comando, 0)) {
                        if (gerenteDeArquivo.saveVirtualDisk())
                            System.out.println("Estruturas salvas em disco com sucesso.");
                        else
                            System.out.println("Falha ao salvar as estruturas em disco.");
                    }
                    break;
                case "ajuda":
                    listarComandos();
                    break;
                case "getSNodeInfo":
                    if (conferirArgumentos(comando, 1)) {
                        try {
                            System.out.println(gerenteDeArquivo.getSNodeInfo(Integer.parseInt(comando[1])));
                        } catch (InvalidSNodeException e) {
                            System.out.println(e);
                        }
                    }
                    break;
                case "getSNodeBitmap":
                    if (conferirArgumentos(comando, 0)) {
                        System.out.println(gerenteDeArquivo.getSnodeBitmap());
                    }
                    break;
                case "getDataBlockBitmap":
                    if (conferirArgumentos(comando, 0)) {
                        System.out.println(gerenteDeArquivo.getDataBlockBitmap());
                    }
                    break;
                default:
                    System.out.println("Comando "+ comando[0] +" nao encontrado.");
                    listarComandos();
                    }
            System.out.print(">");
            input = scanner.nextLine();
        }

        scanner.close();
    }
    // List of all commands.
    private static void listarComandos() {
        System.out.println("Comando <parametros>");
        System.out.println("addDirectory <caminho absoluto do diretorio, nome do novo diretorio>: Adiciona um diretorio");
        System.out.println("addFile <caminho absoluto do diretorio, nome do arquivo, tipo do arquivo, tamanho> : Adiciona um arquivo ");
        System.out.println("listDirectory <caminho absoluto do diretorio> : Lista os diretorios ");
        System.out.println("deleteFile <caminho absoluto do diretorio, nome do arquivo>: Exclui arquivo");
        System.out.println("parseCommandFile <caminho ao arquivo> :");
        System.out.println("saveVirtualDisk: Salva no disco");
        System.out.println("getSNodeInfo <ID do SNode>: Lista informacoes do SNode");
        System.out.println("getSNodeBitmap: Imprime o bitmap referente aos SNodes");
        System.out.println("getDataBlockBitmap: Imprime o bitmap referente aos Blocos de Dados");
        System.out.println("sair: Sair");
    }
    /**
     * Verifica se o comando tem o numero de argumentos necessario para ser executado.
     * @param c o comando completo
     * @param n a quantidade de argumentos que o comando deve ter
     * @return true se a quantidade de argumentos esta de acordo
     * **/
    private static boolean conferirArgumentos(String[] c, int n) {
        if (c.length != (n+1)) {
            System.out.println("Numero incorreto de argumentos, tente novamente.");
            return false;
        }
        return true;
    }
}