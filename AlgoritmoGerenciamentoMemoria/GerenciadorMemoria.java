// GerenciadorMemoria.java
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GerenciadorMemoria {

    private final int tamanhoTotal;
    private final int[] mapaDeBits;
    private final Map<String, InfoProcesso> processosAlocados;
    private int ultimaPosicao;

    public GerenciadorMemoria(int tamanhoTotal) {
        if (tamanhoTotal <= 0) {
            throw new IllegalArgumentException("O tamanho da memória deve ser um valor positivo.");
        }
        this.tamanhoTotal = tamanhoTotal;
        this.mapaDeBits = new int[tamanhoTotal];
        this.processosAlocados = new HashMap<>();
        this.ultimaPosicao = 0;
    }

    public void exibirMapa() {
        System.out.print("Mapa de Bits: ");
        for (int bloco : mapaDeBits) {
            System.out.print(bloco);
        }
        System.out.println();
    }

    public boolean alocar(String pid, int tamanho, Algoritmo algoritmo) {
        if (processosAlocados.containsKey(pid)) {
            System.out.printf("Erro: O processo %s já está alocado.%n", pid);
            return false;
        }
        if (tamanho <= 0) {
            System.out.printf("Erro: O tamanho do processo %s deve ser positivo.%n", pid);
            return false;
        }

        int indiceInicial = -1;
        switch (algoritmo) {
            case PRIMEIRO_ENCAIXE -> indiceInicial = primeiroEncaixe(tamanho);
            case PROXIMO_ENCAIXE  -> indiceInicial = proximoEncaixe(tamanho);
            case MELHOR_ENCAIXE   -> indiceInicial = melhorEncaixe(tamanho);
            case PIOR_ENCAIXE     -> indiceInicial = piorEncaixe(tamanho);
            case ENCAIXE_RAPIDO   -> indiceInicial = encaixeRapido(tamanho);
        }

        if (indiceInicial != -1) {
            Arrays.fill(mapaDeBits, indiceInicial, indiceInicial + tamanho, 1);
            processosAlocados.put(pid, new InfoProcesso(indiceInicial, tamanho));
            ultimaPosicao = (indiceInicial + tamanho) % tamanhoTotal;
            System.out.printf("Sucesso: Processo %s (tamanho %d) alocado no bloco %d usando %s.%n",
                pid, tamanho, indiceInicial, algoritmo);
            return true;
        } else {
            System.out.printf("Falha: Não há espaço contíguo para o Processo %s (tamanho %d) usando %s.%n",
                pid, tamanho, algoritmo);
            return false;
        }
    }

    public void desalocar(String pid) {
        if (!processosAlocados.containsKey(pid)) {
            System.out.printf("Erro: Processo %s não encontrado para desalocação.%n", pid);
            return;
        }

        InfoProcesso info = processosAlocados.get(pid);
        Arrays.fill(mapaDeBits, info.inicio(), info.inicio() + info.tamanho(), 0);
        processosAlocados.remove(pid);
        System.out.printf("Sucesso: Processo %s desalocado. %d blocos liberados a partir do índice %d.%n",
            pid, info.tamanho(), info.inicio());
    }

    public void calcularFragmentacao() {
        List<Lacuna> lacunas = encontrarLacunas();
        int memoriaLivreTotal = lacunas.stream().mapToInt(Lacuna::tamanho).sum();
        
        if (memoriaLivreTotal == 0) {
            System.out.println("Estatísticas: A memória está totalmente ocupada.");
            return;
        }

        int maiorLacuna = lacunas.stream().mapToInt(Lacuna::tamanho).max().orElse(0);

        System.out.println("--- Estatísticas de Fragmentação ---");
        System.out.printf("Memória livre total: %d blocos%n", memoriaLivreTotal);
        System.out.printf("Número de lacunas (fragmentos): %d%n", lacunas.size());
        System.out.printf("Tamanho da maior lacuna contígua: %d blocos%n", maiorLacuna);
    }
    
    private List<Lacuna> encontrarLacunas() {
        List<Lacuna> lacunas = new ArrayList<>();
        int i = 0;
        while (i < tamanhoTotal) {
            if (mapaDeBits[i] == 0) {
                int inicio = i;
                int contador = 0;
                while (i < tamanhoTotal && mapaDeBits[i] == 0) {
                    contador++;
                    i++;
                }
                lacunas.add(new Lacuna(inicio, contador));
            } else {
                i++;
            }
        }
        return lacunas;
    }

    private boolean isRegiaoLivre(int inicio, int tamanho) {
        for (int i = 0; i < tamanho; i++) {
            if (mapaDeBits[inicio + i] == 1) return false;
        }
        return true;
    }

    private int primeiroEncaixe(int tamanho) {
        for (int i = 0; i <= tamanhoTotal - tamanho; i++) {
            if (isRegiaoLivre(i, tamanho)) return i;
        }
        return -1;
    }

    private int proximoEncaixe(int tamanho) {
        for (int i = ultimaPosicao; i <= tamanhoTotal - tamanho; i++) {
            if (isRegiaoLivre(i, tamanho)) return i;
        }
        for (int i = 0; i < ultimaPosicao && i <= tamanhoTotal - tamanho; i++) {
            if (isRegiaoLivre(i, tamanho)) return i;
        }
        return -1;
    }

    private int melhorEncaixe(int tamanho) {
        List<Lacuna> lacunas = encontrarLacunas();
        Lacuna melhorLacuna = new Lacuna(-1, Integer.MAX_VALUE);
        
        for (Lacuna lacuna : lacunas) {
            if (lacuna.tamanho() >= tamanho && lacuna.tamanho() < melhorLacuna.tamanho()) {
                melhorLacuna = lacuna;
            }
        }
        return melhorLacuna.inicio();
    }
    
    private int piorEncaixe(int tamanho) {
        List<Lacuna> lacunas = encontrarLacunas();
        Lacuna piorLacuna = new Lacuna(-1, -1);

        for (Lacuna lacuna : lacunas) {
            if (lacuna.tamanho() >= tamanho && lacuna.tamanho() > piorLacuna.tamanho()) {
                piorLacuna = lacuna;
            }
        }
        return piorLacuna.inicio();
    }

    private int encaixeRapido(int tamanho) {
        return encontrarLacunas().stream()
                .filter(lacuna -> lacuna.tamanho() >= tamanho)
                .map(Lacuna::inicio)
                .findFirst()
                .orElse(-1);
    }
}