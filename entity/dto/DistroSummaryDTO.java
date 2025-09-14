package entity.dto;

/**
 * DTO per informazioni semplificate delle distribuzioni (interfaccia utente)
 */
public class DistroSummaryDTO {
    
    private int id;
    private int idDistribuzione;
    private String nomeDisplay;
    private String descrizioneBreve;
    private String descrizioneDettaglio;
    private String icona;
    private String coloreHex;
    private String livelloDifficolta;
    private int punteggioPopolarita;
    
    // Costruttori
    public DistroSummaryDTO() {}
    
    public DistroSummaryDTO(int id, int idDistribuzione, String nomeDisplay, 
                           String descrizioneBreve, String descrizioneDettaglio,
                           String icona, String coloreHex, String livelloDifficolta, 
                           int punteggioPopolarita) {
        this.id = id;
        this.idDistribuzione = idDistribuzione;
        this.nomeDisplay = nomeDisplay;
        this.descrizioneBreve = descrizioneBreve;
        this.descrizioneDettaglio = descrizioneDettaglio;
        this.icona = icona;
        this.coloreHex = coloreHex;
        this.livelloDifficolta = livelloDifficolta;
        this.punteggioPopolarita = punteggioPopolarita;
    }
    
    // Metodo utility per stelle
    public String getStellePopolrita() {
        StringBuilder stelle = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            stelle.append(i <= punteggioPopolarita ? "★" : "☆");
        }
        return stelle.toString();
    }
    
    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getIdDistribuzione() { return idDistribuzione; }
    public void setIdDistribuzione(int idDistribuzione) { this.idDistribuzione = idDistribuzione; }
    
    public String getNomeDisplay() { return nomeDisplay; }
    public void setNomeDisplay(String nomeDisplay) { this.nomeDisplay = nomeDisplay; }
    
    public String getDescrizioneBreve() { return descrizioneBreve; }
    public void setDescrizioneBreve(String descrizioneBreve) { this.descrizioneBreve = descrizioneBreve; }
    
    public String getDescrizioneDettaglio() { return descrizioneDettaglio; }
    public void setDescrizioneDettaglio(String descrizioneDettaglio) { this.descrizioneDettaglio = descrizioneDettaglio; }
    
    public String getIcona() { return icona; }
    public void setIcona(String icona) { this.icona = icona; }
    
    public String getColoreHex() { return coloreHex; }
    public void setColoreHex(String coloreHex) { this.coloreHex = coloreHex; }
    
    public String getLivelloDifficolta() { return livelloDifficolta; }
    public void setLivelloDifficolta(String livelloDifficolta) { this.livelloDifficolta = livelloDifficolta; }
    
    public int getPunteggioPopolarita() { return punteggioPopolarita; }
    public void setPunteggioPopolarita(int punteggioPopolarita) { this.punteggioPopolarita = punteggioPopolarita; }
}