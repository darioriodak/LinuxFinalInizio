package entity.database;

import java.sql.Timestamp;
import enumerazioni.TipoNotificaUtente;
import enumerazioni.StatoNotificaLettura;
import enumerazioni.PrioritaNotifica;

public class NotificaUtente {
	
	private int idNotifica;
    private int idUtente;
    private int idRichiesta;
    private TipoNotificaUtente tipoNotifica;
    private String titolo;
    private String messaggio;
    private Timestamp dataCreazione;
    private Timestamp dataLettura;
    private StatoNotificaLettura stato;
    private PrioritaNotifica priorita;
    
    // Campi specifici per notifiche utente (extra rispetto alle notifiche esperti)
    private int valutazioniRicevute;    // Quante valutazioni sono arrivate
    private int totalEsperti;           // Totale esperti assegnati
    private String linkAzione;          // URL per azione (es: "/richieste/123/risultati")
    private String testoAzione;         // Testo del bottone (es: "Visualizza risultati")
    
    // ===== COSTRUTTORI =====
    
    /**
     * Costruttore vuoto per ORM/DAO
     */
    public NotificaUtente() {}
    
    /**
     * Costruttore con parametri essenziali per creare nuova notifica
     */
    public NotificaUtente(int idUtente, int idRichiesta, TipoNotificaUtente tipoNotifica, String titolo) {
        this.idUtente = idUtente;
        this.idRichiesta = idRichiesta;
        this.tipoNotifica = tipoNotifica;
        this.titolo = titolo;
        
        // Valori di default
        this.stato = StatoNotificaLettura.NON_LETTA;
        this.priorita = PrioritaNotifica.NORMALE;
        this.valutazioniRicevute = 0;
        this.totalEsperti = 0;
    }
    
    /**
     * Costruttore completo per ricostruire dal database
     */
    public NotificaUtente(int idNotifica, int idUtente, int idRichiesta, TipoNotificaUtente tipoNotifica,
                         String titolo, String messaggio, Timestamp dataCreazione, Timestamp dataLettura,
                         StatoNotificaLettura stato, PrioritaNotifica priorita, int valutazioniRicevute,
                         int totalEsperti, String linkAzione, String testoAzione) {
        this.idNotifica = idNotifica;
        this.idUtente = idUtente;
        this.idRichiesta = idRichiesta;
        this.tipoNotifica = tipoNotifica;
        this.titolo = titolo;
        this.messaggio = messaggio;
        this.dataCreazione = dataCreazione;
        this.dataLettura = dataLettura;
        this.stato = stato;
        this.priorita = priorita;
        this.valutazioniRicevute = valutazioniRicevute;
        this.totalEsperti = totalEsperti;
        this.linkAzione = linkAzione;
        this.testoAzione = testoAzione;
    }
    
    // ===== METODI DI UTILITÀ =====
    
    /**
     * Calcola la percentuale di completamento delle valutazioni
     */
    public double getPercentualeCompletamento() {
        if (totalEsperti == 0) {
            return 0.0;
        }
        return ((double) valutazioniRicevute / totalEsperti) * 100.0;
    }
    
    /**
     * Verifica se la notifica è stata letta
     */
    public boolean isLetta() {
        return stato == StatoNotificaLettura.LETTA || stato == StatoNotificaLettura.ARCHIVIATA;
    }
    
    /**
     * Verifica se la notifica ha un'azione associata
     */
    public boolean hasAzione() {
        return linkAzione != null && !linkAzione.trim().isEmpty();
    }
    
    /**
     * Marca la notifica come letta
     */
    public void marcaComeLetta() {
        this.stato = StatoNotificaLettura.LETTA;
        this.dataLettura = new Timestamp(System.currentTimeMillis());
    }
    
    // ===== GETTERS E SETTERS =====
    
    public int getIdNotifica() {
        return idNotifica;
    }
    
    public void setIdNotifica(int idNotifica) {
        this.idNotifica = idNotifica;
    }
    
    public int getIdUtente() {
        return idUtente;
    }
    
    public void setIdUtente(int idUtente) {
        this.idUtente = idUtente;
    }
    
    public int getIdRichiesta() {
        return idRichiesta;
    }
    
    public void setIdRichiesta(int idRichiesta) {
        this.idRichiesta = idRichiesta;
    }
    
    public TipoNotificaUtente getTipoNotifica() {
        return tipoNotifica;
    }
    
    public void setTipoNotifica(TipoNotificaUtente tipoNotifica) {
        this.tipoNotifica = tipoNotifica;
    }
    
    public String getTitolo() {
        return titolo;
    }
    
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }
    
    public String getMessaggio() {
        return messaggio;
    }
    
    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }
    
    public Timestamp getDataCreazione() {
        return dataCreazione;
    }
    
    public void setDataCreazione(Timestamp dataCreazione) {
        this.dataCreazione = dataCreazione;
    }
    
    public Timestamp getDataLettura() {
        return dataLettura;
    }
    
    public void setDataLettura(Timestamp dataLettura) {
        this.dataLettura = dataLettura;
    }
    
    public StatoNotificaLettura getStato() {
        return stato;
    }
    
    public void setStato(StatoNotificaLettura stato) {
        this.stato = stato;
    }
    
    public PrioritaNotifica getPriorita() {
        return priorita;
    }
    
    public void setPriorita(PrioritaNotifica priorita) {
        this.priorita = priorita;
    }
    
    public int getValutazioniRicevute() {
        return valutazioniRicevute;
    }
    
    public void setValutazioniRicevute(int valutazioniRicevute) {
        this.valutazioniRicevute = valutazioniRicevute;
    }
    
    public int getTotalEsperti() {
        return totalEsperti;
    }
    
    public void setTotalEsperti(int totalEsperti) {
        this.totalEsperti = totalEsperti;
    }
    
    public String getLinkAzione() {
        return linkAzione;
    }
    
    public void setLinkAzione(String linkAzione) {
        this.linkAzione = linkAzione;
    }
    
    public String getTestoAzione() {
        return testoAzione;
    }
    
    public void setTestoAzione(String testoAzione) {
        this.testoAzione = testoAzione;
    }
    
    // ===== METODI STANDARD =====
    
    @Override
    public String toString() {
        return "NotificaUtente{" +
                "idNotifica=" + idNotifica +
                ", idUtente=" + idUtente +
                ", idRichiesta=" + idRichiesta +
                ", tipoNotifica=" + tipoNotifica +
                ", titolo='" + titolo + '\'' +
                ", stato=" + stato +
                ", priorita=" + priorita +
                ", valutazioniRicevute=" + valutazioniRicevute +
                ", totalEsperti=" + totalEsperti +
                ", dataCreazione=" + dataCreazione +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        NotificaUtente that = (NotificaUtente) o;
        return idNotifica == that.idNotifica;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(idNotifica);
    }

}
