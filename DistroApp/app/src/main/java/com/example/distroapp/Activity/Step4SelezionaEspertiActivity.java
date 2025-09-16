package com.example.distroapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.distroapp.R;
import com.example.distroapp.Adapter.EspertiAdapter;
import com.example.distroapp.Entity.EspertoSelezionatoDTO;
import com.example.distroapp.GestioneRete.LoadEspertiTask;
import com.example.distroapp.Util.WizardDataManager;

import java.util.ArrayList;
import java.util.List;

public class Step4SelezionaEspertiActivity extends AppCompatActivity
        implements LoadEspertiTask.OnEspertiLoadedListener, EspertiAdapter.OnEspertiSelezionatiListener {

    // UI Components
    private TextView tvTitolo, tvDescrizione, tvStepCounter;
    private View progressStep1, progressStep2, progressStep3, progressStep4, progressStep5;

    // Modalità selezione
    private RadioGroup radioGroupModalita;
    private RadioButton rbAutomatica, rbManuale;

    // Selezione manuale esperti
    private LinearLayout layoutSelezioneManuale;
    private RecyclerView recyclerViewEsperti;
    private ProgressBar progressBarEsperti;
    private TextView tvErroreEsperti;
    private TextView tvInfoSelezione;

    // Note per esperti
    private androidx.cardview.widget.CardView layoutNoteEsperti;
    private EditText etNotePerEsperti;

    private Button btnIndietro, btnAvanti;

    // Data
    private EspertiAdapter espertiAdapter;
    private List<EspertoSelezionatoDTO> espertiDisponibili = new ArrayList<>();
    private List<EspertoSelezionatoDTO> espertiSelezionati = new ArrayList<>();
    private WizardDataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step4_seleziona_esperti);

        dataManager = new WizardDataManager(this);

        initViews();
        setupProgressIndicator();
        setupRadioGroups();
        setupRecyclerView();
        setupButtons();
        loadSavedData();
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

        // Modalità selezione
        radioGroupModalita = findViewById(R.id.radio_group_modalita);
        rbAutomatica = findViewById(R.id.rb_automatica);
        rbManuale = findViewById(R.id.rb_manuale);

        // Selezione manuale
        layoutSelezioneManuale = findViewById(R.id.layout_selezione_manuale);
        recyclerViewEsperti = findViewById(R.id.recycler_view_esperti);
        progressBarEsperti = findViewById(R.id.progress_bar_esperti);
        tvErroreEsperti = findViewById(R.id.tv_errore_esperti);
        tvInfoSelezione = findViewById(R.id.tv_info_selezione);

        // Note
        layoutNoteEsperti = findViewById(R.id.layout_note_esperti);
        etNotePerEsperti = findViewById(R.id.et_note_per_esperti);

        // Buttons
        btnIndietro = findViewById(R.id.btn_indietro);
        btnAvanti = findViewById(R.id.btn_avanti);
    }

    private void setupProgressIndicator() {
        tvTitolo.setText("Selezione Esperti");
        tvDescrizione.setText("Scegli come selezionare gli esperti per la tua consulenza");
        tvStepCounter.setText("Step 4 di 5");

        // Evidenzia step corrente
        progressStep1.setBackgroundResource(R.drawable.step_completed);
        progressStep2.setBackgroundResource(R.drawable.step_completed);
        progressStep3.setBackgroundResource(R.drawable.step_completed);
        progressStep4.setBackgroundResource(R.drawable.step_active);
        progressStep5.setBackgroundResource(R.drawable.step_inactive);
    }

    private void setupRadioGroups() {
        // Default: selezione automatica
        rbAutomatica.setChecked(true);
        rbManuale.setChecked(false);
        layoutSelezioneManuale.setVisibility(View.GONE);

        radioGroupModalita.setOnCheckedChangeListener((group, checkedId) -> {
            android.util.Log.d("Step4Activity", "RadioGroup cambiato: checkedId = " + checkedId);
            android.util.Log.d("Step4Activity", "R.id.rb_automatica = " + R.id.rb_automatica);
            android.util.Log.d("Step4Activity", "R.id.rb_manuale = " + R.id.rb_manuale);

            if (checkedId == R.id.rb_automatica) {
                android.util.Log.d("Step4Activity", "Selezionata modalità AUTOMATICA");
                rbAutomatica.setChecked(true);
                rbManuale.setChecked(false);

                // Modalità automatica
                layoutSelezioneManuale.setVisibility(View.GONE);
                tvDescrizione.setText("Il sistema selezionerà automaticamente i migliori esperti disponibili");
                espertiSelezionati.clear();
                updateInfoSelezione();

            } else if (checkedId == R.id.rb_manuale) {
                android.util.Log.d("Step4Activity", "Selezionata modalità MANUALE");
                rbManuale.setChecked(true);
                rbAutomatica.setChecked(false);

                // Modalità manuale
                layoutSelezioneManuale.setVisibility(View.VISIBLE);
                tvDescrizione.setText("Seleziona manualmente gli esperti che preferisci (massimo 3)");

                android.util.Log.d("Step4Activity", "espertiDisponibili.size() = " + espertiDisponibili.size());

                // Carica esperti se non già caricati
                if (espertiDisponibili.isEmpty()) {
                    android.util.Log.d("Step4Activity", "Caricando esperti...");
                    loadEsperti();
                } else {
                    android.util.Log.d("Step4Activity", "Esperti già caricati, mostrando RecyclerView");
                    // Se già caricati, mostra subito la RecyclerView
                    recyclerViewEsperti.setVisibility(View.VISIBLE);
                    updateInfoSelezione();
                }
            }

            saveCurrentData();
            updateButtonState();
        });
    }

    private void setupRecyclerView() {
        espertiAdapter = new EspertiAdapter(espertiDisponibili, this);
        recyclerViewEsperti.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEsperti.setAdapter(espertiAdapter);
    }

    private void setupButtons() {
        btnIndietro.setOnClickListener(v -> {
            saveCurrentData();
            onBackPressed();
        });

        btnAvanti.setOnClickListener(v -> {
            if (validateStep()) {
                saveCurrentData();
                proceedToNextStep();
            }
        });

        // Auto-save sulle note
        etNotePerEsperti.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                saveCurrentData();
            }
        });

        updateButtonState();
    }

    private void loadEsperti() {
        progressBarEsperti.setVisibility(View.VISIBLE);
        tvErroreEsperti.setVisibility(View.GONE);
        recyclerViewEsperti.setVisibility(View.GONE);

        try {
            android.util.Log.d("Step4Activity", "Iniziando caricamento esperti...");

            // ✅ SEMPLIFICATO: Non serve più passare wizardData
            LoadEspertiTask task = new LoadEspertiTask(this);
            task.execute();

        } catch (Exception e) {
            android.util.Log.e("Step4Activity", "Errore nel caricamento esperti", e);
            onEspertiError("Errore interno: " + e.getMessage());
        }
    }

    private boolean validateStep() {
        StringBuilder errors = new StringBuilder();
        boolean isValid = true;

        // Se modalità manuale, deve avere almeno 1 esperto selezionato
        if (rbManuale.isChecked() && espertiSelezionati.isEmpty()) {
            errors.append("• Seleziona almeno un esperto per la modalità manuale\n");
            isValid = false;
        }

        if (!isValid) {
            Toast.makeText(this, "Completa la selezione:\n" + errors.toString(),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void saveCurrentData() {
        String modalitaSelezione = rbAutomatica.isChecked() ? "AUTOMATICA" : "MANUALE";

        List<Integer> espertiIds = new ArrayList<>();
        for (EspertoSelezionatoDTO esperto : espertiSelezionati) {
            espertiIds.add(esperto.getId());
        }

        String notePerEsperti = etNotePerEsperti.getText().toString().trim();

        // Se modalità automatica e ha scritto note, aggiungi flag
        if ("AUTOMATICA".equals(modalitaSelezione) && !notePerEsperti.isEmpty()) {
            notePerEsperti += "\n[SELEZIONE_AUTOMATICA_ABILITATA]";
        }

        dataManager.saveStep4Data(modalitaSelezione, espertiIds, notePerEsperti);
    }

    private void loadSavedData() {
        Bundle savedData = dataManager.getStep4Data();

        // Carica modalità selezione
        String modalitaSelezione = savedData.getString("modalitaSelezione", "AUTOMATICA");

        if ("MANUALE".equals(modalitaSelezione)) {
            // ✅ CORREZIONE: Imposta esplicitamente entrambi i RadioButton
            rbManuale.setChecked(true);
            rbAutomatica.setChecked(false);

            layoutSelezioneManuale.setVisibility(View.VISIBLE);
            tvDescrizione.setText("Seleziona manualmente gli esperti che preferisci (massimo 3)");

            // Carica esperti se modalità manuale
            if (espertiDisponibili.isEmpty()) {
                loadEsperti();
            } else {
                // Se già caricati, mostra la RecyclerView
                recyclerViewEsperti.setVisibility(View.VISIBLE);
            }

            // Ripristina esperti selezionati dopo il caricamento
            ArrayList<Integer> savedEspertiIds = savedData.getIntegerArrayList("espertiIds");
            if (savedEspertiIds != null && !savedEspertiIds.isEmpty()) {
                // Salva IDs per ripristinarli dopo il caricamento degli esperti
                // (verrà fatto in onEspertiLoaded)
            }
        } else {

            rbAutomatica.setChecked(true);
            rbManuale.setChecked(false);

            layoutSelezioneManuale.setVisibility(View.GONE);
            tvDescrizione.setText("Il sistema selezionerà automaticamente i migliori esperti disponibili");
        }

        // Carica note
        String savedNote = savedData.getString("notePerEsperti", "");
        // Rimuovi flag automatico se presente
        savedNote = savedNote.replace("\n[SELEZIONE_AUTOMATICA_ABILITATA]", "");
        etNotePerEsperti.setText(savedNote);

        updateButtonState();
        updateInfoSelezione();
    }

    private void updateInfoSelezione() {
        if (rbAutomatica.isChecked()) {
            tvInfoSelezione.setText("✨ Il sistema selezionerà automaticamente i migliori esperti basandosi sui tuoi requisiti");
            tvInfoSelezione.setVisibility(View.VISIBLE);
        } else {
            int selected = espertiSelezionati.size();
            if (selected == 0) {
                tvInfoSelezione.setText("Seleziona da 1 a 3 esperti dalla lista sottostante");
            } else {
                tvInfoSelezione.setText("Hai selezionato " + selected + "/3 esperti");
            }
            tvInfoSelezione.setVisibility(View.VISIBLE);
        }
    }

    private void updateButtonState() {
        boolean isValid = false;

        if (rbAutomatica.isChecked()) {
            isValid = true; // Automatica è sempre valida
        } else if (rbManuale.isChecked()) {
            isValid = !espertiSelezionati.isEmpty(); // Manuale richiede almeno 1 esperto
        }

        btnAvanti.setEnabled(isValid);

        if (isValid) {
            btnAvanti.setText("Avanti - Riepilogo");
            btnAvanti.setAlpha(1.0f);
        } else {
            if (rbManuale.isChecked()) {
                btnAvanti.setText("Seleziona almeno un esperto");
            } else {
                btnAvanti.setText("Seleziona modalità");
            }
            btnAvanti.setAlpha(0.5f);
        }
    }

    private void proceedToNextStep() {
        Intent intent = new Intent(this, Step5RiepilogoActivity.class);
        startActivity(intent);
    }

    // ===== IMPLEMENTAZIONE LoadEspertiTask.OnEspertiLoadedListener =====

    @Override
    public void onEspertiLoaded(List<EspertoSelezionatoDTO> esperti) {
        progressBarEsperti.setVisibility(View.GONE);

        android.util.Log.d("Step4Activity", "Ricevuti " + (esperti != null ? esperti.size() : 0) + " esperti");

        if (esperti != null && !esperti.isEmpty()) {
            espertiDisponibili.clear();
            espertiDisponibili.addAll(esperti);
            espertiAdapter.updateEsperti(espertiDisponibili);

            // ✅ CORREZIONE: Forza sempre la visibilità se modalità manuale è selezionata
            if (rbManuale.isChecked()) {
                recyclerViewEsperti.setVisibility(View.VISIBLE);
                recyclerViewEsperti.requestLayout(); // Forza il ricalcolo del layout

                tvErroreEsperti.setVisibility(View.GONE);

                // Ripristina selezioni salvate
                restoreSavedSelections();

                android.util.Log.d("Step4Activity", "RecyclerView FORZATO visible con " + esperti.size() + " esperti");
            }

        } else {
            android.util.Log.e("Step4Activity", "Lista esperti vuota o null");
            tvErroreEsperti.setText("Nessun esperto disponibile al momento");
            tvErroreEsperti.setVisibility(View.VISIBLE);
            recyclerViewEsperti.setVisibility(View.GONE);
        }

        updateInfoSelezione();
        updateButtonState();
    }

    @Override
    public void onEspertiError(String error) {
        progressBarEsperti.setVisibility(View.GONE);
        tvErroreEsperti.setText("Errore nel caricamento esperti: " + error);
        tvErroreEsperti.setVisibility(View.VISIBLE);
        recyclerViewEsperti.setVisibility(View.GONE);

        android.util.Log.e("Step4Activity", "Errore caricamento esperti: " + error);
    }

    // ===== IMPLEMENTAZIONE EspertiAdapter.OnEspertiSelezionatiListener =====

    @Override
    public void onEspertiSelezionati(List<EspertoSelezionatoDTO> espertiSelezionati) {
        this.espertiSelezionati.clear();
        this.espertiSelezionati.addAll(espertiSelezionati);

        updateInfoSelezione();
        updateButtonState();
        saveCurrentData();

        android.util.Log.d("Step4Activity", "Esperti selezionati: " + espertiSelezionati.size());
    }

    // ===== METODI DI UTILITÀ =====

    private void restoreSavedSelections() {
        Bundle savedData = dataManager.getStep4Data();
        ArrayList<Integer> savedEspertiIds = savedData.getIntegerArrayList("espertiIds");

        if (savedEspertiIds != null && !savedEspertiIds.isEmpty()) {
            for (Integer idEsperto : savedEspertiIds) {
                espertiAdapter.selezionaEsperto(idEsperto);
            }
        }
    }

    /**
     * Forza la visibilità della RecyclerView nella UI thread
     */
    private void forceRecyclerViewVisibility() {
        runOnUiThread(() -> {
            android.util.Log.d("Step4Activity", "Forzando visibilità RecyclerView nella UI thread...");

            // Forza tutti i container
            layoutSelezioneManuale.setVisibility(View.VISIBLE);
            recyclerViewEsperti.setVisibility(View.VISIBLE);

            // Nasconde errori e progress
            tvErroreEsperti.setVisibility(View.GONE);
            progressBarEsperti.setVisibility(View.GONE);

            // Forza il refresh del layout
            recyclerViewEsperti.requestLayout();
            recyclerViewEsperti.invalidate();

            // Log finale
            android.util.Log.d("Step4Activity", "RecyclerView forzata: visibility = " +
                    (recyclerViewEsperti.getVisibility() == View.VISIBLE ? "VISIBLE" : "NOT_VISIBLE"));
            android.util.Log.d("Step4Activity", "Adapter item count = " + espertiAdapter.getItemCount());
        });
    }

    @Override
    public void onBackPressed() {
        saveCurrentData();
        super.onBackPressed();
    }
}