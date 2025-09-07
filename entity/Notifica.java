package entity;
import enumerazioni.StatoNotificaLettura;
import enumerazioni.TipoNotifica;
import enumerazioni.PrioritaNotifica;

public class Notifica {
	
	private int idNotifica;
    private int idEsperto;
    private int idRichiesta;
    private TipoNotifica tipoNotifica;
    private String titolo;
    private String messaggio;
    private java.sql.Timestamp dataCreazione;
    private java.sql.Timestamp dataLettura;
    private StatoNotificaLettura stato;
    private PrioritaNotifica priorita;

    public Notifica(int idEsperto, int idRichiesta, TipoNotifica tipoNotifica, String titolo) {
        this.idEsperto = idEsperto;
        this.idRichiesta = idRichiesta;
        this.tipoNotifica = tipoNotifica;
        this.titolo = titolo;
        this.stato = StatoNotificaLettura.NON_LETTA;
        this.priorita = PrioritaNotifica.NORMALE;
    }

    
    public int getIdNotifica() { return idNotifica; }
    public void setIdNotifica(int idNotifica) { this.idNotifica = idNotifica; }

    public int getIdEsperto() { return idEsperto; }
    public void setIdEsperto(int idEsperto) { this.idEsperto = idEsperto; }

    public int getIdRichiesta() { return idRichiesta; }
    public void setIdRichiesta(int idRichiesta) { this.idRichiesta = idRichiesta; }

    public TipoNotifica getTipoNotifica() { return tipoNotifica; }
    public void setTipoNotifica(TipoNotifica tipoNotifica) { this.tipoNotifica = tipoNotifica; }

    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }

    public String getMessaggio() { return messaggio; }
    public void setMessaggio(String messaggio) { this.messaggio = messaggio; }

    public java.sql.Timestamp getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(java.sql.Timestamp dataCreazione) { this.dataCreazione = dataCreazione; }

    public java.sql.Timestamp getDataLettura() { return dataLettura; }
    public void setDataLettura(java.sql.Timestamp dataLettura) { this.dataLettura = dataLettura; }

    public StatoNotificaLettura getStato() { return stato; }
    public void setStato(StatoNotificaLettura stato) { this.stato = stato; }

    public PrioritaNotifica getPriorita() { return priorita; }
    public void setPriorita(PrioritaNotifica priorita) { this.priorita = priorita; }

}
