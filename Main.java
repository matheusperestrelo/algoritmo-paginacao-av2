import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner teclado = new Scanner(System.in);

        // 1. Sequência de páginas
        System.out.println("Digite a sequência de páginas separadas por espaço");
        System.out.println("(Enter vazio usa o exemplo: 7 0 1 2 0 3 0 4 2 3 0 3 2 1 2 0 1 7 0 1)");
        System.out.print("> ");
        String linha = teclado.nextLine().trim();
        if (linha.isEmpty()) {
            linha = "7 0 1 2 0 3 0 4 2 3 0 3 2 1 2 0 1 7 0 1";
        }

        String[] partes = linha.split("\\s+");
        int[] sequencia = new int[partes.length];
        for (int i = 0; i < partes.length; i++) {
            try {
                sequencia[i] = Integer.parseInt(partes[i]);
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido na sequência: " + partes[i]);
                return;
            }
        }

        // 2. Número de molduras
        System.out.print("Quantas molduras? ");
        int molduras;
        try {
            molduras = Integer.parseInt(teclado.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Número de molduras inválido.");
            return;
        }
        if (molduras <= 0) {
            System.out.println("O número de molduras deve ser maior que zero.");
            return;
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
        System.out.println("Referências: " + sequencia.length);
        System.out.println("Molduras: " + molduras);
        System.out.println("------------------------------------");

        DefaultCategoryDataset dados = new DefaultCategoryDataset();

        for (AlgoritmoSubstituicao algoritmo : algoritmos) {
            int faltas = algoritmo.simularESalvar(sequencia, molduras);

            System.out.println(algoritmo.getNome() + ": " + faltas + " faltas de página");
            dados.addValue(faltas, "Faltas", algoritmo.getNome());
        }

        // 3. Gráfico de colunas salvo como imagem
        JFreeChart grafico = ChartFactory.createBarChart(
            "Faltas de Página por Algoritmo",
            "Algoritmo",
            "Quantidade de faltas",
            dados,
            PlotOrientation.VERTICAL,
            false, true, false);
        ChartUtils.saveChartAsPNG(new File("grafico_faltas.png"), grafico, 700, 450);

        System.out.println("------------------------------------");
        System.out.println("Simulação concluída!");
        System.out.println("Arquivos gerados:");
        System.out.println("- resultados.csv");
        System.out.println("- log_execucao.csv");
        System.out.println("- grafico_faltas.png");
    }
}
