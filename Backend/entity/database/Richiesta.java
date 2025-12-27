package entity.database;
import enumerazioni.StatoRichiesta;
import enumerazioni.ModalitaSelezione;

public class Richiesta {
	
	private int idRichiesta;
    private int idUtente;
    private Integer idHardware; // Integer per permettere null
    private java.sql.Timestamp dataOrarioCreazione;
    private StatoRichiesta statoRichiesta;
    private ModalitaSelezione modalitaSelezione;
    private int maxEsperti;
    private java.sql.Timestamp scadenza;
    private String noteAggiuntive;


    public Richiesta(int idUtente, ModalitaSelezione modalitaSelezione) {
        this.idUtente = idUtente;
        this.modalitaSelezione = modalitaSelezione;
        this.statoRichiesta = StatoRichiesta.IN_ATTESA;
        this.maxEsperti = 3;
    }

    
    public int getIdRichiesta() { return idRichiesta; }
    public void setIdRichiesta(int idRichiesta) { this.idRichiesta = idRichiesta; }

    public int getIdUtente() { return idUtente; }
    public void setIdUtente(int idUtente) { this.idUtente = idUtente; }

    public Integer getIdHardware() { return idHardware; }
    public void setIdHardware(Integer idHardware) { this.idHardware = idHardware; }

    public java.sql.Timestamp getDataOrarioCreazione() { return dataOrarioCreazione; }
    public void setDataOrarioCreazione(java.sql.Timestamp dataOrarioCreazione) { this.dataOrarioCreazione = dataOrarioCreazione; }

    public StatoRichiesta getStatoRichiesta() { return statoRichiesta; }
    public void setStatoRichiesta(StatoRichiesta statoRichiesta) { this.statoRichiesta = statoRichiesta; }

    public ModalitaSelezione getModalitaSelezione() { return modalitaSelezione; }
    public void setModalitaSelezione(ModalitaSelezione modalitaSelezione) { this.modalitaSelezione = modalitaSelezione; }

    public int getMaxEsperti() { return maxEsperti; }
    public void setMaxEsperti(int maxEsperti) { this.maxEsperti = maxEsperti; }

    public java.sql.Timestamp getScadenza() { return scadenza; }
    public void setScadenza(java.sql.Timestamp scadenza) { this.scadenza = scadenza; }

    public String getNoteAggiuntive() { return noteAggiuntive; }
    public void setNoteAggiuntive(String noteAggiuntive) { this.noteAggiuntive = noteAggiuntive; }

}
