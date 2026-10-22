import java.io.FileWriter;
import java.io.IOException;



public class Main {
    public static void main(String[] args) throws IOException {
        int[] referencias = {7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2, 1, 2, 0, 1, 7, 0, 1};
        // 1. Uso geral misto (30 referências)
        int[] misto = {1,2,3,4,2,1,5,6,2,1,2,3,7,6,3,2,1,2,3,6,4,5,6,7,1,2,4,5,3,2};

        // 2. Localidade forte: páginas 1 e 2 muito usadas (40 referências)
        int[] localidade = {1,2,3,1,2,4,1,2,3,5,1,2,4,1,3,2,6,1,2,3,7,1,2,4,3,1,8,2,1,3,
                            5,2,1,4,3,2,1,6,2,1};

        // 3. Laço cíclico: 5 páginas repetidas em ordem (30 referências)
        int[] ciclico = {1,2,3,4,5,1,2,3,4,5,1,2,3,4,5,1,2,3,4,5,1,2,3,4,5,1,2,3,4,5};

        // 4. Fases: o programa muda de grupo de páginas (40 referências)
        int[] fases = {1,2,3,1,2,3,1,2,3,4,5,6,4,5,6,4,5,6,7,8,9,7,8,9,7,8,9,1,2,3,
                    1,2,3,4,5,6,4,5,6,1};

        // 5. Anomalia de Belady (12 referências)
        int[] belady = {1,2,3,4,1,2,5,1,2,3,4,5};
        int molduras = 10;

        // Cria o arquivo do zero, só com o cabeçalho
        try (FileWriter csv = new FileWriter("resultados.csv")) {
            csv.write("molduras,algoritmo,faltas\n");
        }

        AlgoritmoSubstituicao[] algoritmos = { new FIFO(), new LRU(), new Relogio(), new Otimo() };

        for (int i = 0; i < algoritmos.length; i++) {
            int faltas = algoritmos[i].simularESalvar(localidade, molduras);
            System.out.println("Método " + (i + 1) + " - " + faltas + " faltas de página");
        }
    }
}