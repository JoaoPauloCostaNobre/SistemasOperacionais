// Processo.java
public class Processo {
    private int pid;                     // IDENTIFICADOR DE PROCESSO (PID)
    private int tempoTotalExecucao;      // Tempo total necessário para finalizar
    private int tempoProcessamento;      // (TP) Total de ciclos já executados
    private Estado estado;               // (EP) ESTADO DO PROCESSO
    private int nes;                     // (NES) NÚMERO DE VEZES QUE REALIZOU E/S
    private int nCpu;                    // (N_CPU) NÚMERO DE VEZES QUE USOU A CPU

    // Construtor
    public Processo(int pid, int tempoTotalExecucao) {
        this.pid = pid;
        this.tempoTotalExecucao = tempoTotalExecucao;
        this.tempoProcessamento = 0;
        this.estado = Estado.PRONTO; // Todos os processos começam como PRONTO
        this.nes = 0;
        this.nCpu = 0;
    }

    // Getters e Setters
    public int getPid() {
        return pid;
    }

    public int getTempoProcessamento() {
        return tempoProcessamento;
    }

    public int getContadorPrograma() {
        // (CP) Conforme Obs 2: CP = TP + 1
        return this.tempoProcessamento + 1;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public int getNes() {
        return nes;
    }

    public int getNCpu() {
        return nCpu;
    }
    
    public int getTempoTotalExecucao() {
        return tempoTotalExecucao;
    }

    // Métodos para simulação
    public void executarCiclo() {
        this.tempoProcessamento++;
    }

    public void incrementaVezesNaCpu() {
        this.nCpu++;
    }

    public void incrementaOperacoesES() {
        this.nes++;
    }

    public boolean isFinalizado() {
        return this.tempoProcessamento >= this.tempoTotalExecucao;
    }

    // Método para formatar a saída para o console
    @Override
    public String toString() {
        return String.format(
            "PID: %d | Estado: %s | TP: %d/%d | CP: %d | N_CPU: %d | NES: %d",
            this.pid,
            this.estado,
            this.tempoProcessamento,
            this.tempoTotalExecucao,
            this.getContadorPrograma(),
            this.nCpu,
            this.nes
        );
    }
    
    // Método para formatar a saída para o arquivo de texto
    public String paraFormatoArquivo() {
        return String.format("%d;%s;%d;%d;%d;%d;%d",
            this.pid,
            this.estado,
            this.tempoProcessamento,
            this.tempoTotalExecucao,
            this.getContadorPrograma(),
            this.nCpu,
            this.nes
        );
    }
}