import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LRU implements AlgoritmoSubstituicao {

    public String getNome() { return "LRU"; }

    public int simular(int[] referencias, int numMolduras, LogSimulacao log) {
        List<Integer> molduras = new ArrayList<>();
        Map<Integer, Integer> ultimoUso = new HashMap<>();
        int faltas = 0;

        for (int i = 0; i < referencias.length; i++) {
            int pagina = referencias[i];

            if (molduras.contains(pagina)) {
                log.acerto(i, pagina, molduras, "último uso no passo " + ultimoUso.get(pagina));
            } else {
                faltas++;
                if (molduras.size() == numMolduras) {   // sai a usada há mais tempo
                    int vitima = molduras.get(0);
                    for (int p : molduras) {
                        if (ultimoUso.get(p) < ultimoUso.get(vitima)) vitima = p;
                    }
                    int usoVitima = ultimoUso.get(vitima);
                    molduras.remove(Integer.valueOf(vitima));
                    ultimoUso.remove(vitima);
                    log.retirada(i, vitima, molduras, "menos usada recentemente (último uso no passo " + usoVitima + ")");
                }
                molduras.add(pagina);
                log.adicionada(i, pagina, molduras, "falta de página");
            }
            ultimoUso.put(pagina, i);                   // atualiza em acerto e em falta
        }
        return faltas;
    }
}
