import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Random;

public class Escalonador {
    private final List<Processo> processos;
    private final int quantum = 1000;
    private final Random random = new Random();
    private final String NOME_ARQUIVO_SAIDA = "src/Tabela_de_Processos.txt";

    public Escalonador(List<Processo> processos) {
        this.processos = processos;
    }

    public void iniciarSimulacao() {
        System.out.println(">>> INICIANDO SIMULAÇÃO DO ESCALONADOR DE PROCESSOS <<<\n");
        limparArquivoDeLog();
        int processosFinalizados = 0;
        while (processosFinalizados < processos.size()) {
            for (Processo processoAtual : processos) {
                if (processoAtual.getEstado() == Estado.FINALIZADO) {
                    continue;
                }
                if (processoAtual.getEstado() == Estado.BLOQUEADO) {
                    if (random.nextDouble() <= 0.30) {
                        processoAtual.setEstado(Estado.PRONTO);
                        System.out.println(String.format("PROCESSO %d: BLOQUEADO -> PRONTO", processoAtual.getPid()));
                    }
                    continue; 
                }
                if (processoAtual.getEstado() == Estado.PRONTO) {
                    processoAtual.setEstado(Estado.EXECUTANDO);
                    processoAtual.incrementarNumVezesCPU();
                    boolean sofreuES = false;
                    for (int ciclo = 0; ciclo < quantum; ciclo++) {
                        if (processoAtual.isFinalizado()) {
                            break;
                        }
                        processoAtual.executarCiclo();
                        if (random.nextDouble() <= 0.01) {
                            processoAtual.incrementarNumOperacoesES();
                            processoAtual.setEstado(Estado.BLOQUEADO);
                            sofreuES = true;
                            System.out.println(String.format("PROCESSO %d: EXECUTANDO -> BLOQUEADO (E/S)", processoAtual.getPid()));
                            imprimirDadosTrocaContexto(processoAtual);
                            break;
                        }
                    }
                    if (processoAtual.isFinalizado()) {
                        processoAtual.setEstado(Estado.FINALIZADO);
                        processosFinalizados++;
                        System.out.println(processoAtual.toFinalString());
                    } else if (!sofreuES) { 
                        processoAtual.setEstado(Estado.PRONTO);
                        System.out.println(String.format("PROCESSO %d: EXECUTANDO -> PRONTO (Quantum Expirado)", processoAtual.getPid()));
                        imprimirDadosTrocaContexto(processoAtual);
                    }
                }
            }
        }
        System.out.println("\n>>> SIMULAÇÃO FINALIZADA: Todos os processos foram concluídos. <<<");
    }
    
    private void imprimirDadosTrocaContexto(Processo processo) {
        System.out.println("  " + processo.toString());
        System.out.println("--------------------------------------------------------------------------------------");
        atualizarTabelaDeProcessos();
    }
    
    private void atualizarTabelaDeProcessos() {
        try (FileWriter fw = new FileWriter(NOME_ARQUIVO_SAIDA, false);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println("==================== TABELA DE PROCESSOS ATUALIZADA ====================");
            for (Processo p : processos) {
                pw.println(p.toString());
            }
            pw.println("========================================================================");
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo " + NOME_ARQUIVO_SAIDA + ": " + e.getMessage());
        }
    }
    
    private void limparArquivoDeLog() {
         try (FileWriter fw = new FileWriter(NOME_ARQUIVO_SAIDA, false)) {
             fw.write(""); 
         } catch(IOException e) {
             System.err.println("Erro ao limpar o arquivo de log: " + e.getMessage());
         }
    }
}