package entity.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificheUtenteDTO {
	
	private List<NotificaSingolaUtenteDTO> notifiche;
    private int numeroNonLette;
    private int numeroTotali;
    
    public NotificheUtenteDTO() {
        this.notifiche = new ArrayList<>();
    }
    
    /**
     * Serializza le notifiche per invio al frontend utente
     */
    public String toJSONString() throws JSONException {
        JSONObject json = new JSONObject();
        
        json.put("numeroNonLette", numeroNonLette);
        json.put("numeroTotali", numeroTotali);
        
        JSONArray notificheArray = new JSONArray();
        for (NotificaSingolaUtenteDTO notifica : notifiche) {
            JSONObject notificaJson = new JSONObject();
            
            notificaJson.put("idNotifica", notifica.getIdNotifica());
            notificaJson.put("idRichiesta", notifica.getIdRichiesta());
            notificaJson.put("tipoNotifica", notifica.getTipoNotifica());
            notificaJson.put("titolo", notifica.getTitolo());
            notificaJson.put("messaggio", notifica.getMessaggio());
            notificaJson.put("dataCreazione", notifica.getDataCreazione().toString());
            notificaJson.put("stato", notifica.getStato());
            notificaJson.put("priorita", notifica.getPriorita());
            
            // Informazioni sul progresso della richiesta
            if (notifica.getProgressoValutazioni() != null) {
                JSONObject progressoJson = new JSONObject();
                progressoJson.put("valutazioniRicevute", notifica.getProgressoValutazioni().getValutazioniRicevute());
                progressoJson.put("totalEsperti", notifica.getProgressoValutazioni().getTotalEsperti());
                progressoJson.put("percentualeCompletamento", notifica.getProgressoValutazioni().getPercentualeCompletamento());
                notificaJson.put("progresso", progressoJson);
            }
            
            // Link azione (se applicabile)
            if (notifica.getLinkAzione() != null) {
                notificaJson.put("linkAzione", notifica.getLinkAzione());
                notificaJson.put("testoAzione", notifica.getTestoAzione());
            }
            
            notificheArray.put(notificaJson);
        }
        
        json.put("notifiche", notificheArray);
        return json.toString();
    }
    
    public void aggiungiNotifica(NotificaSingolaUtenteDTO notifica) {
        if (this.notifiche == null) {
            this.notifiche = new ArrayList<>();
        }
        this.notifiche.add(notifica);
    }
    
    // Getters e Setters
    public List<NotificaSingolaUtenteDTO> getNotifiche() { return notifiche; }
    public void setNotifiche(List<NotificaSingolaUtenteDTO> notifiche) { this.notifiche = notifiche; }
    
    public int getNumeroNonLette() { return numeroNonLette; }
    public void setNumeroNonLette(int numeroNonLette) { this.numeroNonLette = numeroNonLette; }
    
    public int getNumeroTotali() { return numeroTotali; }
    public void setNumeroTotali(int numeroTotali) { this.numeroTotali = numeroTotali; }
    
    // ===== CLASSE INTERNA: Singola Notifica Utente =====
    
    public static class NotificaSingolaUtenteDTO {
        
        private int idNotifica;
        private int idRichiesta;
        private String tipoNotifica;
        private String titolo;
        private String messaggio;
        private Timestamp dataCreazione;
        private String stato;
        private String priorita;
        
        // Dati specifici per notifiche di progresso
        private ProgressoValutazioniDTO progressoValutazioni;
        
        // Link azione (es: "Visualizza risultati")
        private String linkAzione;
        private String testoAzione;
        
        public NotificaSingolaUtenteDTO() {}
        
        public NotificaSingolaUtenteDTO(int idNotifica, int idRichiesta, String tipoNotifica, String titolo) {
            this.idNotifica = idNotifica;
            this.idRichiesta = idRichiesta;
            this.tipoNotifica = tipoNotifica;
            this.titolo = titolo;
        }
        
        // Getters e Setters
        public int getIdNotifica() { return idNotifica; }
        public void setIdNotifica(int idNotifica) { this.idNotifica = idNotifica; }
        
        public int getIdRichiesta() { return idRichiesta; }
        public void setIdRichiesta(int idRichiesta) { this.idRichiesta = idRichiesta; }
        
        public String getTipoNotifica() { return tipoNotifica; }
        public void setTipoNotifica(String tipoNotifica) { this.tipoNotifica = tipoNotifica; }
        
        public String getTitolo() { return titolo; }
        public void setTitolo(String titolo) { this.titolo = titolo; }
        
        public String getMessaggio() { return messaggio; }
        public void setMessaggio(String messaggio) { this.messaggio = messaggio; }
        
        public Timestamp getDataCreazione() { return dataCreazione; }
        public void setDataCreazione(Timestamp dataCreazione) { this.dataCreazione = dataCreazione; }
        
        public String getStato() { return stato; }
        public void setStato(String stato) { this.stato = stato; }
        
        public String getPriorita() { return priorita; }
        public void setPriorita(String priorita) { this.priorita = priorita; }
        
        public ProgressoValutazioniDTO getProgressoValutazioni() { return progressoValutazioni; }
        public void setProgressoValutazioni(ProgressoValutazioniDTO progressoValutazioni) { this.progressoValutazioni = progressoValutazioni; }
        
        public String getLinkAzione() { return linkAzione; }
        public void setLinkAzione(String linkAzione) { this.linkAzione = linkAzione; }
        
        public String getTestoAzione() { return testoAzione; }
        public void setTestoAzione(String testoAzione) { this.testoAzione = testoAzione; }
    }
    
    // ===== CLASSE INTERNA: Progresso Valutazioni =====
    
    public static class ProgressoValutazioniDTO {
        
        private int valutazioniRicevute;
        private int totalEsperti;
        private double percentualeCompletamento;
        
        public ProgressoValutazioniDTO() {}
        
        public ProgressoValutazioniDTO(int valutazioniRicevute, int totalEsperti) {
            this.valutazioniRicevute = valutazioniRicevute;
            this.totalEsperti = totalEsperti;
            this.percentualeCompletamento = (double) valutazioniRicevute / totalEsperti * 100;
        }
        
        // Getters e Setters
        public int getValutazioniRicevute() { return valutazioniRicevute; }
        public void setValutazioniRicevute(int valutazioniRicevute) { this.valutazioniRicevute = valutazioniRicevute; }
        
        public int getTotalEsperti() { return totalEsperti; }
        public void setTotalEsperti(int totalEsperti) { this.totalEsperti = totalEsperti; }
        
        public double getPercentualeCompletamento() { return percentualeCompletamento; }
        public void setPercentualeCompletamento(double percentualeCompletamento) { this.percentualeCompletamento = percentualeCompletamento; }
    }

}
