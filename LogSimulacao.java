import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.StringJoiner;

// Grava no CSV de log cada evento de um passo da simulação (os "metadados" do algoritmo)
public class LogSimulacao implements AutoCloseable {

    public static final String ARQUIVO = "log_execucao.csv";
    private static final String CABECALHO =
        "timestamp,tempo_us,algoritmo,molduras,passo,pagina,evento,faltas_acumuladas,memoria,detalhe\n";
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    // Log que não grava nada, para quem chama simular() sem querer log
    public static final LogSimulacao VAZIO = new LogSimulacao();

    private final FileWriter csv;
    private final String algoritmo;
    private final int molduras;
    private final long inicio = System.nanoTime();
    private int faltas = 0;

    private LogSimulacao() {
        this.csv = null;
        this.algoritmo = "";
        this.molduras = 0;
    }

    public LogSimulacao(String algoritmo, int molduras) throws IOException {
        this.csv = new FileWriter(ARQUIVO, true);       // true = acrescenta no fim
        this.algoritmo = algoritmo;
        this.molduras = molduras;
    }

    // Cria o arquivo do zero, só com o cabeçalho
    public static void iniciarArquivo() throws IOException {
        try (FileWriter csv = new FileWriter(ARQUIVO)) {
            csv.write(CABECALHO);
        }
    }

    public void acerto(int passo, int pagina, Collection<Integer> memoria, String detalhe) {
        escrever(passo, pagina, "ACERTO", memoria, detalhe);
    }

    // Conta a falta e registra a página entrando na memória
    public void adicionada(int passo, int pagina, Collection<Integer> memoria, String detalhe) {
        faltas++;
        escrever(passo, pagina, "ADICIONADA", memoria, detalhe);
    }

    public void retirada(int passo, int pagina, Collection<Integer> memoria, String detalhe) {
        escrever(passo, pagina, "RETIRADA", memoria, detalhe);
    }

    // Relógio: página teve o bit zerado e ganhou mais uma volta
    public void segundaChance(int passo, int pagina, Collection<Integer> memoria, String detalhe) {
        escrever(passo, pagina, "SEGUNDA_CHANCE", memoria, detalhe);
    }

    private void escrever(int passo, int pagina, String evento, Collection<Integer> memoria, String detalhe) {
        if (csv == null) return;
        long tempoUs = (System.nanoTime() - inicio) / 1_000;
        String linha = String.join(",",
            LocalDateTime.now().format(FORMATO),
            String.valueOf(tempoUs),
            algoritmo,
            String.valueOf(molduras),
            String.valueOf(passo),
            String.valueOf(pagina),
            evento,
            String.valueOf(faltas),
            formatarMemoria(memoria),
            detalhe.replace(",", ";"));             // vírgula quebraria o CSV
        try {
            csv.write(linha + "\n");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // Ex.: [1|2|-] (o "-" é moldura vazia)
    private String formatarMemoria(Collection<Integer> memoria) {
        StringJoiner sj = new StringJoiner("|", "[", "]");
        for (int p : memoria) sj.add(p == -1 ? "-" : String.valueOf(p));
        return sj.toString();
    }

    @Override
    public void close() throws IOException {
        if (csv != null) csv.close();
    }
}
