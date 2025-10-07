// Principal.java
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Principal {

    private static final int TAMANHO_MEMORIA = 32; 
    private static final int NUMERO_DE_EXECUCOES = 30; 

    public static void main(String[] args) {
       
        Map<String, Integer> processosDisponiveis = Map.ofEntries(
            Map.entry("P1", 5), Map.entry("P2", 4),
            Map.entry("P3", 2), Map.entry("P4", 5),
            Map.entry("P5", 8), Map.entry("P6", 3),
            Map.entry("P7", 5), Map.entry("P8", 8),
            Map.entry("P9", 2), Map.entry("P10", 6)
        );

      
        for (Algoritmo alg : Algoritmo.values()) {
            executarSimulacao(alg, processosDisponiveis);
        }
    }

    public static void executarSimulacao(Algoritmo algoritmo, Map<String, Integer> processos) {
        System.out.printf("%n============================================================%n");
        System.out.printf("INICIANDO SIMULAÇÃO PARA: %s%n", algoritmo);
        System.out.printf("============================================================%n");

        GerenciadorMemoria gerenciador = new GerenciadorMemoria(TAMANHO_MEMORIA);
        Random sorteador = new Random();
        List<String> listaDePIDs = new ArrayList<>(processos.keySet());

        for (int i = 1; i <= NUMERO_DE_EXECUCOES; i++) {
            System.out.printf("--- Passo %d de %d ---%n", i, NUMERO_DE_EXECUCOES);

            String pidSorteado = listaDePIDs.get(sorteador.nextInt(listaDePIDs.size()));
            int tamanhoDoProcesso = processos.get(pidSorteado);

            if (gerenciador.isAlocado(pidSorteado)) {
              
                gerenciador.desalocar(pidSorteado);
            } else {
              
                gerenciador.alocar(pidSorteado, tamanhoDoProcesso, algoritmo);
            }

            gerenciador.exibirMapa();
        }

        System.out.printf("%n--- RESULTADO FINAL PARA: %s ---%n", algoritmo);
        gerenciador.exibirMapa();
        gerenciador.calcularFragmentacao(); // [cite: 139]
        System.out.printf("============================================================%n");
    }
}