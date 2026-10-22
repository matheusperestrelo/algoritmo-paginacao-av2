import java.io.FileWriter;
import java.io.IOException;

public interface AlgoritmoSubstituicao {
    String getNome();
    int simular(int[] referencias, int numMolduras);

    // Simula e já acrescenta uma linha no CSV
    default int simularESalvar(int[] referencias, int numMolduras) throws IOException {
        int faltas = simular(referencias, numMolduras);

        try (FileWriter csv = new FileWriter("resultados.csv", true)) {  // true = acrescenta no fim
            csv.write(numMolduras + "," + getNome() + "," + faltas + "\n");
        }
        return faltas;
    }
}