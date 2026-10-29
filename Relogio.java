import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Relogio implements AlgoritmoSubstituicao {

    public String getNome() { return "Relógio"; }

    public int simular(int[] referencias, int numMolduras, LogSimulacao log) {
        int[] molduras = new int[numMolduras];
        boolean[] bitRef = new boolean[numMolduras];
        Arrays.fill(molduras, -1);                      // -1 = moldura vazia
        int ponteiro = 0;
        int faltas = 0;

        for (int i = 0; i < referencias.length; i++) {
            int pagina = referencias[i];
            int posicao = indiceDe(molduras, pagina);

            if (posicao != -1) {                        // acerto: ganha segunda chance
                bitRef[posicao] = true;
                log.acerto(i, pagina, lista(molduras), "moldura " + posicao + ": bit de referência = 1");
                continue;
            }

            faltas++;
            while (bitRef[ponteiro]) {                  // pula quem tem bit 1, zerando
                bitRef[ponteiro] = false;
                log.segundaChance(i, molduras[ponteiro], lista(molduras), "moldura " + ponteiro + ": bit 1 -> 0");
                ponteiro = (ponteiro + 1) % numMolduras;
            }
            int vitima = molduras[ponteiro];
            if (vitima != -1) {
                molduras[ponteiro] = -1;
                log.retirada(i, vitima, lista(molduras), "moldura " + ponteiro + ": bit 0 sob o ponteiro");
            }
            molduras[ponteiro] = pagina;
            bitRef[ponteiro] = true;
            log.adicionada(i, pagina, lista(molduras), "falta de página: entra na moldura " + ponteiro);
            ponteiro = (ponteiro + 1) % numMolduras;
        }
        return faltas;
    }

    private int indiceDe(int[] molduras, int pagina) {
        for (int i = 0; i < molduras.length; i++) {
            if (molduras[i] == pagina) return i;
        }
        return -1;
    }

    private List<Integer> lista(int[] molduras) {
        List<Integer> l = new ArrayList<>();
        for (int p : molduras) l.add(p);
        return l;
    }
}
