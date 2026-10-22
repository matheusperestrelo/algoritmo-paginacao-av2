import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class FIFO implements AlgoritmoSubstituicao {

    public String getNome() { return "FIFO"; }

    public int simular(int[] referencias, int numMolduras) {
        Queue<Integer> fila = new LinkedList<>();
        Set<Integer> naMemoria = new HashSet<>();
        int faltas = 0;

        for (int pagina : referencias) {
            if (naMemoria.contains(pagina)) continue;   // acerto

            faltas++;
            if (fila.size() == numMolduras) {           // memória cheia: sai a mais antiga
                naMemoria.remove(fila.poll());
            }
            fila.add(pagina);
            naMemoria.add(pagina);
        }
        return faltas;
    }
}