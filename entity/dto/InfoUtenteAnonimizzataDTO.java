package entity.dto;

//CLASSE HELPER DETTAGLI RICHIESTA ESPERTO 

public class InfoUtenteAnonimizzataDTO {
	
	private String livelloEsperienza;
    private String scopoUso;
    private String etaApprossimativa; // es: "25-30 anni" invece di età precisa
    
    public InfoUtenteAnonimizzataDTO() {}
    
    public InfoUtenteAnonimizzataDTO(String livelloEsperienza, String scopoUso) {
        this.livelloEsperienza = livelloEsperienza;
        this.scopoUso = scopoUso;
    }
    
    // Getters e Setters
    public String getLivelloEsperienza() { return livelloEsperienza; }
    public void setLivelloEsperienza(String livelloEsperienza) { this.livelloEsperienza = livelloEsperienza; }
    
    public String getScopoUso() { return scopoUso; }
    public void setScopoUso(String scopoUso) { this.scopoUso = scopoUso; }
    
    public String getEtaApprossimativa() { return etaApprossimativa; }
    public void setEtaApprossimativa(String etaApprossimativa) { this.etaApprossimativa = etaApprossimativa; }

}
