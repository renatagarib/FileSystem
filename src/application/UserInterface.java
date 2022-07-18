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
                    if (conferirArgumentos(comando, 2))
                        gerenteDeArquivo.addDirectory(comando[1], comando[2]);
                    break;
                case "addFile":
                    if (conferirArgumentos(comando, 4))
                        try {
                            gerenteDeArquivo.addFile(comando[1], comando[2], FileType.valueOf(comando[3]), Integer.parseInt(comando[4]) );
                        } catch (IllegalArgumentException e) {
                            System.out.println("Types of files:\n" +
                                                "UNKNOWN\n" +
                                                "REGULAR\n" +
                                                "CHARACTER_DEVICE\n" +
                                                "BLOCK_DEVICE\n" +
                                                "FIFO\n" +
                                                "SOCKET\n" +
                                                "SYMBOLIC_LINK");
                        }

                    break;
                case "listDirectory":
                    if (conferirArgumentos(comando, 1)) {
                        String[] directory =  gerenteDeArquivo.listDirectory(comando[1]);
                        System.out.println(Arrays.toString(directory));
                    }
                    break;
                case "deleteFile":
                    if (conferirArgumentos(comando, 2)) {
                        gerenteDeArquivo.deleteFile(comando[1], comando[2]);
                        System.out.println("Processo "+ comando[1] +" excluído.");
                    }
                    break;
                case "parseCommandFile":
                    if (conferirArgumentos(comando, 1)) {
                        gerenteDeArquivo.parseCommandFile(comando[1]);
                        System.out.println("Memoria esvaziada.");
                    }
                    break;
                case "saveVirtualDisk":
                    if (conferirArgumentos(comando, 0)) {
                        gerenteDeArquivo.saveVirtualDisk();
                    }
                    break;
                case "ajuda":
                            listarComandos();
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
        System.out.println("deletFile <caminho absoluto do diretorio, nome do arquivo>: Excluir arquivo");
        System.out.println("parseCommandFile <caminho ao arquivo> :");
        System.out.println("saveVirtualDisk: Salva no disco");
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