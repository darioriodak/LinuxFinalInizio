package entity.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificheEspertoDTO {
	
	private List<NotificaSingolaDTO> notifiche;
    private int numeroNonLette;
    
    public NotificheEspertoDTO() {
        this.notifiche = new ArrayList<>();
    }
    
    public String toJSONString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("numeroNonLette", numeroNonLette);
        
        JSONArray notificheArray = new JSONArray();
        for (NotificaSingolaDTO notifica : notifiche) {
            JSONObject notificaJson = new JSONObject();
            notificaJson.put("idNotifica", notifica.getIdNotifica());
            notificaJson.put("idRichiesta", notifica.getIdRichiesta());
            notificaJson.put("titolo", notifica.getTitolo());
            notificaJson.put("messaggio", notifica.getMessaggio());
            notificaJson.put("dataCreazione", notifica.getDataCreazione().toString());
            notificaJson.put("stato", notifica.getStato());
            notificaJson.put("priorita", notifica.getPriorita());
            notificaJson.put("tipoNotifica", notifica.getTipoNotifica());
            notificheArray.put(notificaJson);
        }
        json.put("notifiche", notificheArray);
        
        return json.toString();
    }
    
    // Getters e setters...
    public List<NotificaSingolaDTO> getNotifiche() { return notifiche; }
    public void setNotifiche(List<NotificaSingolaDTO> notifiche) { this.notifiche = notifiche; }
    
    public int getNumeroNonLette() { return numeroNonLette; }
    public void setNumeroNonLette(int numeroNonLette) { this.numeroNonLette = numeroNonLette; }
    
    public void aggiungiNotifica(NotificaSingolaDTO notifica) {
        if (this.notifiche == null) {
            this.notifiche = new ArrayList<>();
        }
        this.notifiche.add(notifica);
    }
    
    // DTO interno per singola notifica
    public static class NotificaSingolaDTO {
        private int idNotifica;
        private int idRichiesta;
        private String titolo;
        private String messaggio;
        private Timestamp dataCreazione;
        private String stato;
        private String priorita;
        private String tipoNotifica;
        
        // Costruttori, getters, setters...
        public NotificaSingolaDTO() {}
        
        public NotificaSingolaDTO(int idNotifica, int idRichiesta, String titolo) {
            this.idNotifica = idNotifica;
            this.idRichiesta = idRichiesta;
            this.titolo = titolo;
        }
        
        // Tutti i getters/setters...
        public int getIdNotifica() { return idNotifica; }
        public void setIdNotifica(int idNotifica) { this.idNotifica = idNotifica; }
        
        public int getIdRichiesta() { return idRichiesta; }
        public void setIdRichiesta(int idRichiesta) { this.idRichiesta = idRichiesta; }
        
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
        
        public String getTipoNotifica() { return tipoNotifica; }
        public void setTipoNotifica(String tipoNotifica) { this.tipoNotifica = tipoNotifica; }
    }

}
