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

import java.io.IOException;
import java.util.Map;

public class InterfaceGrafica extends JFrame {

        private final DefaultTableModel modeloTabela;
        private final JLabel mensagemStatus;
        private final DefaultCategoryDataset dadosColunas = new DefaultCategoryDataset();
        private final DefaultPieDataset<String> dadosSetores = new DefaultPieDataset<>();

        private final AlgoritmoSubstituicao[] algoritmos = {new FIFO(), new LRU(), new Relogio(), new Otimo()};

        public InterfaceGrafica() {
                setTitle("Simulador de Algoritmos de Substituição de Páginas");
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setSize(850, 520);
                setMinimumSize(new java.awt.Dimension(700, 420));
                setLocationRelativeTo(null);

                // Painel principal
                JPanel principal = new JPanel(new BorderLayout(12, 12));
                principal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                setContentPane(principal);

                // Cabeçalho
                JLabel titulo = new JLabel("Simulador de Paginação");
                titulo.setFont(new Font("SansSerif", Font.BOLD, 24));

                JLabel subtitulo = new JLabel("Resultados registrados em log_execucao.csv");

                JPanel cabecalho = new JPanel(new GridLayout(2, 1, 0, 5));
                cabecalho.add(titulo);
                cabecalho.add(subtitulo);

                principal.add(cabecalho, BorderLayout.NORTH);

                JButton botaoExecutar = new JButton("Carregar resultados do CSV");
                botaoExecutar.addActionListener(e -> carregarResultadosCSV());

                JPanel formulario = new JPanel(new GridLayout(0, 1, 0, 8));
                formulario.setBorder(BorderFactory.createTitledBorder("Configuração"));

                JPanel linhaMolduras = new JPanel(new FlowLayout(FlowLayout.LEFT));
                linhaMolduras.add(botaoExecutar);
                formulario.add(linhaMolduras);

                // Tabela de resultados
                modeloTabela = new DefaultTableModel(
                        new Object[] {
                                "Algoritmo",
                                "Faltas de página",
                                "Acertos",
                                "Taxa de faltas (%)"
                        }, 0) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                                return false;
                        }
                };

                JTable tabela = new JTable(modeloTabela);
                tabela.setRowHeight(28);
                tabela.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

                JScrollPane scrollTabela = new JScrollPane(tabela);
                scrollTabela.setBorder(BorderFactory.createTitledBorder("Resultados"));

                // Gráfico de colunas
                JFreeChart graficoColunas = ChartFactory.createBarChart(
                        "Faltas de Página por Algoritmo",
                        "Algoritmo",
                        "Quantidade de faltas", dadosColunas, PlotOrientation.VERTICAL, false, true, 
                        false);

                // Gráfico de setores
                JFreeChart graficoSetores = ChartFactory.createPieChart("Participação nas Faltas de Página", dadosSetores, true, true, false);
                JTabbedPane abas = new JTabbedPane();

                abas.addTab("Tabela de resultados", scrollTabela);
                abas.addTab("Gráfico de colunas", new ChartPanel(graficoColunas));
                abas.addTab("Gráfico de setores", new ChartPanel(graficoSetores));

                JPanel centro = new JPanel(new BorderLayout(10, 10));
                centro.add(formulario, BorderLayout.NORTH);
                centro.add(abas, BorderLayout.CENTER);

                principal.add(centro, BorderLayout.CENTER);

                // Mensagem inferior
                mensagemStatus = new JLabel("Informe os dados e execute a simulação.");
                principal.add(mensagemStatus, BorderLayout.SOUTH);
        }

        private void carregarResultadosCSV() {
                try {
                        int numMolduras = LeitorLogCSV.carregarNumeroMolduras();

                        Map<String, LeitorLogCSV.Resultado> resultados = LeitorLogCSV.carregarResultados(numMolduras);

                        if (resultados.isEmpty()) {
                                mostrarErro("Nenhum resultado encontrado para " + numMolduras + " molduras.");
                                return;
                        }

                        modeloTabela.setRowCount(0);
                        dadosColunas.clear();
                        dadosSetores.clear();

                        String[] ordemAlgoritmos = { "FIFO", "LRU", "Relógio", "Ótimo" };

                        int totalFaltas = 0;

                        for (String nome : ordemAlgoritmos) {
                                LeitorLogCSV.Resultado resultado = resultados.get(nome);

                                if (resultado != null) {
                                        totalFaltas += resultado.faltas;
                                }
                        }

                        for (String nome : ordemAlgoritmos) {
                                LeitorLogCSV.Resultado resultado = resultados.get(nome);

                                if (resultado == null) {
                                        continue;
                                }

                                modeloTabela.addRow(new Object[] {
                                        nome,
                                        resultado.faltas,
                                        resultado.getAcertos(),
                                        String.format("%.2f", resultado.getTaxaFaltas())
                                });

                                dadosColunas.addValue(resultado.faltas, "Faltas", nome);
                                dadosSetores.setValue(nome, resultado.faltas);
                        }

                        mensagemStatus.setText("Resultados carregados do CSV para " + numMolduras + " molduras.");

                } catch (IOException | NumberFormatException ex) {
                        mostrarErro("Erro ao ler o CSV: " + ex.getMessage());
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