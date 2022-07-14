package application;

import java.util.Scanner;
import exceptions.*;

public class UserInterface {

    public static void main(String args[]) throws InvalidEntryException, InvalidSNodeException, VirtualFileNotFoundException{

        FileManager gerenteDeArquivo = new FileManager(Integer.parseInt(args[0]));

        String input;
        String[] comando = new String[3];
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
                        gerenteDeArquivo.addFile(comando[1], comando[2], comando[3], Integer.parseInt(comando[4]) );
                    break;
                case "listDirectory":
                    if (conferirArgumentos(comando, 1)) {
                        gerenteDeArquivo.listDirectory(comando[1]);
                        
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
        System.out.println("addDirectory <nome do diretorio, nome do arquivo>: Adiciona um diretorio");
        System.out.println("addFile <nome do diretorio, nome do arquivo, tipo do arquivo, tamanho> : Adiciona um arquivo ");
        System.out.println("listDirector <nome do diretorio> : Lista os diretorios ");
        System.out.println("deletFile <nome do diretorio, nome do arquivo>: Excluir arquivo");
        System.out.println("parseCommandFile <nome do diretorio> :");
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