package entity.database;

public class ValutazioneEsperto {
	
	private int idValutazione;
    private int idRichiesta;
    private int idDistribuzione;
    private int idEsperto;
    private double punteggio;
    private String suggerimento;
    private String motivazione;
    private java.sql.Timestamp dataValutazione;

    public ValutazioneEsperto(int idRichiesta, int idDistribuzione, int idEsperto, double punteggio) {
        this.idRichiesta = idRichiesta;
        this.idDistribuzione = idDistribuzione;
        this.idEsperto = idEsperto;
        this.punteggio = punteggio;
    }

 
    public int getIdValutazione() { return idValutazione; }
    public void setIdValutazione(int idValutazione) { this.idValutazione = idValutazione; }

    public int getIdRichiesta() { return idRichiesta; }
    public void setIdRichiesta(int idRichiesta) { this.idRichiesta = idRichiesta; }

    public int getIdDistribuzione() { return idDistribuzione; }
    public void setIdDistribuzione(int idDistribuzione) { this.idDistribuzione = idDistribuzione; }

    public int getIdEsperto() { return idEsperto; }
    public void setIdEsperto(int idEsperto) { this.idEsperto = idEsperto; }

    public double getPunteggio() { return punteggio; }
    public void setPunteggio(double punteggio) { this.punteggio = punteggio; }

    public String getSuggerimento() { return suggerimento; }
    public void setSuggerimento(String suggerimento) { this.suggerimento = suggerimento; }

    public String getMotivazione() { return motivazione; }
    public void setMotivazione(String motivazione) { this.motivazione = motivazione; }

    public java.sql.Timestamp getDataValutazione() { return dataValutazione; }
    public void setDataValutazione(java.sql.Timestamp dataValutazione) { this.dataValutazione = dataValutazione; }

}
