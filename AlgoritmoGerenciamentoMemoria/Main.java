// Principal.java
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        int tamanhoMemoria = 32;
        Map<String, Integer> processos = Map.of(
            "P1", 5, "P2", 8, "P3", 3, "P4", 7
        );
        GerenciadorMemoria gerenciador = new GerenciadorMemoria(tamanhoMemoria);
        System.out.println("--- Início da Simulação de Gerenciamento de Memória ---");
        System.out.printf("Tamanho da memória: %d blocos.%n", tamanhoMemoria);
        System.out.println("Estado inicial da memória:");
        gerenciador.exibirMapa();
        System.out.println("-".repeat(60));

        System.out.println("\n1. Alocando P1 (tam 5) com PRIMEIRO_ENCAIXE...");
        gerenciador.alocar("P1", processos.get("P1"), Algoritmo.PRIMEIRO_ENCAIXE);
        gerenciador.exibirMapa();

        System.out.println("\n2. Alocando P2 (tam 8) com MELHOR_ENCAIXE...");
        gerenciador.alocar("P2", processos.get("P2"), Algoritmo.MELHOR_ENCAIXE);
        gerenciador.exibirMapa();

        System.out.println("\n3. Alocando P3 (tam 3) com PRIMEIRO_ENCAIXE...");
        gerenciador.alocar("P3", processos.get("P3"), Algoritmo.PRIMEIRO_ENCAIXE);
        gerenciador.exibirMapa();

        System.out.println("\n4. Desalocando P1...");
        gerenciador.desalocar("P1");
        gerenciador.exibirMapa();

        System.out.println("\n5. Alocando P4 (tam 7) com PIOR_ENCAIXE...");
        gerenciador.alocar("P4", processos.get("P4"), Algoritmo.PIOR_ENCAIXE);
        gerenciador.exibirMapa();

        System.out.println("\n6. Desalocando P2...");
        gerenciador.desalocar("P2");
        gerenciador.exibirMapa();

        System.out.println("\n7. Alocando P1 (tam 5) novamente com PROXIMO_ENCAIXE...");
        gerenciador.alocar("P1", processos.get("P1"), Algoritmo.PROXIMO_ENCAIXE);
        gerenciador.exibirMapa();

        System.out.println("-".repeat(60));
        System.out.println("\n--- Estado Final da Memória ---");
        gerenciador.exibirMapa();
        gerenciador.calcularFragmentacao();
    }
}