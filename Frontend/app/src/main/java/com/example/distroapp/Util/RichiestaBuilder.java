package com.example.distroapp.Util;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.example.distroapp.Entity.DistroSummaryDTO;
import com.example.distroapp.Entity.EspertoSelezionatoDTO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class per costruire il JSON della richiesta completa
 * compatibile con NuovaRichiestaDTO del server
 */
public class RichiestaBuilder {

    private static final String TAG = "RichiestaBuilder";

    private WizardDataManager dataManager;
    private Context context;

    public RichiestaBuilder(Context context) {
        this.context = context;
        this.dataManager = new WizardDataManager(context);
    }

    /**
     * Costruisce il JSON completo della richiesta dai dati salvati nel wizard
     * Formato compatibile con NuovaRichiestaDTO.RichiestafromJSON()
     */
    public String buildRequestJSON(List<DistroSummaryDTO> distribuzioniSelezionate,
                                   List<EspertoSelezionatoDTO> espertiSelezionati,
                                   String modalitaSelezione) throws JSONException {

        JSONObject richiesta = new JSONObject();

        // ===== STEP 3: ESPERIENZA (OBBLIGATORI) =====
        Bundle step3Data = dataManager.getStep3Data();
        String livelloEsperienza = step3Data.getString("esperienza", "");
        String dettagliUtilizzo = step3Data.getString("dettagli", "");
        ArrayList<String> modalitaUtilizzo = step3Data.getStringArrayList("modalita");

        if (livelloEsperienza.isEmpty()) {
            throw new JSONException("Livello esperienza è obbligatorio");
        }

        richiesta.put("livelloEsperienza", livelloEsperienza);
        richiesta.put("scopoUso", dettagliUtilizzo.isEmpty() ? "Uso generico Linux" : dettagliUtilizzo);

        // ===== DISTRIBUZIONI CANDIDATE (OBBLIGATORIO) =====
        if (distribuzioniSelezionate == null || distribuzioniSelezionate.isEmpty()) {
            throw new JSONException("Almeno una distribuzione candidata è obbligatoria");
        }

        JSONArray distribuzioniArray = new JSONArray();
        for (DistroSummaryDTO distro : distribuzioniSelezionate) {
            JSONObject distroJson = new JSONObject();
            distroJson.put("id", distro.getIdDistribuzione()); // Usa idDistribuzione per il server
            distroJson.put("nome", distro.getNomeDisplay());
            // Aggiungi campi opzionali se necessari
            distribuzioniArray.put(distroJson);
        }
        richiesta.put("distribuzioniCandidate", distribuzioniArray);

        // ===== MODALITÀ SELEZIONE ESPERTI =====
        richiesta.put("modalitaSelezione", modalitaSelezione != null ? modalitaSelezione : "AUTOMATICA");

        // ===== ESPERTI SELEZIONATI (se modalità manuale) =====
        if ("MANUALE".equals(modalitaSelezione) && espertiSelezionati != null && !espertiSelezionati.isEmpty()) {
            JSONArray espertiArray = new JSONArray();
            for (EspertoSelezionatoDTO esperto : espertiSelezionati) {
                JSONObject espertoJson = new JSONObject();
                espertoJson.put("id", esperto.getId());
                espertoJson.put("nome", esperto.getNome());
                if (esperto.getSpecializzazione() != null) {
                    espertoJson.put("specializzazione", esperto.getSpecializzazione());
                }
                if (esperto.getAnniEsperienza() > 0) {
                    espertoJson.put("anniEsperienza", esperto.getAnniEsperienza());
                }
                if (esperto.getFeedbackMedio() > 0) {
                    espertoJson.put("feedbackMedio", esperto.getFeedbackMedio());
                }
                espertiArray.put(espertoJson);
            }
            richiesta.put("espertiSelezionati", espertiArray);
        } else {
            richiesta.put("espertiSelezionati", new JSONArray()); // Array vuoto per selezione automatica
        }





        // ===== NOTE AGGIUNTIVE =====
        String noteAggiuntive = buildNoteAggiuntive(step3Data, modalitaUtilizzo, modalitaSelezione);
        if (!noteAggiuntive.isEmpty()) {
            richiesta.put("noteAggiuntive", noteAggiuntive);
        }

        Log.d(TAG, "JSON richiesta costruito: " + richiesta.toString(2));
        return richiesta.toString();
    }

    /**
     * Aggiunge dati hardware al JSON se presenti
     */
    private void addHardwareDataIfPresent(JSONObject richiesta, Bundle hardwareData) throws JSONException {
        String cpu = hardwareData.getString("cpu", "");
        String ram = hardwareData.getString("ram", "");
        String spazioArchiviazione = hardwareData.getString("spazioArchiviazione", "");
        String schedaVideo = hardwareData.getString("schedaVideo", "");
        String tipoSistema = hardwareData.getString("tipoSistema", "");

        // Aggiungi solo se non vuoti
        if (!cpu.isEmpty()) {
            richiesta.put("cpu", cpu);
        }
        if (!ram.isEmpty() && !"Non specificato".equals(ram)) {
            richiesta.put("ram", ram);
        }
        if (!spazioArchiviazione.isEmpty() && !"Non specificato".equals(spazioArchiviazione)) {
            richiesta.put("spazioArchiviazione", spazioArchiviazione);
        }
        if (!schedaVideo.isEmpty()) {
            richiesta.put("schedaVideo", schedaVideo);
        }
        if (!tipoSistema.isEmpty() && !"Non specificato".equals(tipoSistema)) {
            richiesta.put("tipoSistema", tipoSistema);
        }
    }

    /**
     * Costruisce note aggiuntive combinando vari dati del wizard
     */
    private String buildNoteAggiuntive(Bundle step3Data, ArrayList<String> modalitaUtilizzo, String modalitaSelezione) {
        StringBuilder note = new StringBuilder();

        // Aggiungi modalità di utilizzo se presenti
        if (modalitaUtilizzo != null && !modalitaUtilizzo.isEmpty()) {
            note.append("Modalità di utilizzo: ");
            for (int i = 0; i < modalitaUtilizzo.size(); i++) {
                note.append(modalitaUtilizzo.get(i));
                if (i < modalitaUtilizzo.size() - 1) {
                    note.append(", ");
                }
            }
            note.append("\n\n");
        }

        // Aggiungi dettagli utilizzo
        String dettagli = step3Data.getString("dettagli", "");
        if (!dettagli.isEmpty()) {
            note.append("Dettagli utilizzo: ").append(dettagli).append("\n\n");
        }

        // Aggiungi info sulla modalità di selezione
        if ("AUTOMATICA".equals(modalitaSelezione)) {
            note.append("Selezione automatica esperti abilitata - assegna i migliori esperti disponibili");
        } else if ("MANUALE".equals(modalitaSelezione)) {
            note.append("Esperti selezionati manualmente dall'utente");
        }

        return note.toString().trim();
    }

    /**
     * Valida che tutti i dati obbligatori siano presenti
     */
    public boolean validateWizardData(List<DistroSummaryDTO> distribuzioni) {
        // Controlla Step 3 (esperienza)
        Bundle step3Data = dataManager.getStep3Data();
        String livelloEsperienza = step3Data.getString("esperienza", "");
        String dettagli = step3Data.getString("dettagli", "");

        if (livelloEsperienza.isEmpty()) {
            Log.e(TAG, "Validazione fallita: livello esperienza mancante");
            return false;
        }

        if (dettagli.isEmpty() || dettagli.length() < 10) {
            Log.e(TAG, "Validazione fallita: dettagli utilizzo insufficienti");
            return false;
        }

        // Controlla distribuzioni
        if (distribuzioni == null || distribuzioni.isEmpty()) {
            Log.e(TAG, "Validazione fallita: nessuna distribuzione selezionata");
            return false;
        }

        Log.d(TAG, "Validazione wizard completata con successo");
        return true;
    }

    /**
     * Ottiene un riassunto della richiesta per conferma
     */
    public String getRiepilogoRichiesta(List<DistroSummaryDTO> distribuzioni,
                                        List<EspertoSelezionatoDTO> esperti,
                                        String modalitaSelezione) {
        StringBuilder riepilogo = new StringBuilder();

        // Esperienza
        Bundle step3Data = dataManager.getStep3Data();
        riepilogo.append("📋 RIEPILOGO RICHIESTA\n\n");
        riepilogo.append("🎯 Livello esperienza: ").append(step3Data.getString("esperienza", "")).append("\n\n");

        // Distribuzioni
        riepilogo.append("🐧 Distribuzioni di interesse (").append(distribuzioni.size()).append("):\n");
        for (DistroSummaryDTO distro : distribuzioni) {
            riepilogo.append("• ").append(distro.getNomeDisplay()).append("\n");
        }
        riepilogo.append("\n");

        // Esperti
        riepilogo.append("👥 Modalità selezione esperti: ").append(modalitaSelezione).append("\n");
        if ("MANUALE".equals(modalitaSelezione) && esperti != null && !esperti.isEmpty()) {
            riepilogo.append("Esperti selezionati (").append(esperti.size()).append("):\n");
            for (EspertoSelezionatoDTO esperto : esperti) {
                riepilogo.append("• ").append(esperto.getNome()).append("\n");
            }
        } else {
            riepilogo.append("Gli esperti verranno assegnati automaticamente\n");
        }
        riepilogo.append("\n");

        // Hardware (se presente)

        riepilogo.append("✅ Pronto per l'invio agli esperti!");

        return riepilogo.toString();
    }

    /**
     * Pulisce tutti i dati del wizard
     */
    public void clearAllWizardData() {
        dataManager.clearAllData();
        Log.d(TAG, "Tutti i dati del wizard sono stati cancellati");
    }
}