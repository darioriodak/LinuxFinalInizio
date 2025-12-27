package com.example.distroapp.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Gestore centralizzato per i dati del wizard di creazione richiesta
 * Salva automaticamente in SharedPreferences per tutti i 5 step
 */
public class WizardDataManager {

    private static final String PREFS_NAME = "wizard_richiesta";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    // Keys per SharedPreferences Step 1
    private static final String KEY_STEP1_DISTRIBUZIONI = "step1_distribuzioni_ids";
    private static final String KEY_STEP1_MOTIVAZIONE = "step1_motivazione";

    // Keys per SharedPreferences Step 2

    // Keys per SharedPreferences Step 3
    private static final String KEY_STEP3_ESPERIENZA = "step3_esperienza";
    private static final String KEY_STEP3_MODALITA = "step3_modalita_utilizzo";
    private static final String KEY_STEP3_DETTAGLI = "step3_dettagli_utilizzo";
    private static final String KEY_STEP3_MOTIVAZIONE = "step3_motivazione";

    // Keys per SharedPreferences Step 4
    private static final String KEY_STEP4_MODALITA_SELEZIONE = "step4_modalita_selezione";
    private static final String KEY_STEP4_ESPERTI_IDS = "step4_esperti_selezionati";
    private static final String KEY_STEP4_NOTE_ESPERTI = "step4_note_per_esperti";

    // Keys per SharedPreferences Step 5
    private static final String KEY_STEP5_CONFIRMED = "step5_confirmed";
    private static final String KEY_STEP5_TIMESTAMP = "step5_timestamp";

    public WizardDataManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // ===== STEP 1: DISTRIBUZIONI =====

    public void saveStep1Data(List<Integer> distribuzioniIds, String motivazione) {
        // Salva IDs come stringa separata da virgole
        if (distribuzioniIds != null && !distribuzioniIds.isEmpty()) {
            StringBuilder idsString = new StringBuilder();
            for (int i = 0; i < distribuzioniIds.size(); i++) {
                idsString.append(distribuzioniIds.get(i));
                if (i < distribuzioniIds.size() - 1) {
                    idsString.append(",");
                }
            }
            editor.putString(KEY_STEP1_DISTRIBUZIONI, idsString.toString());
        } else {
            editor.remove(KEY_STEP1_DISTRIBUZIONI);
        }

        if (motivazione != null && !motivazione.trim().isEmpty()) {
            editor.putString(KEY_STEP1_MOTIVAZIONE, motivazione.trim());
        } else {
            editor.remove(KEY_STEP1_MOTIVAZIONE);
        }

        editor.apply();
    }

    public Bundle getStep1Data() {
        Bundle bundle = new Bundle();

        // Recupera IDs distribuzioni
        String idsString = prefs.getString(KEY_STEP1_DISTRIBUZIONI, "");
        if (!idsString.isEmpty()) {
            String[] idsArray = idsString.split(",");
            ArrayList<Integer> distribuzioniIds = new ArrayList<>();
            for (String idStr : idsArray) {
                try {
                    distribuzioniIds.add(Integer.parseInt(idStr.trim()));
                } catch (NumberFormatException e) {
                    // Skip ID non validi
                }
            }
            bundle.putIntegerArrayList("distribuzioniIds", distribuzioniIds);
        }

        bundle.putString("motivazione", prefs.getString(KEY_STEP1_MOTIVAZIONE, ""));
        return bundle;
    }

    // ===== STEP 2: HARDWARE =====


    // ===== STEP 3: ESPERIENZA =====

    public void saveStep3Data(String esperienza, List<String> modalitaSelezionate, String dettagli, String motivazione) {
        if (esperienza != null && !esperienza.trim().isEmpty()) {
            editor.putString(KEY_STEP3_ESPERIENZA, esperienza.trim());
        } else {
            editor.remove(KEY_STEP3_ESPERIENZA);
        }

        // Salva modalità come Set (SharedPreferences supporta StringSet)
        if (modalitaSelezionate != null && !modalitaSelezionate.isEmpty()) {
            Set<String> modalitaSet = new HashSet<>(modalitaSelezionate);
            editor.putStringSet(KEY_STEP3_MODALITA, modalitaSet);
        } else {
            editor.remove(KEY_STEP3_MODALITA);
        }

        if (dettagli != null && !dettagli.trim().isEmpty()) {
            editor.putString(KEY_STEP3_DETTAGLI, dettagli.trim());
        } else {
            editor.remove(KEY_STEP3_DETTAGLI);
        }

        if (motivazione != null && !motivazione.trim().isEmpty()) {
            editor.putString(KEY_STEP3_MOTIVAZIONE, motivazione.trim());
        } else {
            editor.remove(KEY_STEP3_MOTIVAZIONE);
        }

        editor.apply();
    }

    public Bundle getStep3Data() {
        Bundle bundle = new Bundle();

        bundle.putString("esperienza", prefs.getString(KEY_STEP3_ESPERIENZA, ""));
        bundle.putString("dettagli", prefs.getString(KEY_STEP3_DETTAGLI, ""));
        bundle.putString("motivazione", prefs.getString(KEY_STEP3_MOTIVAZIONE, ""));

        // Recupera modalità come ArrayList
        Set<String> modalitaSet = prefs.getStringSet(KEY_STEP3_MODALITA, new HashSet<>());
        ArrayList<String> modalitaList = new ArrayList<>(modalitaSet);
        bundle.putStringArrayList("modalita", modalitaList);

        return bundle;
    }

    // ===== STEP 4: SELEZIONE ESPERTI =====

    public void saveStep4Data(String modalitaSelezione, List<Integer> espertiIds, String notePerEsperti) {
        if (modalitaSelezione != null && !modalitaSelezione.trim().isEmpty()) {
            editor.putString(KEY_STEP4_MODALITA_SELEZIONE, modalitaSelezione.trim());
        } else {
            editor.remove(KEY_STEP4_MODALITA_SELEZIONE);
        }

        // Salva IDs esperti come stringa separata da virgole
        if (espertiIds != null && !espertiIds.isEmpty()) {
            StringBuilder idsString = new StringBuilder();
            for (int i = 0; i < espertiIds.size(); i++) {
                idsString.append(espertiIds.get(i));
                if (i < espertiIds.size() - 1) {
                    idsString.append(",");
                }
            }
            editor.putString(KEY_STEP4_ESPERTI_IDS, idsString.toString());
        } else {
            editor.remove(KEY_STEP4_ESPERTI_IDS);
        }

        if (notePerEsperti != null && !notePerEsperti.trim().isEmpty()) {
            editor.putString(KEY_STEP4_NOTE_ESPERTI, notePerEsperti.trim());
        } else {
            editor.remove(KEY_STEP4_NOTE_ESPERTI);
        }

        editor.apply();
    }

    public Bundle getStep4Data() {
        Bundle bundle = new Bundle();

        bundle.putString("modalitaSelezione", prefs.getString(KEY_STEP4_MODALITA_SELEZIONE, "AUTOMATICA"));
        bundle.putString("notePerEsperti", prefs.getString(KEY_STEP4_NOTE_ESPERTI, ""));

        // Recupera IDs esperti
        String idsString = prefs.getString(KEY_STEP4_ESPERTI_IDS, "");
        if (!idsString.isEmpty()) {
            String[] idsArray = idsString.split(",");
            ArrayList<Integer> espertiIds = new ArrayList<>();
            for (String idStr : idsArray) {
                try {
                    espertiIds.add(Integer.parseInt(idStr.trim()));
                } catch (NumberFormatException e) {
                    // Skip ID non validi
                }
            }
            bundle.putIntegerArrayList("espertiIds", espertiIds);
        }

        return bundle;
    }

    // ===== STEP 5: CONFERMA =====

    public void saveStep5Data(boolean confirmed) {
        editor.putBoolean(KEY_STEP5_CONFIRMED, confirmed);
        editor.putLong(KEY_STEP5_TIMESTAMP, System.currentTimeMillis());
        editor.apply();
    }

    public Bundle getStep5Data() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("confirmed", prefs.getBoolean(KEY_STEP5_CONFIRMED, false));
        bundle.putLong("timestamp", prefs.getLong(KEY_STEP5_TIMESTAMP, 0));
        return bundle;
    }

    // ===== UTILITY METHODS =====

    /**
     * Recupera tutti i dati del wizard per creare il JSON finale
     */
    public Bundle getAllWizardData() {
        Bundle allData = new Bundle();

        // Step 1
        Bundle step1 = getStep1Data();
        allData.putBundle("step1", step1);

        // Step 2

        // Step 3
        Bundle step3 = getStep3Data();
        allData.putBundle("step3", step3);

        // Step 4
        Bundle step4 = getStep4Data();
        allData.putBundle("step4", step4);

        // Step 5
        Bundle step5 = getStep5Data();
        allData.putBundle("step5", step5);

        return allData;
    }

    /**
     * Verifica se tutti i dati obbligatori sono stati inseriti
     */
    public boolean isWizardComplete() {
        // Step 1: almeno una distribuzione
        Bundle step1 = getStep1Data();
        ArrayList<Integer> distribuzioni = step1.getIntegerArrayList("distribuzioniIds");
        if (distribuzioni == null || distribuzioni.isEmpty()) {
            return false;
        }

        // Step 3: esperienza e dettagli obbligatori
        Bundle step3 = getStep3Data();
        String esperienza = step3.getString("esperienza", "");
        String dettagli = step3.getString("dettagli", "");
        ArrayList<String> modalita = step3.getStringArrayList("modalita");

        if (esperienza.isEmpty() || dettagli.length() < 10 || modalita == null || modalita.isEmpty()) {
            return false;
        }

        // Step 4: modalità selezione obbligatoria
        Bundle step4 = getStep4Data();
        String modalitaSelezione = step4.getString("modalitaSelezione", "");
        if (modalitaSelezione.isEmpty()) {
            return false;
        }

        // Se modalità manuale, deve avere almeno un esperto
        if ("MANUALE".equals(modalitaSelezione)) {
            ArrayList<Integer> esperti = step4.getIntegerArrayList("espertiIds");
            if (esperti == null || esperti.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Cancella tutti i dati del wizard
     */
    public void clearAllData() {
        editor.clear();
        editor.apply();
    }

    /**
     * Cancella dati di uno step specifico
     */
    public void clearStepData(int stepNumber) {
        switch (stepNumber) {
            case 1:
                editor.remove(KEY_STEP1_DISTRIBUZIONI);
                editor.remove(KEY_STEP1_MOTIVAZIONE);
                break;
            case 3:
                editor.remove(KEY_STEP3_ESPERIENZA);
                editor.remove(KEY_STEP3_MODALITA);
                editor.remove(KEY_STEP3_DETTAGLI);
                editor.remove(KEY_STEP3_MOTIVAZIONE);
                break;
            case 4:
                editor.remove(KEY_STEP4_MODALITA_SELEZIONE);
                editor.remove(KEY_STEP4_ESPERTI_IDS);
                editor.remove(KEY_STEP4_NOTE_ESPERTI);
                break;
            case 5:
                editor.remove(KEY_STEP5_CONFIRMED);
                editor.remove(KEY_STEP5_TIMESTAMP);
                break;
        }
        editor.apply();
    }
}

