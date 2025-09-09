package entity.database;
import enumerazioni.StatoNotifica;

public class RichiestaEsperto {
	
	private int idRichiesta;
    private int idEsperto;
    private java.sql.Timestamp dataAssegnazione;
    private StatoNotifica statoNotifica;
    private java.sql.Timestamp dataLettura;
    private java.sql.Timestamp dataScadenza;

    

    public RichiestaEsperto(int idRichiesta, int idEsperto) {
        this.idRichiesta = idRichiesta;
        this.idEsperto = idEsperto;
        this.statoNotifica = StatoNotifica.INVIATA;
    }

    // Getter e Setter
    public int getIdRichiesta() { return idRichiesta; }
    public void setIdRichiesta(int idRichiesta) { this.idRichiesta = idRichiesta; }

    public int getIdEsperto() { return idEsperto; }
    public void setIdEsperto(int idEsperto) { this.idEsperto = idEsperto; }

    public java.sql.Timestamp getDataAssegnazione() { return dataAssegnazione; }
    public void setDataAssegnazione(java.sql.Timestamp dataAssegnazione) { this.dataAssegnazione = dataAssegnazione; }

    public StatoNotifica getStatoNotifica() { return statoNotifica; }
    public void setStatoNotifica(StatoNotifica statoNotifica) { this.statoNotifica = statoNotifica; }

    public java.sql.Timestamp getDataLettura() { return dataLettura; }
    public void setDataLettura(java.sql.Timestamp dataLettura) { this.dataLettura = dataLettura; }

    public java.sql.Timestamp getDataScadenza() { return dataScadenza; }
    public void setDataScadenza(java.sql.Timestamp dataScadenza) { this.dataScadenza = dataScadenza; }

}
