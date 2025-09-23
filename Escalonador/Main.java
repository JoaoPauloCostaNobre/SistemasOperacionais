import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        int[] temposDeExecucao = {10000, 5000, 7000, 3000, 3000, 8000, 2000, 5000, 4000, 10000};
        List<Processo> listaDeProcessos = new ArrayList<>();
        for (int i = 0; i < temposDeExecucao.length; i++) {
            listaDeProcessos.add(new Processo(i, temposDeExecucao[i]));
        }
        Escalonador escalonador = new Escalonador(listaDeProcessos);
        escalonador.iniciarSimulacao();
    }
}