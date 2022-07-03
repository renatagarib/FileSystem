package IOHandler;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class InputReader {

    public static FileResponse readEspecificationFile(String name) {

        FileResponse fileResponse = new FileResponse();

        try {

            FileReader fileReader = new FileReader("./input/" + name + ".txt");

            String line;

            BufferedReader specificationFile = new BufferedReader(fileReader);

            while ((line = specificationFile.readLine()) != null) {
                String[] array = line.split(" ");

                switch (array[0]) {
                    case "switch-fabric:":
                        fileResponse.setSwitchFabric(array[1]);
                        break;
                    case "input:":
                        fileResponse.addInputPort(array[1], array[2], array[3], array[4]);
                        break;
                    case "output:":
                        fileResponse.addOutputPort(array[1], array[2], array[3], array[4], array[5]);
                        break;
                    default:
                        throw new FileFormatException("Formato do arquivo");
                }
            }

            specificationFile.close();
            fileResponse.checkSumFowardProbability();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro de leitura do txt");
        }
        return fileResponse;
    }
}

