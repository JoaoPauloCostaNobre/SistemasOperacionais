// GerenciadorMemoria.java
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GerenciadorMemoria {

    private final int[] mapaDeBits;
    private final Map<String, InfoProcesso> processosAlocados;
    private int ultimaPosicao;
    private final int tamanhoTotal;

    public GerenciadorMemoria(int tamanhoTotal) {
        if (tamanhoTotal <= 0) {
            throw new IllegalArgumentException("O tamanho da memória deve ser positivo.");
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

    public boolean isAlocado(String pid) {
        return processosAlocados.containsKey(pid);
    }

    public boolean alocar(String pid, int tamanho, Algoritmo algoritmo) {
        if (tamanho <= 0) return false;
        
        int indiceInicial = switch (algoritmo) {
            case PRIMEIRO_ENCAIXE -> primeiroEncaixe(tamanho);
            case PROXIMO_ENCAIXE  -> proximoEncaixe(tamanho);
            case MELHOR_ENCAIXE   -> melhorEncaixe(tamanho);
            case PIOR_ENCAIXE     -> piorEncaixe(tamanho);
            case ENCAIXE_RAPIDO   -> encaixeRapido(tamanho);
        };

        if (indiceInicial != -1) {
            Arrays.fill(mapaDeBits, indiceInicial, indiceInicial + tamanho, 1);
            processosAlocados.put(pid, new InfoProcesso(indiceInicial, tamanho));
            ultimaPosicao = (indiceInicial + tamanho) % tamanhoTotal;
            System.out.printf("Processo %s (tam %d) ENTRANDO na memória no bloco %d.%n", pid, tamanho, indiceInicial);
            return true;
        } else {
            System.out.printf("Falha na alocação: Processo %s (tam %d) não encontrou espaço.%n", pid, tamanho);
            return false;
        }
    }

    public void desalocar(String pid) {
        if (!processosAlocados.containsKey(pid)) return;

        InfoProcesso info = processosAlocados.get(pid);
        Arrays.fill(mapaDeBits, info.inicio(), info.inicio() + info.tamanho(), 0);
        processosAlocados.remove(pid);
        System.out.printf("Processo %s SAINDO da memória (liberou %d blocos a partir de %d).%n", pid, info.tamanho(), info.inicio());
    }

    public void calcularFragmentacao() {
        List<Lacuna> lacunas = encontrarLacunas();
        int memoriaLivreTotal = 0;
        for (Lacuna l : lacunas) {
            memoriaLivreTotal += l.tamanho();
        }
        
        System.out.println("\n--- Estatísticas de Fragmentação Externa ---");
        if (memoriaLivreTotal == 0) {
            System.out.println("A memória está totalmente ocupada.");
            return;
        }
        int maiorLacuna = 0;
        for (Lacuna l : lacunas) {
            if (l.tamanho() > maiorLacuna) {
                maiorLacuna = l.tamanho();
            }
        }
        System.out.printf("Memória livre total: %d blocos%n", memoriaLivreTotal);
        System.out.printf("Número de lacunas (fragmentos): %d%n", lacunas.size());
        System.out.printf("Tamanho do maior fragmento livre: %d blocos%n", maiorLacuna);
    }
    
    private List<Lacuna> encontrarLacunas() {
        List<Lacuna> lacunas = new ArrayList<>();
        for (int i = 0; i < tamanhoTotal; ) {
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
        for (int i = 0; i < tamanho; i++) if (mapaDeBits[inicio + i] == 1) return false;
        return true;
    }



    private int primeiroEncaixe(int tamanho) {
        for (int i = 0; i <= tamanhoTotal - tamanho; i++) {
            if (isRegiaoLivre(i, tamanho)) {
                return i;
            }
        }
        return -1;
    }

    private int proximoEncaixe(int tamanho) {
        for (int i = ultimaPosicao; i <= tamanhoTotal - tamanho; i++) {
            if (isRegiaoLivre(i, tamanho)) {
                return i;
            }
        }
        for (int i = 0; i < ultimaPosicao && i <= tamanhoTotal - tamanho; i++) {
            if (isRegiaoLivre(i, tamanho)) {
                return i;
            }
        }
        return -1;
    }

    private int melhorEncaixe(int tamanho) {
        Lacuna melhorLacuna = null;
        for (Lacuna lacunaAtual : encontrarLacunas()) {
            if (lacunaAtual.tamanho() >= tamanho) {
                if (melhorLacuna == null || lacunaAtual.tamanho() < melhorLacuna.tamanho()) {
                    melhorLacuna = lacunaAtual;
                }
            }
        }
        return (melhorLacuna != null) ? melhorLacuna.inicio() : -1;
    }
    
    private int piorEncaixe(int tamanho) {
        Lacuna piorLacuna = null;
        for (Lacuna lacunaAtual : encontrarLacunas()) {
            if (lacunaAtual.tamanho() >= tamanho) {
                if (piorLacuna == null || lacunaAtual.tamanho() > piorLacuna.tamanho()) {
                    piorLacuna = lacunaAtual;
                }
            }
        }
        return (piorLacuna != null) ? piorLacuna.inicio() : -1;
    }

    private int encaixeRapido(int tamanho) {
        for (Lacuna lacunaAtual : encontrarLacunas()) {
            if (lacunaAtual.tamanho() >= tamanho) {
                return lacunaAtual.inicio(); 
            }
        }
        return -1; 
    }
}