package entity.dto;

//CLASSE HELPER DETTAGLI RICHIESTA ESPERTO
public class HardwareInfoDTO {
	
	private String cpu;
    private String ram;
    private String spazioArchiviazione;
    private String schedaVideo;
    private String tipoSistema;
    
    public HardwareInfoDTO() {}
    
    public HardwareInfoDTO(String cpu, String ram, String spazioArchiviazione) {
        this.cpu = cpu;
        this.ram = ram;
        this.spazioArchiviazione = spazioArchiviazione;
    }
    
    // Getters e Setters
    public String getCpu() { return cpu; }
    public void setCpu(String cpu) { this.cpu = cpu; }
    
    public String getRam() { return ram; }
    public void setRam(String ram) { this.ram = ram; }
    
    public String getSpazioArchiviazione() { return spazioArchiviazione; }
    public void setSpazioArchiviazione(String spazioArchiviazione) { this.spazioArchiviazione = spazioArchiviazione; }
    
    public String getSchedaVideo() { return schedaVideo; }
    public void setSchedaVideo(String schedaVideo) { this.schedaVideo = schedaVideo; }
    
    public String getTipoSistema() { return tipoSistema; }
    public void setTipoSistema(String tipoSistema) { this.tipoSistema = tipoSistema; }

}
