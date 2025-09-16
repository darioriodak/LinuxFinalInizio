package com.example.distroapp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.distroapp.R;
import com.example.distroapp.Entity.DistroSummaryDTO;
import com.example.distroapp.Entity.EspertoSelezionatoDTO;
import com.example.distroapp.Entity.RichiestaUtente;
import com.example.distroapp.GestioneRete.InviaRichiestaTask;
import com.example.distroapp.Util.WizardDataManager;
import com.example.distroapp.Util.RichiestaBuilder;

import java.util.ArrayList;
import java.util.List;

public class Step5RiepilogoActivity extends AppCompatActivity
        implements InviaRichiestaTask.OnRichiestaInviataListener {

    // UI Components
    private TextView tvTitolo, tvDescrizione, tvStepCounter;
    private View progressStep1, progressStep2, progressStep3, progressStep4, progressStep5;

    // Riepilogo Components
    private TextView tvRiepilogoDistribuzioni;
    private TextView tvRiepilogoEsperienza;
    private TextView tvRiepilogoModalita;
    private TextView tvRiepilogoEsperti;
    private TextView tvRiepilogoDettagli;

    // Note finali
    private EditText etNoteFinali;

    // Buttons
    private Button btnIndietro, btnInviaRichiesta;
    private ProgressBar progressBarInvio;

    // Data
    private WizardDataManager dataManager;
    private RichiestaBuilder richiestaBuilder;
    private List<DistroSummaryDTO> distribuzioniSelezionate;
    private List<EspertoSelezionatoDTO> espertiSelezionati;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step5_riepilogo);

        dataManager = new WizardDataManager(this);
        richiestaBuilder = new RichiestaBuilder(this);

        initViews();
        setupProgressIndicator();
        loadWizardData();
        setupRiepilogo();
        setupButtons();
    }

    private void initViews() {
        tvTitolo = findViewById(R.id.tv_titolo_step);
        tvDescrizione = findViewById(R.id.tv_descrizione_step);
        tvStepCounter = findViewById(R.id.tv_step_counter);

        // Progress indicator
        progressStep1 = findViewById(R.id.progress_step_1);
        progressStep2 = findViewById(R.id.progress_step_2);
        progressStep3 = findViewById(R.id.progress_step_3);
        progressStep4 = findViewById(R.id.progress_step_4);
        progressStep5 = findViewById(R.id.progress_step_5);

        // Riepilogo
        tvRiepilogoDistribuzioni = findViewById(R.id.tv_riepilogo_distribuzioni);
        tvRiepilogoEsperienza = findViewById(R.id.tv_riepilogo_esperienza);
        tvRiepilogoModalita = findViewById(R.id.tv_riepilogo_modalita);
        tvRiepilogoEsperti = findViewById(R.id.tv_riepilogo_esperti);
        tvRiepilogoDettagli = findViewById(R.id.tv_riepilogo_dettagli);

        etNoteFinali = findViewById(R.id.et_note_finali);

        // Buttons
        btnIndietro = findViewById(R.id.btn_indietro);
        btnInviaRichiesta = findViewById(R.id.btn_invia_richiesta);
        progressBarInvio = findViewById(R.id.progress_bar_invio);

        distribuzioniSelezionate = new ArrayList<>();
        espertiSelezionati = new ArrayList<>();
    }

    private void setupProgressIndicator() {
        tvTitolo.setText("Riepilogo Richiesta");
        tvDescrizione.setText("Controlla i dati inseriti e invia la tua richiesta di consulenza");
        tvStepCounter.setText("Step 5 di 5");

        // Evidenzia step corrente
        progressStep1.setBackgroundResource(R.drawable.step_completed);
        progressStep2.setBackgroundResource(R.drawable.step_completed);
        progressStep3.setBackgroundResource(R.drawable.step_completed);
        progressStep4.setBackgroundResource(R.drawable.step_completed);
        progressStep5.setBackgroundResource(R.drawable.step_active);
    }

    private void loadWizardData() {
        android.util.Log.d("Step5Activity", "=== LOAD WIZARD DATA ===");

        Bundle allData = dataManager.getAllWizardData();
        android.util.Log.d("Step5Activity", "Bundle keys: " + allData.keySet().toString());

        // Carica distribuzioni da Step 1
        Bundle step1Data = allData.getBundle("step1");
        android.util.Log.d("Step5Activity", "Step1 bundle: " + (step1Data != null ? "EXISTS" : "NULL"));

        if (step1Data != null) {
            android.util.Log.d("Step5Activity", "Step1 keys: " + step1Data.keySet().toString());

            ArrayList<Integer> distribuzioniIds = step1Data.getIntegerArrayList("distribuzioniIds");
            android.util.Log.d("Step5Activity", "Distribuzioni IDs: " +
                    (distribuzioniIds != null ? distribuzioniIds.toString() : "NULL"));

            // ✅ CORREZIONE: Verifica anche SharedPreferences direttamente
            SharedPreferences wizardPrefs = getSharedPreferences("wizard_richiesta", MODE_PRIVATE);
            String savedDistros = wizardPrefs.getString("distribuzioni_selezionate", "");
            android.util.Log.d("Step5Activity", "SharedPreferences distribuzioni: " + savedDistros);

            // Per ora creiamo oggetti mock - in futuro caricherai dal server
            if (distribuzioniIds != null && !distribuzioniIds.isEmpty()) {
                for (Integer id : distribuzioniIds) {
                    DistroSummaryDTO distro = new DistroSummaryDTO();
                    distro.setId(id);
                    distro.setIdDistribuzione(id);
                    distro.setNomeDisplay("Distribuzione #" + id);
                    distribuzioniSelezionate.add(distro);
                    android.util.Log.d("Step5Activity", "Aggiunta distribuzione: " + distro.getNomeDisplay());
                }
            } else if (!savedDistros.isEmpty()) {
                // ✅ FALLBACK: Usa SharedPreferences se WizardDataManager fallisce
                String[] ids = savedDistros.split(",");
                android.util.Log.d("Step5Activity", "Usando fallback SharedPreferences, IDs: " + ids.length);

                for (String idStr : ids) {
                    try {
                        int id = Integer.parseInt(idStr.trim());
                        DistroSummaryDTO distro = new DistroSummaryDTO();
                        distro.setId(id);
                        distro.setIdDistribuzione(id);
                        distro.setNomeDisplay("Distribuzione #" + id);
                        distribuzioniSelezionate.add(distro);
                        android.util.Log.d("Step5Activity", "Fallback - Aggiunta distribuzione: " + distro.getNomeDisplay());
                    } catch (NumberFormatException e) {
                        android.util.Log.e("Step5Activity", "ID non valido: " + idStr);
                    }
                }
            }
        }

        android.util.Log.d("Step5Activity", "Distribuzioni finali caricate: " + distribuzioniSelezionate.size());

        // Step 4: Esperti (per ora lista vuota dato il problema)
        Bundle step4Data = allData.getBundle("step4");
        if (step4Data != null) {
            String modalitaSelezione = step4Data.getString("modalitaSelezione", "AUTOMATICA");
            ArrayList<Integer> espertiIds = step4Data.getIntegerArrayList("espertiIds");

            android.util.Log.d("Step5Activity", "Modalità selezione: " + modalitaSelezione);
            android.util.Log.d("Step5Activity", "Esperti IDs: " +
                    (espertiIds != null ? espertiIds.toString() : "NULL"));

            if ("MANUALE".equals(modalitaSelezione) && espertiIds != null) {
                for (Integer id : espertiIds) {
                    EspertoSelezionatoDTO esperto = new EspertoSelezionatoDTO();
                    esperto.setId(id);
                    esperto.setNome("Esperto #" + id);
                    espertiSelezionati.add(esperto);
                }
            }
        }

        android.util.Log.d("Step5Activity", "=== FINE LOAD WIZARD DATA ===");
    }
    private void setupRiepilogo() {
        // Step 1: Distribuzioni
        StringBuilder distribuzioniText = new StringBuilder();
        distribuzioniText.append("🐧 Distribuzioni selezionate (").append(distribuzioniSelezionate.size()).append("):\n");
        if (distribuzioniSelezionate.isEmpty()) {
            distribuzioniText.append("• Nessuna distribuzione selezionata");
        } else {
            for (DistroSummaryDTO distro : distribuzioniSelezionate) {
                distribuzioniText.append("• ").append(distro.getNomeDisplay()).append("\n");
            }
        }
        tvRiepilogoDistribuzioni.setText(distribuzioniText.toString());

        // Step 3: Esperienza
        Bundle step3Data = dataManager.getStep3Data();
        StringBuilder esperienzaText = new StringBuilder();
        esperienzaText.append("🎯 Livello esperienza: ").append(step3Data.getString("esperienza", "Non specificato")).append("\n\n");

        ArrayList<String> modalita = step3Data.getStringArrayList("modalita");
        if (modalita != null && !modalita.isEmpty()) {
            esperienzaText.append("💻 Modalità di utilizzo:\n");
            for (String mod : modalita) {
                esperienzaText.append("• ").append(mod).append("\n");
            }
        }
        tvRiepilogoEsperienza.setText(esperienzaText.toString());

        // Step 3: Dettagli
        String dettagli = step3Data.getString("dettagli", "");
        if (!dettagli.isEmpty()) {
            tvRiepilogoDettagli.setText("📝 Dettagli utilizzo:\n" + dettagli);
            tvRiepilogoDettagli.setVisibility(View.VISIBLE);
        } else {
            tvRiepilogoDettagli.setVisibility(View.GONE);
        }

        // Step 4: Modalità selezione esperti
        Bundle step4Data = dataManager.getStep4Data();
        String modalitaSelezione = step4Data.getString("modalitaSelezione", "AUTOMATICA");
        StringBuilder modalitaText = new StringBuilder();
        modalitaText.append("👥 Selezione esperti: ").append(modalitaSelezione).append("\n");

        if ("MANUALE".equals(modalitaSelezione) && !espertiSelezionati.isEmpty()) {
            modalitaText.append("Esperti selezionati (").append(espertiSelezionati.size()).append("):\n");
            for (EspertoSelezionatoDTO esperto : espertiSelezionati) {
                modalitaText.append("• ").append(esperto.getNome()).append("\n");
            }
        } else {
            modalitaText.append("Gli esperti verranno assegnati automaticamente\n");
        }
        tvRiepilogoModalita.setText(modalitaText.toString());

        // Note per esperti
        String noteEsperti = step4Data.getString("notePerEsperti", "");
        if (!noteEsperti.isEmpty()) {
            tvRiepilogoEsperti.setText("💬 Note per esperti:\n" + noteEsperti);
            tvRiepilogoEsperti.setVisibility(View.VISIBLE);
        } else {
            tvRiepilogoEsperti.setVisibility(View.GONE);
        }
    }

    private void setupButtons() {
        btnIndietro.setOnClickListener(v -> {
            saveCurrentData();
            onBackPressed();
        });

        btnInviaRichiesta.setOnClickListener(v -> {
            if (validateData()) {
                inviaRichiesta();
            }
        });

        // Auto-save note finali
        etNoteFinali.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                saveCurrentData();
            }
        });
    }

    private boolean validateData() {
        if (distribuzioniSelezionate.isEmpty()) {
            Toast.makeText(this, "Seleziona almeno una distribuzione per continuare", Toast.LENGTH_LONG).show();
            return false;
        }

        Bundle step3Data = dataManager.getStep3Data();
        String esperienza = step3Data.getString("esperienza", "");
        if (esperienza.isEmpty()) {
            Toast.makeText(this, "Livello di esperienza obbligatorio", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void inviaRichiesta() {
        btnInviaRichiesta.setEnabled(false);
        progressBarInvio.setVisibility(View.VISIBLE);

        try {
            // Crea la richiesta usando RichiestaBuilder
            String modalitaSelezione = dataManager.getStep4Data().getString("modalitaSelezione", "AUTOMATICA");
            String jsonRichiesta = richiestaBuilder.buildRequestJSON(
                    distribuzioniSelezionate,
                    espertiSelezionati,
                    modalitaSelezione
            );

            // Crea oggetto RichiestaUtente per compatibilità
            RichiestaUtente richiesta = new RichiestaUtente();
            richiesta.setDistribuzioniInteresse(distribuzioniSelezionate);
            richiesta.setEspertiSelezionati(espertiSelezionati);

            Bundle step3Data = dataManager.getStep3Data();
            richiesta.setLivelloEsperienza(step3Data.getString("esperienza", ""));
            richiesta.setDettagliUtilizzo(step3Data.getString("dettagli", ""));
            richiesta.setModalitaUtilizzo(step3Data.getStringArrayList("modalita"));

            // Note finali
            String noteFinali = etNoteFinali.getText().toString().trim();
            if (!noteFinali.isEmpty()) {
                String noteEsperti = dataManager.getStep4Data().getString("notePerEsperti", "");
                richiesta.setNotePerEsperti(noteEsperti + "\n\nNote finali: " + noteFinali);
            }

            // Invia la richiesta
            InviaRichiestaTask task = new InviaRichiestaTask(this);
            task.execute(richiesta);

        } catch (Exception e) {
            android.util.Log.e("Step5Activity", "Errore nella creazione della richiesta", e);
            onRichiestaError("Errore nella preparazione della richiesta: " + e.getMessage());
        }
    }

    private void saveCurrentData() {
        // Salva note finali se presenti
        String noteFinali = etNoteFinali.getText().toString().trim();
        dataManager.saveStep5Data(true);

        // Potresti salvare le note finali in un campo separato se necessario
    }

    // Implementazione InviaRichiestaTask.OnRichiestaInviataListener
    @Override
    public void onRichiestaInviata(String richiestaId) {
        runOnUiThread(() -> {
            progressBarInvio.setVisibility(View.GONE);
            btnInviaRichiesta.setEnabled(true);

            Toast.makeText(this, "Richiesta inviata con successo!", Toast.LENGTH_LONG).show();

            // Pulisci tutti i dati del wizard
            dataManager.clearAllData();

            // Torna alla Dashboard
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onRichiestaError(String error) {
        runOnUiThread(() -> {
            progressBarInvio.setVisibility(View.GONE);
            btnInviaRichiesta.setEnabled(true);

            Toast.makeText(this, "Errore nell'invio: " + error, Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onBackPressed() {
        saveCurrentData();
        super.onBackPressed();
    }
}