// Arquivo: Processo.java

enum Estado {
    PRONTO,
    EXECUTANDO,
    BLOQUEADO,
    FINALIZADO
}

public class Processo {
    private final int pid;
    private int tempoProcessamentoTotal;
    private int contadorPrograma;
    private Estado estado;
    private int numOperacoesES;
    private int numVezesCPU;
    private final int tempoExecucaoNecessario;

    public Processo(int pid, int tempoExecucaoNecessario) {
        this.pid = pid;
        this.tempoExecucaoNecessario = tempoExecucaoNecessario;
        this.tempoProcessamentoTotal = 0;
        this.contadorPrograma = 0;
        this.estado = Estado.PRONTO;
        this.numOperacoesES = 0;
        this.numVezesCPU = 0;
    }

    public int getPid() {
        return pid;
    }

    public int getTempoProcessamentoTotal() {
        return tempoProcessamentoTotal;
    }

    public int getContadorPrograma() {
        return contadorPrograma;
    }

    public Estado getEstado() {
        return estado;
    }

    public int getNumOperacoesES() {
        return numOperacoesES;
    }

    public int getNumVezesCPU() {
        return numVezesCPU;
    }

    public int getTempoExecucaoNecessario() {
        return tempoExecucaoNecessario;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public void executarCiclo() {
        this.tempoProcessamentoTotal++;
        this.contadorPrograma = this.tempoProcessamentoTotal + 1;
    }

    public void incrementarNumOperacoesES() {
        this.numOperacoesES++;
    }

    public void incrementarNumVezesCPU() {
        this.numVezesCPU++;
    }

    public boolean isFinalizado() {
        return this.tempoProcessamentoTotal >= this.tempoExecucaoNecessario;
    }

    @Override
    public String toString() {
        return String.format(
            "PID: %d | Estado: %-10s | TP: %-5d | CP: %-5d | NES: %-3d | N_CPU: %-3d",
            pid, estado, tempoProcessamentoTotal, contadorPrograma, numOperacoesES, numVezesCPU
        );
    }

    public String toFinalString() {
         return String.format(
            "--------------------------------------------------\n" +
            "PROCESSO %d FINALIZADO\n" +
            "  - Tempo Total de Processamento (TP): %d ciclos\n" +
            "  - Total de Operações de E/S (NES):   %d\n" +
            "  - Total de Vezes na CPU (N_CPU):     %d\n" +
            "--------------------------------------------------",
            pid, tempoProcessamentoTotal, numOperacoesES, numVezesCPU
        );
    }
}