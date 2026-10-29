import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class FIFO implements AlgoritmoSubstituicao {

    public String getNome() { return "FIFO"; }

    public int simular(int[] referencias, int numMolduras, LogSimulacao log) {
        Queue<Integer> fila = new LinkedList<>();
        Set<Integer> naMemoria = new HashSet<>();
        int faltas = 0;

        for (int i = 0; i < referencias.length; i++) {
            int pagina = referencias[i];

            if (naMemoria.contains(pagina)) {           // acerto
                log.acerto(i, pagina, fila, "página já estava na memória");
                continue;
            }

            faltas++;
            if (fila.size() == numMolduras) {           // memória cheia: sai a mais antiga
                int vitima = fila.poll();
                naMemoria.remove(vitima);
                log.retirada(i, vitima, fila, "mais antiga na fila");
            }
            fila.add(pagina);
            naMemoria.add(pagina);
            log.adicionada(i, pagina, fila, "falta de página: entra no fim da fila");
        }
        return faltas;
    }
}
