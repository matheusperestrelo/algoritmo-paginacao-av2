import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class LeitorLogCSV {
    private static final String ARQUIVO = "log_execucao.csv";

    public static class Resultado {
        public final int faltas;
        public final int referencias;

        public Resultado(int faltas, int referencias) {
            this.faltas = faltas;
            this.referencias = referencias;
        }

        public int getAcertos() {
            return referencias - faltas;
        }

        public double getTaxaFaltas() {
            if (referencias == 0) {
                return 0;
            }

            return 100.0 * faltas / referencias;
        }
    }

    private static class Parcial {
        int faltas;
        int referencias;
    }

    public static int carregarNumeroMolduras() throws IOException {
        try (BufferedReader leitor =
                     Files.newBufferedReader(Path.of(ARQUIVO))) {

            String linha = leitor.readLine(); // Cabeçalho

            while ((linha = leitor.readLine()) != null) {
                String[] colunas = linha.split(",", 10);

                if (colunas.length >= 4) {
                    try {
                        return Integer.parseInt(colunas[3].trim());
                    } catch (NumberFormatException e) {
                        throw new IOException(
                                "Número de molduras inválido no CSV.", e);
                    }
                }
            }
        }

        throw new IOException(
                "O arquivo log_execucao.csv não possui registros.");
    }

    public static Map<String, Resultado> carregarResultados(int molduras)
            throws IOException {

        Map<String, Parcial> parciais = new LinkedHashMap<>();

        try (BufferedReader leitor =
                     Files.newBufferedReader(Path.of(ARQUIVO))) {

            String linha = leitor.readLine(); // Ignora o cabeçalho

            while ((linha = leitor.readLine()) != null) {
                String[] colunas = linha.split(",", 10);

                if (colunas.length < 8) {
                    continue;
                }

                try {
                    String algoritmo = colunas[2].trim();
                    int moldurasRegistro =
                            Integer.parseInt(colunas[3].trim());
                    int passo = Integer.parseInt(colunas[4].trim());
                    int faltasAcumuladas =
                            Integer.parseInt(colunas[7].trim());

                    if (moldurasRegistro != molduras) {
                        continue;
                    }

                    Parcial parcial = parciais.computeIfAbsent(
                            algoritmo, chave -> new Parcial());

                    parcial.referencias = Math.max(
                            parcial.referencias, passo + 1);

                    parcial.faltas = Math.max(
                            parcial.faltas, faltasAcumuladas);

                } catch (NumberFormatException e) {
                    throw new IOException(
                            "Registro inválido no arquivo log_execucao.csv: "
                                    + linha, e);
                }
            }
        }

        Map<String, Resultado> resultados = new LinkedHashMap<>();

        for (Map.Entry<String, Parcial> entrada : parciais.entrySet()) {
            Parcial parcial = entrada.getValue();

            resultados.put(
                    entrada.getKey(),
                    new Resultado(
                            parcial.faltas,
                            parcial.referencias));
        }

        if (resultados.isEmpty()) {
            throw new IOException(
                    "Nenhum resultado encontrado para "
                            + molduras + " molduras.");
        }

        return resultados;
    }
}
