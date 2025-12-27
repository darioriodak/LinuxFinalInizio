package entity.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// QUANDO L'ESPERTO CLICCA SULLA NOTIFICA QUESTE SONO LE INFO CHE GLI VENGONO MOSTRATE
public class DettagliRichiestaPerEspertoDTO {
	
	private int idRichiesta;
    private Timestamp dataCreazione;
    private Timestamp scadenza;
    private String statoRichiesta;
    private String modalitaSelezione;
    
    // Informazioni utente (anonimizzate)
    private InfoUtenteAnonimizzataDTO infoUtente;
    
    // Hardware opzionale
    private HardwareInfoDTO hardware;
    
    // Distribuzioni da valutare
    private List<DistribuzioneCompletaDTO> distribuzioniDaValutare;
    
    // Note aggiuntive dell'utente
    private String noteAggiuntive;
    
    // Altri esperti assegnati (per trasparenza)
    private List<EspertoCollegaDTO> altriEsperti;
    
    // Stato valutazione per questo esperto
    private boolean giaValutata;
    
    public DettagliRichiestaPerEspertoDTO() {
        this.distribuzioniDaValutare = new ArrayList<>();
        this.altriEsperti = new ArrayList<>();
    }
    
    public DettagliRichiestaPerEspertoDTO(int idRichiesta, Timestamp dataCreazione, String statoRichiesta) {
        this.idRichiesta = idRichiesta;
        this.dataCreazione = dataCreazione;
        this.statoRichiesta = statoRichiesta;
        this.distribuzioniDaValutare = new ArrayList<>();
        this.altriEsperti = new ArrayList<>();
    }
    
    /**
     * Serializza in JSON per invio al frontend dell'esperto
     */
    public String toJSONString() throws JSONException {
        JSONObject json = new JSONObject();
        
        // Informazioni base
        json.put("idRichiesta", idRichiesta);
        json.put("dataCreazione", dataCreazione.toString());
        if (scadenza != null) {
            json.put("scadenza", scadenza.toString());
        }
        json.put("statoRichiesta", statoRichiesta);
        json.put("modalitaSelezione", modalitaSelezione);
        json.put("giaValutata", giaValutata);
        
        // Informazioni utente anonimizzate
        if (infoUtente != null) {
            JSONObject infoUtenteJson = new JSONObject();
            infoUtenteJson.put("livelloEsperienza", infoUtente.getLivelloEsperienza());
            infoUtenteJson.put("scopoUso", infoUtente.getScopoUso());
            infoUtenteJson.put("etaApprossimativa", infoUtente.getEtaApprossimativa());
            json.put("infoUtente", infoUtenteJson);
        }
        
        // Hardware
        if (hardware != null) {
            JSONObject hardwareJson = new JSONObject();
            hardwareJson.put("cpu", hardware.getCpu());
            hardwareJson.put("ram", hardware.getRam());
            hardwareJson.put("spazioArchiviazione", hardware.getSpazioArchiviazione());
            hardwareJson.put("schedaVideo", hardware.getSchedaVideo());
            hardwareJson.put("tipoSistema", hardware.getTipoSistema());
            json.put("hardware", hardwareJson);
        }
        
        // Distribuzioni da valutare
        JSONArray distribuzioniArray = new JSONArray();
        for (DistribuzioneCompletaDTO distro : distribuzioniDaValutare) {
            JSONObject distroJson = new JSONObject();
            distroJson.put("id", distro.getId());
            distroJson.put("nome", distro.getNome());
            distroJson.put("versione", distro.getVersione());
            distroJson.put("ambienteDesktop", distro.getAmbienteDesktop());
            distroJson.put("categoria", distro.getCategoria());
            distroJson.put("descrizione", distro.getDescrizione());
            distroJson.put("requisiti", distro.getRequisitiHardware());
            distribuzioniArray.put(distroJson);
        }
        json.put("distribuzioniDaValutare", distribuzioniArray);
        
        // Note aggiuntive
        if (noteAggiuntive != null) {
            json.put("noteAggiuntive", noteAggiuntive);
        }
        
        // Altri esperti
        JSONArray espertiArray = new JSONArray();
        for (EspertoCollegaDTO esperto : altriEsperti) {
            JSONObject espertoJson = new JSONObject();
            espertoJson.put("nome", esperto.getNomeAnonimizzato());
            espertoJson.put("specializzazione", esperto.getSpecializzazione());
            espertoJson.put("haValutato", esperto.isHaValutato());
            espertiArray.put(espertoJson);
        }
        json.put("altriEsperti", espertiArray);
        
        return json.toString();
    }
    
    // Metodi di utilità
    public void aggiungiDistribuzione(DistribuzioneCompletaDTO distribuzione) {
        if (this.distribuzioniDaValutare == null) {
            this.distribuzioniDaValutare = new ArrayList<>();
        }
        this.distribuzioniDaValutare.add(distribuzione);
    }
    
    public void aggiungiEspertoCollega(EspertoCollegaDTO esperto) {
        if (this.altriEsperti == null) {
            this.altriEsperti = new ArrayList<>();
        }
        this.altriEsperti.add(esperto);
    }
    
    // Getters e Setters
    public int getIdRichiesta() { return idRichiesta; }
    public void setIdRichiesta(int idRichiesta) { this.idRichiesta = idRichiesta; }
    
    public Timestamp getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(Timestamp dataCreazione) { this.dataCreazione = dataCreazione; }
    
    public Timestamp getScadenza() { return scadenza; }
    public void setScadenza(Timestamp scadenza) { this.scadenza = scadenza; }
    
    public String getStatoRichiesta() { return statoRichiesta; }
    public void setStatoRichiesta(String statoRichiesta) { this.statoRichiesta = statoRichiesta; }
    
    public String getModalitaSelezione() { return modalitaSelezione; }
    public void setModalitaSelezione(String modalitaSelezione) { this.modalitaSelezione = modalitaSelezione; }
    
    public InfoUtenteAnonimizzataDTO getInfoUtente() { return infoUtente; }
    public void setInfoUtente(InfoUtenteAnonimizzataDTO infoUtente) { this.infoUtente = infoUtente; }
    
    public HardwareInfoDTO getHardware() { return hardware; }
    public void setHardware(HardwareInfoDTO hardware) { this.hardware = hardware; }
    
    public List<DistribuzioneCompletaDTO> getDistribuzioniDaValutare() { return distribuzioniDaValutare; }
    public void setDistribuzioniDaValutare(List<DistribuzioneCompletaDTO> distribuzioniDaValutare) { 
        this.distribuzioniDaValutare = distribuzioniDaValutare != null ? distribuzioniDaValutare : new ArrayList<>(); 
    }
    
    public String getNoteAggiuntive() { return noteAggiuntive; }
    public void setNoteAggiuntive(String noteAggiuntive) { this.noteAggiuntive = noteAggiuntive; }
    
    public List<EspertoCollegaDTO> getAltriEsperti() { return altriEsperti; }
    public void setAltriEsperti(List<EspertoCollegaDTO> altriEsperti) { 
        this.altriEsperti = altriEsperti != null ? altriEsperti : new ArrayList<>(); 
    }
    
    public boolean isGiaValutata() { return giaValutata; }
    public void setGiaValutata(boolean giaValutata) { this.giaValutata = giaValutata; }

}
