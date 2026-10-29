import java.util.ArrayList;
import java.util.List;

public class Otimo implements AlgoritmoSubstituicao {

    public String getNome() { return "Ótimo"; }

    public int simular(int[] referencias, int numMolduras, LogSimulacao log) {
        List<Integer> molduras = new ArrayList<>();
        int faltas = 0;

        for (int i = 0; i < referencias.length; i++) {
            int pagina = referencias[i];
            if (molduras.contains(pagina)) {            // acerto
                log.acerto(i, pagina, molduras, "página já estava na memória");
                continue;
            }

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
                String motivo = maisDistante == Integer.MAX_VALUE
                    ? "não será mais usada"
                    : "próximo uso mais distante (passo " + maisDistante + ")";
                log.retirada(i, vitima, molduras, motivo);
            }
            molduras.add(pagina);
            log.adicionada(i, pagina, molduras, "falta de página");
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
