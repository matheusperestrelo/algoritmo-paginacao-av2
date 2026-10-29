
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class InterfaceGrafica extends JFrame {

    private final JTextField campoReferencias;
    private final JSpinner campoMolduras;
    private final DefaultTableModel modeloTabela;
    private final JLabel mensagemStatus;
    private final DefaultCategoryDataset dadosColunas = new DefaultCategoryDataset();
    private final DefaultPieDataset<String> dadosSetores = new DefaultPieDataset<>();

    private final AlgoritmoSubstituicao[] algoritmos = {
            new FIFO(),
            new LRU(),
            new Relogio(),
            new Otimo()
    };

    public InterfaceGrafica() {
        setTitle("Simulador de Algoritmos de Substituição de Páginas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 520);
        setMinimumSize(new java.awt.Dimension(700, 420));
        setLocationRelativeTo(null);

        // Painel principal
        JPanel principal = new JPanel(new BorderLayout(12, 12));
        principal.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(principal);

        // Cabeçalho
        JLabel titulo = new JLabel("Simulador de Paginação");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));

        JLabel subtitulo = new JLabel(
                "Compare FIFO, LRU, Relógio e Ótimo");

        JPanel cabecalho = new JPanel(new GridLayout(2, 1, 0, 5));
        cabecalho.add(titulo);
        cabecalho.add(subtitulo);

        principal.add(cabecalho, BorderLayout.NORTH);

        // Área de entrada
        campoReferencias = new JTextField(
                "1, 2, 3, 4, 1, 2, 5, 1, 2, 3, 4, 5", 28);

        campoMolduras = new JSpinner(
                new SpinnerNumberModel(3, 1, 100, 1));

        JButton botaoExecutar = new JButton("Executar simulação");
        botaoExecutar.addActionListener(e -> executarSimulacao());

        JPanel formulario = new JPanel(new GridLayout(0, 1, 0, 8));
        formulario.setBorder(
                BorderFactory.createTitledBorder("Configuração"));

        formulario.add(new JLabel(
                "Referências de páginas (separadas por vírgula ou espaço):"));
        formulario.add(campoReferencias);

        JPanel linhaMolduras = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linhaMolduras.add(new JLabel("Número de molduras:"));
        linhaMolduras.add(campoMolduras);
        linhaMolduras.add(botaoExecutar);

        formulario.add(linhaMolduras);

        // Tabela de resultados
        modeloTabela = new DefaultTableModel(
                new Object[] {
                        "Algoritmo",
                        "Faltas de página",
                        "Acertos",
                        "Taxa de faltas (%)"
                },
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabela = new JTable(modeloTabela);
        tabela.setRowHeight(28);
        tabela.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12));

        JScrollPane scrollTabela = new JScrollPane(tabela);
        scrollTabela.setBorder(
                BorderFactory.createTitledBorder("Resultados"));

        // Gráfico de colunas
        JFreeChart graficoColunas = ChartFactory.createBarChart(
                "Faltas de Página por Algoritmo",
                "Algoritmo",
                "Quantidade de faltas",
                dadosColunas,
                PlotOrientation.VERTICAL,
                false,
                true,
                false);

        // Gráfico de setores
        JFreeChart graficoSetores = ChartFactory.createPieChart(
                "Participação nas Faltas de Página",
                dadosSetores,
                true,
                true,
                false);

        JTabbedPane abas = new JTabbedPane();

        abas.addTab("Tabela de resultados", scrollTabela);
        abas.addTab("Gráfico de colunas", new ChartPanel(graficoColunas));
        abas.addTab("Gráfico de setores", new ChartPanel(graficoSetores));

        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.add(formulario, BorderLayout.NORTH);
        centro.add(abas, BorderLayout.CENTER);

        principal.add(centro, BorderLayout.CENTER);

        // Mensagem inferior
        mensagemStatus = new JLabel(
                "Informe os dados e execute a simulação.");
        principal.add(mensagemStatus, BorderLayout.SOUTH);

        // Permite executar com Enter no campo de referências
        campoReferencias.addActionListener(e -> executarSimulacao());
    }

    private void executarSimulacao() {
        try {
            String entrada = campoReferencias.getText().trim();

            if (entrada.isEmpty()) {
                throw new IllegalArgumentException(
                        "Informe a sequência de páginas.");
            }

            String[] valores = entrada.split("[,;\\s]+");
            int[] referencias = new int[valores.length];

            for (int i = 0; i < valores.length; i++) {
                referencias[i] = Integer.parseInt(valores[i]);
            }

            int numMolduras = (Integer) campoMolduras.getValue();

            modeloTabela.setRowCount(0);
            dadosColunas.clear();
            dadosSetores.clear();

            int totalFaltas = 0;
            int[] faltasPorAlgoritmo = new int[algoritmos.length];

            for (int i = 0; i < algoritmos.length; i++) {
                AlgoritmoSubstituicao algoritmo = algoritmos[i];

                int faltas = algoritmo.simular(referencias, numMolduras);
                int acertos = referencias.length - faltas;
                double taxaFaltas = 100.0 * faltas / referencias.length;

                modeloTabela.addRow(new Object[] {
                        algoritmo.getNome(),
                        faltas,
                        acertos,
                        String.format("%.2f", taxaFaltas)
                });

                // Dados do gráfico de colunas
                dadosColunas.addValue(
                        faltas,
                        "Faltas",
                        algoritmo.getNome());

                faltasPorAlgoritmo[i] = faltas;
                totalFaltas += faltas;
            }

            // Dados do gráfico de setores
            for (int i = 0; i < algoritmos.length; i++) {
                double percentual = 100.0 * faltasPorAlgoritmo[i] / totalFaltas;

                dadosSetores.setValue(
                        algoritmos[i].getNome(),
                        faltasPorAlgoritmo[i]);
            }

            mensagemStatus.setText(
                    "Simulação concluída: "
                            + referencias.length
                            + " referências e "
                            + numMolduras
                            + " molduras.");

        } catch (NumberFormatException ex) {
            mostrarErro(
                    "Use apenas números inteiros, separados por vírgulas "
                            + "ou espaços.");
        } catch (IllegalArgumentException ex) {
            mostrarErro(ex.getMessage());
        } catch (RuntimeException ex) {
            mostrarErro(
                    "Não foi possível executar a simulação: "
                            + ex.getMessage());
        }
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(
                this,
                mensagem,
                "Dados inválidos",
                JOptionPane.ERROR_MESSAGE);

        mensagemStatus.setText("Corrija os dados e tente novamente.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InterfaceGrafica interfaceGrafica = new InterfaceGrafica();
            interfaceGrafica.setVisible(true);
        });
    }
}