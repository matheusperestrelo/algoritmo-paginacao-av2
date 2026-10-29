import java.io.FileWriter;
import java.io.IOException;

public interface AlgoritmoSubstituicao {
    String getNome();
    int simular(int[] referencias, int numMolduras, LogSimulacao log);

    // Simula sem gravar log
    default int simular(int[] referencias, int numMolduras) {
        return simular(referencias, numMolduras, LogSimulacao.VAZIO);
    }

    // Simula gravando cada passo no log e acrescenta uma linha no CSV de resultados
    default int simularESalvar(int[] referencias, int numMolduras) throws IOException {
        int faltas;
        try (LogSimulacao log = new LogSimulacao(getNome(), numMolduras)) {
            faltas = simular(referencias, numMolduras, log);
        }

        try (FileWriter csv = new FileWriter("resultados.csv", true)) {  // true = acrescenta no fim
            csv.write(numMolduras + "," + getNome() + "," + faltas + "\n");
        }
        return faltas;
    }
}
