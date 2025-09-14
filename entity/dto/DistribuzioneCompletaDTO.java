package entity.dto;

//CLASSE HELPER DETTAGLI RICHIESTA ESPERTO

public class DistribuzioneCompletaDTO {
	
	private int id;
    private String nome;
    private String versione;
    private String ambienteDesktop;
    private String categoria;
    private String descrizione;
    private String requisitiHardware;
    
    public DistribuzioneCompletaDTO() {}
    
    public DistribuzioneCompletaDTO(int id, String nome, String versione) {
        this.id = id;
        this.nome = nome;
        this.versione = versione;
    }
    
    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getVersione() { return versione; }
    public void setVersione(String versione) { this.versione = versione; }
    
    public String getAmbienteDesktop() { return ambienteDesktop; }
    public void setAmbienteDesktop(String ambienteDesktop) { this.ambienteDesktop = ambienteDesktop; }
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
    
    public String getRequisitiHardware() { return requisitiHardware; }
    public void setRequisitiHardware(String requisitiHardware) { this.requisitiHardware = requisitiHardware; }
    


}
