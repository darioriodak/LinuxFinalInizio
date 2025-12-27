package entity.dto;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RispostaValutazioneDTO {
    
    // Informazioni sulla richiesta
    private int idRichiesta;
    private String statoRichiesta;
    
    // Risultati valutazioni per distribuzione
    private List<ValutazioneDistribuzioneDTO> valutazioniDistribuzioni;
    
    // Costruttori
    public RispostaValutazioneDTO() {
        this.valutazioniDistribuzioni = new ArrayList<>();
    }
    
    public RispostaValutazioneDTO(int idRichiesta, String statoRichiesta) {
        this.idRichiesta = idRichiesta;
        this.statoRichiesta = statoRichiesta;
        this.valutazioniDistribuzioni = new ArrayList<>();
    }
    
    
    public String toJSONString() throws JSONException {
        JSONObject json = new JSONObject();
        
        json.put("idRichiesta", idRichiesta);
        json.put("statoRichiesta", statoRichiesta);
        
        // Array delle valutazioni
        JSONArray valutazioniArray = new JSONArray();
        for (ValutazioneDistribuzioneDTO valutazione : valutazioniDistribuzioni) {
            JSONObject valutazioneJson = new JSONObject();
            
            // Informazioni distribuzione
            JSONObject distribuzioneJson = new JSONObject();
            distribuzioneJson.put("id", valutazione.getDistribuzione().getId());
            distribuzioneJson.put("nome", valutazione.getDistribuzione().getNome());
            distribuzioneJson.put("versione", valutazione.getDistribuzione().getVersione());
            distribuzioneJson.put("ambienteDesktop", valutazione.getDistribuzione().getAmbienteDesktop());
            valutazioneJson.put("distribuzione", distribuzioneJson);
            
            valutazioneJson.put("punteggioMedio", valutazione.getPunteggioMedio());
            valutazioneJson.put("numeroValutazioni", valutazione.getNumeroValutazioni());
            
            // Array dei commenti degli esperti
            JSONArray commentiArray = new JSONArray();
            for (CommentoEspertoDTO commento : valutazione.getCommentiEsperti()) {
                JSONObject commentoJson = new JSONObject();
                
                // Informazioni esperto
                JSONObject espertoJson = new JSONObject();
                espertoJson.put("id", commento.getEsperto().getId());
                espertoJson.put("nome", commento.getEsperto().getNome());
                espertoJson.put("specializzazione", commento.getEsperto().getSpecializzazione());
                commentoJson.put("esperto", espertoJson);
                
                commentoJson.put("punteggio", commento.getPunteggio());
                commentoJson.put("suggerimento", commento.getSuggerimento());
                commentoJson.put("motivazione", commento.getMotivazione());
                commentiArray.put(commentoJson);
            }
            valutazioneJson.put("commentiEsperti", commentiArray);
            
            valutazioniArray.put(valutazioneJson);
        }
        json.put("valutazioniDistribuzioni", valutazioniArray);
        
        return json.toString();
    }
    
    // Metodo di utilità per aggiungere una valutazione
    public void aggiungiValutazione(ValutazioneDistribuzioneDTO valutazione) {
        if (this.valutazioniDistribuzioni == null) {
            this.valutazioniDistribuzioni = new ArrayList<>();
        }
        this.valutazioniDistribuzioni.add(valutazione);
    }
    
    // Getter e Setter
    public int getIdRichiesta() { return idRichiesta; }
    public void setIdRichiesta(int idRichiesta) { this.idRichiesta = idRichiesta; }
    
    public String getStatoRichiesta() { return statoRichiesta; }
    public void setStatoRichiesta(String statoRichiesta) { this.statoRichiesta = statoRichiesta; }
    
    public List<ValutazioneDistribuzioneDTO> getValutazioniDistribuzioni() { return valutazioniDistribuzioni; }
    public void setValutazioniDistribuzioni(List<ValutazioneDistribuzioneDTO> valutazioniDistribuzioni) { 
        this.valutazioniDistribuzioni = valutazioniDistribuzioni != null ? valutazioniDistribuzioni : new ArrayList<>(); 
    }
    
    // Classe interna per valutazioni di singole distribuzioni
    public static class ValutazioneDistribuzioneDTO {
        private DistribuzioneSelezionataDTO distribuzione;
        private double punteggioMedio;
        private int numeroValutazioni;
        private List<CommentoEspertoDTO> commentiEsperti;
        
        public ValutazioneDistribuzioneDTO() {
            this.commentiEsperti = new ArrayList<>();
        }
        
        public ValutazioneDistribuzioneDTO(DistribuzioneSelezionataDTO distribuzione, 
                                         double punteggioMedio, int numeroValutazioni) {
            this.distribuzione = distribuzione;
            this.punteggioMedio = punteggioMedio;
            this.numeroValutazioni = numeroValutazioni;
            this.commentiEsperti = new ArrayList<>();
        }
        
        // Getter e Setter
        public DistribuzioneSelezionataDTO getDistribuzione() { return distribuzione; }
        public void setDistribuzione(DistribuzioneSelezionataDTO distribuzione) { this.distribuzione = distribuzione; }
        
        public double getPunteggioMedio() { return punteggioMedio; }
        public void setPunteggioMedio(double punteggioMedio) { this.punteggioMedio = punteggioMedio; }
        
        public int getNumeroValutazioni() { return numeroValutazioni; }
        public void setNumeroValutazioni(int numeroValutazioni) { this.numeroValutazioni = numeroValutazioni; }
        
        public List<CommentoEspertoDTO> getCommentiEsperti() { return commentiEsperti; }
        public void setCommentiEsperti(List<CommentoEspertoDTO> commentiEsperti) { 
            this.commentiEsperti = commentiEsperti != null ? commentiEsperti : new ArrayList<>(); 
        }
        
        public void aggiungiCommento(CommentoEspertoDTO commento) {
            if (this.commentiEsperti == null) {
                this.commentiEsperti = new ArrayList<>();
            }
            this.commentiEsperti.add(commento);
        }
    }
    
    // Classe interna per commenti degli esperti
    public static class CommentoEspertoDTO {
        private EspertoSelezionatoDTO esperto;
        private double punteggio;
        private String suggerimento;
        private String motivazione;
        
        public CommentoEspertoDTO() {}
        
        public CommentoEspertoDTO(EspertoSelezionatoDTO esperto, double punteggio, 
                                 String suggerimento, String motivazione) {
            this.esperto = esperto;
            this.punteggio = punteggio;
            this.suggerimento = suggerimento;
            this.motivazione = motivazione;
        }
        
        // Getter e Setter
        public EspertoSelezionatoDTO getEsperto() { return esperto; }
        public void setEsperto(EspertoSelezionatoDTO esperto) { this.esperto = esperto; }
        
        public double getPunteggio() { return punteggio; }
        public void setPunteggio(double punteggio) { this.punteggio = punteggio; }
        
        public String getSuggerimento() { return suggerimento; }
        public void setSuggerimento(String suggerimento) { this.suggerimento = suggerimento; }
        
        public String getMotivazione() { return motivazione; }
        public void setMotivazione(String motivazione) { this.motivazione = motivazione; }
    }
}
