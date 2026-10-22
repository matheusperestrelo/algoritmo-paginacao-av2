import java.util.Arrays;

public class Relogio implements AlgoritmoSubstituicao {

    public String getNome() { return "Relógio"; }

    public int simular(int[] referencias, int numMolduras) {
        int[] molduras = new int[numMolduras];
        boolean[] bitRef = new boolean[numMolduras];
        Arrays.fill(molduras, -1);                      // -1 = moldura vazia
        int ponteiro = 0;
        int faltas = 0;

        for (int pagina : referencias) {
            int posicao = indiceDe(molduras, pagina);

            if (posicao != -1) {                        // acerto: ganha segunda chance
                bitRef[posicao] = true;
                continue;
            }

            faltas++;
            while (bitRef[ponteiro]) {                  // pula quem tem bit 1, zerando
                bitRef[ponteiro] = false;
                ponteiro = (ponteiro + 1) % numMolduras;
            }
            molduras[ponteiro] = pagina;
            bitRef[ponteiro] = true;
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
}