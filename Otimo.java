import java.util.ArrayList;
import java.util.List;

public class Otimo implements AlgoritmoSubstituicao {

    public String getNome() { return "Ótimo"; }

    public int simular(int[] referencias, int numMolduras) {
        List<Integer> molduras = new ArrayList<>();
        int faltas = 0;

        for (int i = 0; i < referencias.length; i++) {
            int pagina = referencias[i];
            if (molduras.contains(pagina)) continue;    // acerto

            faltas++;
            if (molduras.size() == numMolduras) {       // sai a de uso mais distante
                int vitima = molduras.get(0);
                int maisDistante = -1;
                for (int p : molduras) {
                    int proximo = proximoUso(referencias, i, p);
                    if (proximo > maisDistante) {
                        maisDistante = proximo;
                        vitima = p;
                    }
                }
                molduras.remove(Integer.valueOf(vitima));
            }
            molduras.add(pagina);
        }
        return faltas;
    }

    private int proximoUso(int[] referencias, int atual, int pagina) {
        for (int j = atual + 1; j < referencias.length; j++) {
            if (referencias[j] == pagina) return j;
        }
        return Integer.MAX_VALUE;                       // nunca mais será usada
    }
}