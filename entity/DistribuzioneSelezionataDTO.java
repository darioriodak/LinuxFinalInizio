package entity;

public class DistribuzioneSelezionataDTO {
	
	private int id;
    private String nome;
    private String versione;
    private String ambienteDesktop;
    
    public DistribuzioneSelezionataDTO(int id, String nome) {
    	this.id = id;
        this.nome = nome;	
    }
    
    public DistribuzioneSelezionataDTO(int id, String nome, String versione, String ambienteDesktop) {
        this.id = id;
        this.nome = nome;
        this.versione = versione;
        this.ambienteDesktop = ambienteDesktop;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getVersione() { return versione; }
    public void setVersione(String versione) { this.versione = versione; }
    
    public String getAmbienteDesktop() { return ambienteDesktop; }
    public void setAmbienteDesktop(String ambienteDesktop) { this.ambienteDesktop = ambienteDesktop; }

}
