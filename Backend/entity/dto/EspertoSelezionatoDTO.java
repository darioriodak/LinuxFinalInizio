package entity.dto;

public class EspertoSelezionatoDTO {
	
	private int id;
    private String nome; // Nome dell'utente associato all'esperto
    private String specializzazione;
    private int anniEsperienza;
    private double feedbackMedio;
    
    public EspertoSelezionatoDTO(int id, String nome) {
    	this.id = id;
        this.nome = nome;
    }
    
    public EspertoSelezionatoDTO(int id, String nome, String specializzazione, 
                               int anniEsperienza, double feedbackMedio) {
        this.id = id;
        this.nome = nome;
        this.specializzazione = specializzazione;
        this.anniEsperienza = anniEsperienza;
        this.feedbackMedio = feedbackMedio;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getSpecializzazione() { return specializzazione; }
    public void setSpecializzazione(String specializzazione) { this.specializzazione = specializzazione; }
    
    public int getAnniEsperienza() { return anniEsperienza; }
    public void setAnniEsperienza(int anniEsperienza) { this.anniEsperienza = anniEsperienza; }
    
    public double getFeedbackMedio() { return feedbackMedio; }
    public void setFeedbackMedio(double feedbackMedio) { this.feedbackMedio = feedbackMedio; }

}
