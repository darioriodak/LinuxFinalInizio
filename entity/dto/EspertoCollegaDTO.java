package entity.dto;

public class EspertoCollegaDTO {
	
	private String nomeAnonimizzato; // es: "Marco R.", "Esperto_042"
    private String specializzazione;
    private boolean haValutato;
    
    public EspertoCollegaDTO() {}
    
    public EspertoCollegaDTO(String nomeAnonimizzato, String specializzazione) {
        this.nomeAnonimizzato = nomeAnonimizzato;
        this.specializzazione = specializzazione;
        this.haValutato = false;
    }
    
    // Getters e Setters
    public String getNomeAnonimizzato() { return nomeAnonimizzato; }
    public void setNomeAnonimizzato(String nomeAnonimizzato) { this.nomeAnonimizzato = nomeAnonimizzato; }
    
    public String getSpecializzazione() { return specializzazione; }
    public void setSpecializzazione(String specializzazione) { this.specializzazione = specializzazione; }
    
    public boolean isHaValutato() { return haValutato; }
    public void setHaValutato(boolean haValutato) { this.haValutato = haValutato; }
	

}
