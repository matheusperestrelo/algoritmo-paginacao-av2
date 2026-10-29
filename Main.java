import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws IOException {
        int molduras = 5;
        int quantidadeReferencias = 5000;
        String cenarioEscolhido = "localidade";

        int[] referencias = {
            7, 0, 1, 2, 0, 3, 0, 4, 2, 3,
            0, 3, 2, 1, 2, 0, 1, 7, 0, 1
        };

        int[] misto = {
            1, 2, 3, 4, 2, 1, 5, 6, 2, 1,
            2, 3, 7, 6, 3, 2, 1, 2, 3, 6,
            4, 5, 6, 7, 1, 2, 4, 5, 3, 2
        };

        int[] localidade = gerarReferenciasLocalidade(
            quantidadeReferencias,
            50,       
            8,        
            12345L 
        );

        int[] ciclico = {
            1, 2, 3, 4, 5, 1, 2, 3, 4, 5,
            1, 2, 3, 4, 5, 1, 2, 3, 4, 5,
            1, 2, 3, 4, 5, 1, 2, 3, 4, 5
        };

        int[] fases = {
            1, 2, 3, 1, 2, 3, 1, 2, 3,
            4, 5, 6, 4, 5, 6, 4, 5, 6,
            7, 8, 9, 7, 8, 9, 7, 8, 9,
            1, 2, 3, 1, 2, 3, 4, 5, 6,
            4, 5, 6, 1
        };

        int[] belady = {
            1, 2, 3, 4, 1, 2, 5, 1, 2, 3, 4, 5
        };

        int[] sequencia;

        switch (cenarioEscolhido.toLowerCase()) {
            case "misto":
                sequencia = misto;
                break;

            case "localidade":
                sequencia = localidade;
                break;

            case "ciclico":
                sequencia = ciclico;
                break;

            case "fases":
                sequencia = fases;
                break;

            case "belady":
                sequencia = belady;
                break;

            default:
                throw new IllegalArgumentException(
                    "Cenário desconhecido: " + cenarioEscolhido
                );
        }

        try (FileWriter csv = new FileWriter("resultados.csv")) {
            csv.write("molduras,algoritmo,faltas\n");
        }

        LogSimulacao.iniciarArquivo();

        AlgoritmoSubstituicao[] algoritmos = {
            new FIFO(),
            new LRU(),
            new Relogio(),
            new Otimo()
        };

        System.out.println("====================================");
        System.out.println(" SIMULADOR DE PAGINACAO");
        System.out.println("====================================");
        System.out.println("Cenário: " + cenarioEscolhido);
        System.out.println("Referências: " + sequencia.length);
        System.out.println("Molduras: " + molduras);
        System.out.println("------------------------------------");

        for (AlgoritmoSubstituicao algoritmo : algoritmos) {

            int faltas = algoritmo.simularESalvar(
                sequencia,
                molduras
            );

            System.out.println(
                algoritmo.getClass().getSimpleName()
                + ": "
                + faltas
                + " faltas de página"
            );
        }

        System.out.println("------------------------------------");
        System.out.println("Simulação concluída!");
        System.out.println("Arquivos gerados:");
        System.out.println("- resultados.csv");
        System.out.println("- log_execucao.csv");
    }

    private static int[] gerarReferenciasLocalidade(
            int quantidade,
            int totalPaginas,
            int tamanhoGrupo,
            long semente) {

        if (quantidade <= 0 || totalPaginas <= 0
                || tamanhoGrupo <= 0
                || tamanhoGrupo > totalPaginas) {
            throw new IllegalArgumentException(
                "Parâmetros inválidos para gerar referências."
            );
        }

        Random random = new Random(semente);
        int[] referencias = new int[quantidade];

        int inicioGrupo = 0;

        for (int i = 0; i < quantidade; i++) {
            // A cada 100 referências, escolhe outro grupo.
            if (i % 100 == 0) {
                int maxInicio = totalPaginas - tamanhoGrupo + 1;
                inicioGrupo = random.nextInt(maxInicio);
            }

            // 85% dos acessos ficam no grupo local.
            if (random.nextDouble() < 0.85) {

                referencias[i] = inicioGrupo
                    + random.nextInt(tamanhoGrupo);

            } else {
                // 15% dos acessos podem atingir qualquer página.
                referencias[i] = random.nextInt(totalPaginas);
            }
        }

        return referencias;
    }
}
