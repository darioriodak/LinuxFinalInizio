package entity;

import enumerazioni.TipoSistema;

public class ProfiloHardware {
	
	 private int idHardware;
	    private int idUtente;
	    private String cpu;
	    private String ram;
	    private String spazioArchiviazione;
	    private String schedaVideo;
	    private TipoSistema tipoSistema;
	    private java.sql.Timestamp dataCreazione;

	    // Costruttori
	    public ProfiloHardware() {}

	    public ProfiloHardware(int idUtente, String cpu, String ram, String spazioArchiviazione) {
	        this.idUtente = idUtente;
	        this.cpu = cpu;
	        this.ram = ram;
	        this.spazioArchiviazione = spazioArchiviazione;
	    }

	    // Getter e Setter
	    public int getIdHardware() { return idHardware; }
	    public void setIdHardware(int idHardware) { this.idHardware = idHardware; }

	    public int getIdUtente() { return idUtente; }
	    public void setIdUtente(int idUtente) { this.idUtente = idUtente; }

	    public String getCpu() { return cpu; }
	    public void setCpu(String cpu) { this.cpu = cpu; }

	    public String getRam() { return ram; }
	    public void setRam(String ram) { this.ram = ram; }

	    public String getSpazioArchiviazione() { return spazioArchiviazione; }
	    public void setSpazioArchiviazione(String spazioArchiviazione) { this.spazioArchiviazione = spazioArchiviazione; }

	    public String getSchedaVideo() { return schedaVideo; }
	    public void setSchedaVideo(String schedaVideo) { this.schedaVideo = schedaVideo; }

	    public TipoSistema getTipoSistema() { return tipoSistema; }
	    public void setTipoSistema(TipoSistema tipoSistema) { this.tipoSistema = tipoSistema; }

	    public java.sql.Timestamp getDataCreazione() { return dataCreazione; }
	    public void setDataCreazione(java.sql.Timestamp dataCreazione) { this.dataCreazione = dataCreazione; }

}
