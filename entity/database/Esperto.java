package entity.database;

public class Esperto {
	
	private int idEsperto;
    private int idUtente;
    private String specializzazione;
    private int anniEsperienza;
    private double feedbackMedio;
    private int numeroValutazioni;
    private boolean attivo;
    private java.sql.Timestamp dataRegistrazione;


    public Esperto(int idUtente, String specializzazione, int anniEsperienza) {
        this.idUtente = idUtente;
        this.specializzazione = specializzazione;
        this.anniEsperienza = anniEsperienza;
        this.attivo = true;
    }

    
    public int getIdEsperto() { return idEsperto; }
    public void setIdEsperto(int idEsperto) { this.idEsperto = idEsperto; }

    public int getIdUtente() { return idUtente; }
    public void setIdUtente(int idUtente) { this.idUtente = idUtente; }

    public String getSpecializzazione() { return specializzazione; }
    public void setSpecializzazione(String specializzazione) { this.specializzazione = specializzazione; }

    public int getAnniEsperienza() { return anniEsperienza; }
    public void setAnniEsperienza(int anniEsperienza) { this.anniEsperienza = anniEsperienza; }

    public double getFeedbackMedio() { return feedbackMedio; }
    public void setFeedbackMedio(double feedbackMedio) { this.feedbackMedio = feedbackMedio; }

    public int getNumeroValutazioni() { return numeroValutazioni; }
    public void setNumeroValutazioni(int numeroValutazioni) { this.numeroValutazioni = numeroValutazioni; }

    public boolean isAttivo() { return attivo; }
    public void setAttivo(boolean attivo) { this.attivo = attivo; }

    public java.sql.Timestamp getDataRegistrazione() { return dataRegistrazione; }
    public void setDataRegistrazione(java.sql.Timestamp dataRegistrazione) { this.dataRegistrazione = dataRegistrazione; }

}
