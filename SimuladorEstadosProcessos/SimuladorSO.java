import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimuladorSO {
    private static final int QUANTUM = 1000;
    private static final double CHANCE_IO = 0.01;
    private static final double CHANCE_DESBLOQUEIO = 0.30;
    private static final String NOME_ARQUIVO = "Tabela_de_Processos.txt";

    private List<Processo> processos;
    private Random random;

    public SimuladorSO() {
        this.processos = new ArrayList<>();
        this.random = new Random();
        limparArquivoHistorico();
        inicializarProcessos();
    }

    private void limparArquivoHistorico() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(NOME_ARQUIVO))) {
            writer.println("Histórico de Processos");
        } catch (IOException e) {
            System.err.println("Erro ao limpar o arquivo de histórico: " + e.getMessage());
        }
    }

    private void inicializarProcessos() {
        int[] tempos = {10000, 5000, 7000, 3000, 3000, 8000, 2000, 5000, 4000, 10000};
        for (int i = 0; i < 10; i++) {
            processos.add(new Processo(i, tempos[i]));
        }
        System.out.println(">>> Sistema Operacional iniciado com 10 processos na fila de PRONTO.");
        salvarTabelaDeProcessos();
    }

    public void simular() {
        while (!todosProcessosFinalizados()) {
            for (Processo p : processos) {
                if (p.isFinalizado()) {
                    continue;
                }
                if (p.getEstado() == Estado.BLOQUEADO) {
                    if (random.nextDouble() < CHANCE_DESBLOQUEIO) {
                        p.setEstado(Estado.PRONTO);
                        System.out.println("\n--- Evento: Processo desbloqueado ---");
                        System.out.printf("PID %d: BLOQUEADO -> PRONTO\n", p.getPid());
                        salvarTabelaDeProcessos();
                    }
                }
                if (p.getEstado() == Estado.PRONTO) {
                    executarProcesso(p);
                    if (todosProcessosFinalizados()) break;
                }
            }
        }
        System.out.println("\n>>> SIMULAÇÃO CONCLUÍDA: Todos os processos foram finalizados.");
    }

    private void executarProcesso(Processo p) {
        System.out.println("\n---------------------------------------------------------");
        System.out.printf(">>> Assumindo CPU: Processo com PID %d\n", p.getPid());

        p.setEstado(Estado.EXECUTANDO);
        p.incrementaVezesNaCpu();

        boolean sofreuIO = false;

        for (int ciclo = 0; ciclo < QUANTUM; ciclo++) {
            if (p.isFinalizado()) {
                break;
            }

            p.executarCiclo();

            if (random.nextDouble() < CHANCE_IO) {
                sofreuIO = true;
                p.incrementaOperacoesES();
                trocaDeContexto(p, Estado.BLOQUEADO);
                break;
            }
        }

        if (p.isFinalizado()) {
            p.setEstado(Estado.FINALIZADO);
            System.out.println("\n***** PROCESSO FINALIZADO *****");
            System.out.println(p.toString());
            System.out.println("*********************************");
            salvarTrocaDeContextoNoHistorico(p, "FINALIZADO");
        } else if (!sofreuIO) {
            trocaDeContexto(p, Estado.PRONTO);
        }

        salvarTabelaDeProcessos();
    }

    private void trocaDeContexto(Processo p, Estado novoEstado) {
        Estado estadoAnterior = p.getEstado();
        p.setEstado(novoEstado);

        System.out.println("\n--- Troca de Contexto ---");
        System.out.printf("PID %d: %s >>> %s\n", p.getPid(), estadoAnterior, novoEstado);
        System.out.println("Dados do processo salvos:");
        System.out.println(p.toString());
        System.out.println("-------------------------");

        salvarTrocaDeContextoNoHistorico(p, novoEstado.toString());
    }

    private void salvarTrocaDeContextoNoHistorico(Processo p, String novoEstado) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(NOME_ARQUIVO, true))) {
            writer.println("==== TROCA DE CONTEXTO ====");
            writer.printf("PID %d: %s >>> %s\n", p.getPid(), p.getEstado(), novoEstado);
            writer.println("PID;ESTADO;TP;TP_TOTAL;CP;N_CPU;NES");
            writer.println(p.paraFormatoArquivo());
            writer.println("===========================");
        } catch (IOException e) {
            System.err.println("Erro ao salvar o histórico de trocas de contexto: " + e.getMessage());
        }
    }

    private boolean todosProcessosFinalizados() {
        for (Processo p : processos) {
            if (p.getEstado() != Estado.FINALIZADO) {
                return false;
            }
        }
        return true;
    }

    private void salvarTabelaDeProcessos() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(NOME_ARQUIVO, true))) {
            writer.println("--------------------------------------------------");
            writer.println("PID;ESTADO;TP;TP_TOTAL;CP;N_CPU;NES");
            for (Processo p : processos) {
                writer.println(p.paraFormatoArquivo());
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar a tabela de processos: " + e.getMessage());
        }
    }
}