import java.io.FileWriter;
import java.io.IOException;



public class Main {
    public static void main(String[] args) throws IOException {
        int[] referencias = {7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2, 1, 2, 0, 1, 7, 0, 1};
        int molduras = 3;

        // Cria o arquivo do zero, só com o cabeçalho
        try (FileWriter csv = new FileWriter("resultados.csv")) {
            csv.write("molduras,algoritmo,faltas\n");
        }

        AlgoritmoSubstituicao[] algoritmos = { new FIFO(), new LRU(), new Relogio(), new Otimo() };

        for (int i = 0; i < algoritmos.length; i++) {
            int faltas = algoritmos[i].simularESalvar(referencias, molduras);
            System.out.println("Método " + (i + 1) + " - " + faltas + " faltas de página");
        }
    }
}