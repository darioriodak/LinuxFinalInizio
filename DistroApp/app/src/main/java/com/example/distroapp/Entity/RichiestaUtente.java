package com.example.distroapp.Entity;

import com.example.distroapp.Entity.DistroSummaryDTO;
import com.example.distroapp.Entity.EspertoSelezionatoDTO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Entità che rappresenta una richiesta completa dell'utente
 * Contiene tutti i dati raccolti attraverso i 5 step del wizard
 */
public class RichiestaUtente {

    // Step 1: Distribuzioni
    private List<DistroSummaryDTO> distribuzioniInteresse;
    private String motivazioneDistribuzioni;

    // Step 2: Hardware
    private String cpu;
    private String ram;
    private String storageSize;
    private String storageType;
    private String gpuType;
    private String gpuDetails;

    // Step 3: Esperienza
    private String livelloEsperienza;
    private List<String> modalitaUtilizzo;
    private String dettagliUtilizzo;

    // Step 4: Esperti
    private List<EspertoSelezionatoDTO> espertiSelezionati;
    private String notePerEsperti;

    // Metadati
    private long dataCreazione;
    private String stato;  // "INVIATA", "IN_VALUTAZIONE", "COMPLETATA"

    // Costruttore vuoto
    public RichiestaUtente() {
        this.distribuzioniInteresse = new ArrayList<>();
        this.modalitaUtilizzo = new ArrayList<>();
        this.espertiSelezionati = new ArrayList<>();
        this.dataCreazione = System.currentTimeMillis();
        this.stato = "BOZZA";
    }

    /**
     * Converte la richiesta in JSON per l'invio al server
     * Riutilizza la struttura del tuo codice esistente
     */
    public String toJSONString() throws JSONException {
        JSONObject json = new JSONObject();

        // Metadati base
        json.put("dataCreazione", dataCreazione);
        json.put("stato", stato);

        // Step 1: Distribuzioni
        if (distribuzioniInteresse != null && !distribuzioniInteresse.isEmpty()) {
            JSONArray distribuzioniArray = new JSONArray();
            for (DistroSummaryDTO distro : distribuzioniInteresse) {
                JSONObject distroJson = new JSONObject();
                distroJson.put("id", distro.getIdDistribuzione()); // Usa idDistribuzione per il server
                distroJson.put("nome", distro.getNomeDisplay());
                distribuzioniArray.put(distroJson);
            }
            json.put("distribuzioniCandidate", distribuzioniArray);
        }

        if (motivazioneDistribuzioni != null && !motivazioneDistribuzioni.isEmpty()) {
            json.put("motivazioneDistro", motivazioneDistribuzioni);
        }

        // Step 2: Hardware (tutti opzionali)
        if (cpu != null && !cpu.isEmpty()) {
            json.put("cpu", cpu);
        }
        if (ram != null && !ram.isEmpty()) {
            json.put("ram", ram);
        }
        if (storageSize != null && !storageSize.isEmpty()) {
            json.put("spazioArchiviazione", storageSize + " GB");
        }
        if (storageType != null && !storageType.isEmpty()) {
            json.put("tipoStorage", storageType);
        }
        if (gpuType != null && !gpuType.isEmpty()) {
            json.put("gpu", gpuType);
        }
        if (gpuDetails != null && !gpuDetails.isEmpty()) {
            json.put("gpuDettagli", gpuDetails);
        }

        // Step 3: Esperienza
        if (livelloEsperienza != null && !livelloEsperienza.isEmpty()) {
            json.put("livelloEsperienza", livelloEsperienza);
            json.put("livelloEsperienzaLinux", livelloEsperienza); // Compatibilità con server esistente
        }

        if (modalitaUtilizzo != null && !modalitaUtilizzo.isEmpty()) {
            JSONArray usiArray = new JSONArray();
            for (String modalita : modalitaUtilizzo) {
                // Mappatura per compatibilità con il server
                String usoMappato = mapModalitaToServerFormat(modalita);
                usiArray.put(usoMappato);
            }
            json.put("usiPrevisti", usiArray);
        }

        if (dettagliUtilizzo != null && !dettagliUtilizzo.isEmpty()) {
            json.put("noteAggiuntive", dettagliUtilizzo);
        }

        // Step 4: Esperti
        if (espertiSelezionati != null && !espertiSelezionati.isEmpty()) {
            JSONArray espertiArray = new JSONArray();
            for (EspertoSelezionatoDTO esperto : espertiSelezionati) {
                JSONObject espertoJson = new JSONObject();
                espertoJson.put("id", esperto.getId());
                espertoJson.put("nome", esperto.getNomeCompleto());
                espertiArray.put(espertoJson);
            }
            json.put("espertiSelezionati", espertiArray);
            json.put("modalitaSelezione", "MANUALE");
        } else {
            // Selezione automatica
            json.put("espertiSelezionati", new JSONArray());
            json.put("modalitaSelezione", "AUTOMATICA");
        }

        if (notePerEsperti != null && !notePerEsperti.isEmpty()) {
            // Rimuovi il flag di selezione automatica se presente
            String noteClean = notePerEsperti.replace("\n[SELEZIONE_AUTOMATICA_ABILITATA]", "");
            if (!noteClean.trim().isEmpty()) {
                json.put("notePerEsperti", noteClean);
            }
        }

        return json.toString();
    }

    /**
     * Mappa le modalità del wizard al formato atteso dal server
     */
    private String mapModalitaToServerFormat(String modalita) {
        switch (modalita) {
            case "Desktop":
                return "Desktop uso quotidiano";
            case "Programmazione":
                return "Sviluppo software";
            case "Server":
                return "Server / Hosting";
            case "Gaming":
                return "Gaming";
            case "Sicurezza":
                return "Sicurezza / Penetration testing";
            case "Multimedia":
                return "Multimedia / Editing";
            case "Ufficio":
                return "Produttività ufficio";
            case "Educazione":
                return "Scopi educativi";
            default:
                return modalita;
        }
    }

    // Getters e Setters
    public List<DistroSummaryDTO> getDistribuzioniInteresse() {
        return distribuzioniInteresse;
    }

    public void setDistribuzioniInteresse(List<DistroSummaryDTO> distribuzioniInteresse) {
        this.distribuzioniInteresse = distribuzioniInteresse;
    }

    public String getMotivazioneDistribuzioni() {
        return motivazioneDistribuzioni;
    }

    public void setMotivazioneDistribuzioni(String motivazioneDistribuzioni) {
        this.motivazioneDistribuzioni = motivazioneDistribuzioni;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getStorageSize() {
        return storageSize;
    }

    public void setStorageSize(String storageSize) {
        this.storageSize = storageSize;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public String getGpuType() {
        return gpuType;
    }

    public void setGpuType(String gpuType) {
        this.gpuType = gpuType;
    }

    public String getGpuDetails() {
        return gpuDetails;
    }

    public void setGpuDetails(String gpuDetails) {
        this.gpuDetails = gpuDetails;
    }

    public String getLivelloEsperienza() {
        return livelloEsperienza;
    }

    public void setLivelloEsperienza(String livelloEsperienza) {
        this.livelloEsperienza = livelloEsperienza;
    }

    public List<String> getModalitaUtilizzo() {
        return modalitaUtilizzo;
    }

    public void setModalitaUtilizzo(List<String> modalitaUtilizzo) {
        this.modalitaUtilizzo = modalitaUtilizzo;
    }

    public String getDettagliUtilizzo() {
        return dettagliUtilizzo;
    }

    public void setDettagliUtilizzo(String dettagliUtilizzo) {
        this.dettagliUtilizzo = dettagliUtilizzo;
    }

    public List<EspertoSelezionatoDTO> getEspertiSelezionati() {
        return espertiSelezionati;
    }

    public void setEspertiSelezionati(List<EspertoSelezionatoDTO> espertiSelezionati) {
        this.espertiSelezionati = espertiSelezionati;
    }

    public String getNotePerEsperti() {
        return notePerEsperti;
    }

    public void setNotePerEsperti(String notePerEsperti) {
        this.notePerEsperti = notePerEsperti;
    }

    public long getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(long dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    @Override
    public String toString() {
        return "RichiestaUtente{" +
                "distribuzioniInteresse=" + distribuzioniInteresse.size() +
                ", livelloEsperienza='" + livelloEsperienza + '\'' +
                ", modalitaUtilizzo=" + modalitaUtilizzo.size() +
                ", espertiSelezionati=" + espertiSelezionati.size() +
                ", stato='" + stato + '\'' +
                '}';
    }
}