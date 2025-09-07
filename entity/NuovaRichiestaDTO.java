package entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NuovaRichiestaDTO {
    
    // Dati utente base
    private String livelloEsperienza;
    private String scopoUso;
    private String modalitaSelezione;
    
    // Distribuzioni candidate selezionate dall'utente (oggetti completi)
    private List<DistribuzioneSelezionataDTO> distribuzioniCandidate;
    
    // Esperti selezionati (solo se modalità manuale - oggetti completi)
    private List<EspertoSelezionatoDTO> espertiSelezionati;
    
    // Hardware opzionale
    private String cpu;
    private String ram;
    private String spazioArchiviazione;
    private String schedaVideo;
    private String tipoSistema;
    
    // Note aggiuntive opzionali
    private String noteAggiuntive;
    
    // Costruttori
    public NuovaRichiestaDTO() {
        this.distribuzioniCandidate = new ArrayList<>();
        this.espertiSelezionati = new ArrayList<>();
    }
    
    // Costruttore con parametri essenziali
    public NuovaRichiestaDTO(String livelloEsperienza, String scopoUso, 
                            List<DistribuzioneSelezionataDTO> distribuzioniCandidate, String modalitaSelezione) {
        this.livelloEsperienza = livelloEsperienza;
        this.scopoUso = scopoUso;
        this.distribuzioniCandidate = distribuzioniCandidate != null ? distribuzioniCandidate : new ArrayList<>();
        this.modalitaSelezione = modalitaSelezione;
        this.espertiSelezionati = new ArrayList<>();
    }
    
    /**
     * Deserializza JSON in NuovaRichiestaDTO
     * Pattern basato su InfoUtente.fromJSON() con supporto per oggetti nested
     */
    public static NuovaRichiestaDTO RichiestafromJSON(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        
        // Verifica parametri OBBLIGATORI
        if (!jsonObject.has("livelloEsperienza") || !jsonObject.has("scopoUso") || 
            !jsonObject.has("distribuzioniCandidate") || !jsonObject.has("modalitaSelezione")) {
            throw new JSONException("Parametri obbligatori mancanti: livelloEsperienza, scopoUso, distribuzioniCandidate, modalitaSelezione");
        }
        
        String livelloEsperienza = jsonObject.getString("livelloEsperienza");
        String scopoUso = jsonObject.getString("scopoUso");
        String modalitaSelezione = jsonObject.getString("modalitaSelezione");
        
        // Parse distribuzioni candidate (array di oggetti)
        JSONArray distribuzioniArray = jsonObject.getJSONArray("distribuzioniCandidate");
        List<DistribuzioneSelezionataDTO> distribuzioniCandidate = new ArrayList<>();
        
        for (int i = 0; i < distribuzioniArray.length(); i++) {
            JSONObject distroJson = distribuzioniArray.getJSONObject(i);
            
            // Campi obbligatori per distribuzione
            if (!distroJson.has("id") || !distroJson.has("nome")) {
                throw new JSONException("Distribuzione deve avere almeno id e nome");
            }
            
            int idDistro = distroJson.getInt("id");
            String nomeDistro = distroJson.getString("nome");
            
            DistribuzioneSelezionataDTO distro = new DistribuzioneSelezionataDTO(idDistro,nomeDistro);
           
            
            // Campi opzionali per distribuzione
            if (distroJson.has("versione")) {
                distro.setVersione(distroJson.getString("versione"));
            }
            if (distroJson.has("ambienteDesktop")) {
                distro.setAmbienteDesktop(distroJson.getString("ambienteDesktop"));
            }
            
            distribuzioniCandidate.add(distro);
        }
        
        if (distribuzioniCandidate.isEmpty()) {
            throw new JSONException("Almeno una distribuzione candidata è obbligatoria");
        }
        
        NuovaRichiestaDTO dto = new NuovaRichiestaDTO(livelloEsperienza, scopoUso, 
                                                     distribuzioniCandidate, modalitaSelezione);
        
        // Parametri OPZIONALI - esperti selezionati (array di oggetti)
        if (jsonObject.has("espertiSelezionati")) {
            JSONArray espertiArray = jsonObject.getJSONArray("espertiSelezionati");
            List<EspertoSelezionatoDTO> espertiSelezionati = new ArrayList<>();
            
            for (int i = 0; i < espertiArray.length(); i++) {
                JSONObject espertoJson = espertiArray.getJSONObject(i);
                
                // Campi obbligatori per esperto
                if (!espertoJson.has("id") || !espertoJson.has("nome")) {
                    throw new JSONException("Esperto deve avere almeno id e nome");
                }
                
                int idEsperto = espertoJson.getInt("id");
                String nomeEsperto = espertoJson.getString("nome");
                
                EspertoSelezionatoDTO esperto = new EspertoSelezionatoDTO(idEsperto,nomeEsperto);
                
                // Campi opzionali per esperto
                if (espertoJson.has("specializzazione")) {
                    esperto.setSpecializzazione(espertoJson.getString("specializzazione"));
                }
                if (espertoJson.has("anniEsperienza")) {
                    esperto.setAnniEsperienza(espertoJson.getInt("anniEsperienza"));
                }
                if (espertoJson.has("feedbackMedio")) {
                    esperto.setFeedbackMedio(espertoJson.getDouble("feedbackMedio"));
                }
                
                espertiSelezionati.add(esperto);
            }
            dto.setEspertiSelezionati(espertiSelezionati);
        }
        
        // Parametri OPZIONALI - hardware
        if (jsonObject.has("cpu")) {
            dto.setCpu(jsonObject.getString("cpu"));
        }
        if (jsonObject.has("ram")) {
            dto.setRam(jsonObject.getString("ram"));
        }
        if (jsonObject.has("spazioArchiviazione")) {
            dto.setSpazioArchiviazione(jsonObject.getString("spazioArchiviazione"));
        }
        if (jsonObject.has("schedaVideo")) {
            dto.setSchedaVideo(jsonObject.getString("schedaVideo"));
        }
        if (jsonObject.has("tipoSistema")) {
            dto.setTipoSistema(jsonObject.getString("tipoSistema"));
        }
        if (jsonObject.has("noteAggiuntive")) {
            dto.setNoteAggiuntive(jsonObject.getString("noteAggiuntive"));
        }
        
        return dto;
    }
    
    // Getter e Setter
    public String getLivelloEsperienza() { return livelloEsperienza; }
    public void setLivelloEsperienza(String livelloEsperienza) { this.livelloEsperienza = livelloEsperienza; }
    
    public String getScopoUso() { return scopoUso; }
    public void setScopoUso(String scopoUso) { this.scopoUso = scopoUso; }
    
    public String getModalitaSelezione() { return modalitaSelezione; }
    public void setModalitaSelezione(String modalitaSelezione) { this.modalitaSelezione = modalitaSelezione; }
    
    public List<DistribuzioneSelezionataDTO> getDistribuzioniCandidate() { return distribuzioniCandidate; }
    public void setDistribuzioniCandidate(List<DistribuzioneSelezionataDTO> distribuzioniCandidate) { 
        this.distribuzioniCandidate = distribuzioniCandidate != null ? distribuzioniCandidate : new ArrayList<>(); 
    }
    
    public List<EspertoSelezionatoDTO> getEspertiSelezionati() { return espertiSelezionati; }
    public void setEspertiSelezionati(List<EspertoSelezionatoDTO> espertiSelezionati) { 
        this.espertiSelezionati = espertiSelezionati != null ? espertiSelezionati : new ArrayList<>(); 
    }
    
    public String getCpu() { return cpu; }
    public void setCpu(String cpu) { this.cpu = cpu; }
    
    public String getRam() { return ram; }
    public void setRam(String ram) { this.ram = ram; }
    
    public String getSpazioArchiviazione() { return spazioArchiviazione; }
    public void setSpazioArchiviazione(String spazioArchiviazione) { this.spazioArchiviazione = spazioArchiviazione; }
    
    public String getSchedaVideo() { return schedaVideo; }
    public void setSchedaVideo(String schedaVideo) { this.schedaVideo = schedaVideo; }
    
    public String getTipoSistema() { return tipoSistema; }
    public void setTipoSistema(String tipoSistema) { this.tipoSistema = tipoSistema; }
    
    public String getNoteAggiuntive() { return noteAggiuntive; }
    public void setNoteAggiuntive(String noteAggiuntive) { this.noteAggiuntive = noteAggiuntive; }
    
    // Metodi di utilità
    public void aggiungiDistribuzione(DistribuzioneSelezionataDTO distribuzione) {
        if (this.distribuzioniCandidate == null) {
            this.distribuzioniCandidate = new ArrayList<>();
        }
        this.distribuzioniCandidate.add(distribuzione);
    }
    
    public void aggiungiEsperto(EspertoSelezionatoDTO esperto) {
        if (this.espertiSelezionati == null) {
            this.espertiSelezionati = new ArrayList<>();
        }
        this.espertiSelezionati.add(esperto);
    }
}