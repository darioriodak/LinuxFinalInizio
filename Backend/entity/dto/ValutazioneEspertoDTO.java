package entity.dto;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ValutazioneEspertoDTO {
	
	private int idRichiesta;
    private int idEsperto;
    private List<ValutazioneSingolaDistro> valutazioni;
    private String noteGenerali;
    
    public ValutazioneEspertoDTO() {
        this.valutazioni = new ArrayList<>();
    }
    
    public ValutazioneEspertoDTO(int idRichiesta, int idEsperto) {
        this.idRichiesta = idRichiesta;
        this.idEsperto = idEsperto;
        this.valutazioni = new ArrayList<>();
    }
    
    /**
     * Deserializza JSON inviato dall'esperto
     * Formato atteso:
     * {
     *   "idRichiesta": 123,
     *   "valutazioni": [
     *     {
     *       "idDistribuzione": 1,
     *       "punteggio": 8.5,
     *       "suggerimento": "Ottima per principianti",
     *       "motivazione": "Interfaccia user-friendly e buon supporto community"
     *     }
     *   ],
     *   "noteGenerali": "Considerare anche l'hardware specifico..."
     * }
     */
    public static ValutazioneEspertoDTO fromJSON(String jsonString, int idEsperto) throws JSONException {
        JSONObject json = new JSONObject(jsonString);
        
        if (!json.has("idRichiesta") || !json.has("valutazioni")) {
            throw new JSONException("Parametri obbligatori mancanti: idRichiesta, valutazioni");
        }
        
        ValutazioneEspertoDTO dto = new ValutazioneEspertoDTO(
            json.getInt("idRichiesta"),
            idEsperto
        );
        
        JSONArray valutazioniArray = json.getJSONArray("valutazioni");
        if (valutazioniArray.length() == 0) {
            throw new JSONException("Almeno una valutazione è obbligatoria");
        }
        
        for (int i = 0; i < valutazioniArray.length(); i++) {
            JSONObject valutazioneJson = valutazioniArray.getJSONObject(i);
            
            if (!valutazioneJson.has("idDistribuzione") || !valutazioneJson.has("punteggio")) {
                throw new JSONException("Valutazione deve avere idDistribuzione e punteggio");
            }
            
            ValutazioneSingolaDistro valutazione = new ValutazioneSingolaDistro(
                valutazioneJson.getInt("idDistribuzione"),
                valutazioneJson.getDouble("punteggio")
            );
            
            if (valutazioneJson.has("suggerimento")) {
                valutazione.setSuggerimento(valutazioneJson.getString("suggerimento"));
            }
            if (valutazioneJson.has("motivazione")) {
                valutazione.setMotivazione(valutazioneJson.getString("motivazione"));
            }
            
            dto.aggiungiValutazione(valutazione);
        }
        
        if (json.has("noteGenerali")) {
            dto.setNoteGenerali(json.getString("noteGenerali"));
        }
        
        return dto;
    }
    
    public String toJSONString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("idRichiesta", idRichiesta);
        json.put("idEsperto", idEsperto);
        
        JSONArray valutazioniArray = new JSONArray();
        for (ValutazioneSingolaDistro valutazione : valutazioni) {
            JSONObject valJson = new JSONObject();
            valJson.put("idDistribuzione", valutazione.getIdDistribuzione());
            valJson.put("punteggio", valutazione.getPunteggio());
            valJson.put("suggerimento", valutazione.getSuggerimento());
            valJson.put("motivazione", valutazione.getMotivazione());
            valutazioniArray.put(valJson);
        }
        json.put("valutazioni", valutazioniArray);
        
        if (noteGenerali != null) {
            json.put("noteGenerali", noteGenerali);
        }
        
        return json.toString();
    }
    
    // Getters, setters e metodi utility
    public int getIdRichiesta() { return idRichiesta; }
    public void setIdRichiesta(int idRichiesta) { this.idRichiesta = idRichiesta; }
    
    public int getIdEsperto() { return idEsperto; }
    public void setIdEsperto(int idEsperto) { this.idEsperto = idEsperto; }
    
    public List<ValutazioneSingolaDistro> getValutazioni() { return valutazioni; }
    public void setValutazioni(List<ValutazioneSingolaDistro> valutazioni) { 
        this.valutazioni = valutazioni != null ? valutazioni : new ArrayList<>(); 
    }
    
    public String getNoteGenerali() { return noteGenerali; }
    public void setNoteGenerali(String noteGenerali) { this.noteGenerali = noteGenerali; }
    
    public void aggiungiValutazione(ValutazioneSingolaDistro valutazione) {
        if (this.valutazioni == null) {
            this.valutazioni = new ArrayList<>();
        }
        this.valutazioni.add(valutazione);
    }
    
    // Classe interna per singola valutazione
    public static class ValutazioneSingolaDistro {
        private int idDistribuzione;
        private double punteggio;
        private String suggerimento;
        private String motivazione;
        
        public ValutazioneSingolaDistro(int idDistribuzione, double punteggio) {
            this.idDistribuzione = idDistribuzione;
            this.punteggio = punteggio;
        }
        
        // Getters e Setters
        public int getIdDistribuzione() { return idDistribuzione; }
        public void setIdDistribuzione(int idDistribuzione) { this.idDistribuzione = idDistribuzione; }
        
        public double getPunteggio() { return punteggio; }
        public void setPunteggio(double punteggio) { this.punteggio = punteggio; }
        
        public String getSuggerimento() { return suggerimento; }
        public void setSuggerimento(String suggerimento) { this.suggerimento = suggerimento; }
        
        public String getMotivazione() { return motivazione; }
        public void setMotivazione(String motivazione) { this.motivazione = motivazione; }
    }

}
