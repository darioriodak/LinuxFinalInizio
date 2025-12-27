package com.example.distroapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.distroapp.R;
import com.example.distroapp.Util.WizardDataManager;

import java.util.ArrayList;
import java.util.List;

public class Step3EsperienzaActivity extends AppCompatActivity {

    // UI Components
    private TextView tvTitolo, tvDescrizione, tvStepCounter;
    private View progressStep1, progressStep2, progressStep3, progressStep4, progressStep5;

    // Esperienza Form Components
    private Spinner spinnerEsperienza;
    private LinearLayout layoutModalita;
    private CheckBox cbDesktop, cbProgrammazione, cbServer, cbGaming, cbSicurezza, cbMultimedia, cbUfficio, cbEducazione;
    private EditText etDettagliUtilizzo;

    private Button btnIndietro, btnAvanti;

    // Data Manager
    private WizardDataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step3_esperienza);

        dataManager = new WizardDataManager(this);

        initViews();
        setupProgressIndicator();
        setupSpinners();
        setupCheckboxes();
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

        // Form components
        spinnerEsperienza = findViewById(R.id.spinner_esperienza);
        layoutModalita = findViewById(R.id.layout_modalita);

        cbDesktop = findViewById(R.id.cb_desktop);
        cbProgrammazione = findViewById(R.id.cb_programmazione);
        cbServer = findViewById(R.id.cb_server);
        cbGaming = findViewById(R.id.cb_gaming);
        cbSicurezza = findViewById(R.id.cb_sicurezza);
        cbMultimedia = findViewById(R.id.cb_multimedia);
        cbUfficio = findViewById(R.id.cb_ufficio);
        cbEducazione = findViewById(R.id.cb_educazione);

        etDettagliUtilizzo = findViewById(R.id.et_dettagli_utilizzo);

        btnIndietro = findViewById(R.id.btn_indietro);
        btnAvanti = findViewById(R.id.btn_avanti);
    }

    private void setupProgressIndicator() {
        tvTitolo.setText("Esperienza Linux");
        tvDescrizione.setText("Raccontaci la tua esperienza e come intendi usare Linux");
        tvStepCounter.setText("Step 3 di 5");

        // Evidenzia step corrente
        progressStep1.setBackgroundResource(R.drawable.step_completed);
        progressStep2.setBackgroundResource(R.drawable.step_completed);
        progressStep3.setBackgroundResource(R.drawable.step_active);
        progressStep4.setBackgroundResource(R.drawable.step_inactive);
        progressStep5.setBackgroundResource(R.drawable.step_inactive);
    }

    private void setupSpinners() {
        // Setup Esperienza Spinner - compatibile con LivelloEsperienza enum
        String[] esperienzaOptions = {
                "Seleziona il tuo livello",
                "PRINCIPIANTE",
                "INTERMEDIO",
                "AVANZATO"
        };
        ArrayAdapter<String> esperienzaAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, esperienzaOptions);
        esperienzaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEsperienza.setAdapter(esperienzaAdapter);

        // Auto-save su selezione
        spinnerEsperienza.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // Skip "Seleziona il tuo livello"
                    saveCurrentData();
                    updateButtonState();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupCheckboxes() {
        CheckBox[] checkboxes = {cbDesktop, cbProgrammazione, cbServer, cbGaming,
                cbSicurezza, cbMultimedia, cbUfficio, cbEducazione};

        View.OnClickListener checkboxListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCurrentData();
                updateButtonState();
            }
        };

        for (CheckBox cb : checkboxes) {
            cb.setOnClickListener(checkboxListener);
        }
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

        // Auto-save sui campi di testo
        etDettagliUtilizzo.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                saveCurrentData();
                updateButtonState();
            }
        });

        updateButtonState();
    }

    private boolean validateStep() {
        StringBuilder errors = new StringBuilder();
        boolean isValid = true;

        // Validazione esperienza obbligatoria
        if (spinnerEsperienza.getSelectedItemPosition() <= 0) {
            errors.append("• Seleziona il tuo livello di esperienza\n");
            isValid = false;
        }

        // Validazione modalità utilizzo (almeno una)
        List<String> modalitaSelezionate = getModalitaSelezionate();
        if (modalitaSelezionate.isEmpty()) {
            errors.append("• Seleziona almeno una modalità di utilizzo\n");
            isValid = false;
        }

        // Validazione dettagli utilizzo
        String dettagli = etDettagliUtilizzo.getText().toString().trim();
        if (dettagli.length() < 10) {
            errors.append("• Descrivi come userai Linux (minimo 10 caratteri)\n");
            etDettagliUtilizzo.setError("Descrizione troppo breve");
            isValid = false;
        }

        if (!isValid) {
            Toast.makeText(this, "Completa i campi obbligatori:\n" + errors.toString(),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void saveCurrentData() {
        // Raccogli esperienza selezionata
        String esperienza = "";
        if (spinnerEsperienza.getSelectedItemPosition() > 0) {
            esperienza = spinnerEsperienza.getSelectedItem().toString();
        }

        // Raccogli modalità selezionate
        List<String> modalitaSelezionate = getModalitaSelezionate();

        // Raccogli dettagli utilizzo
        String dettagli = etDettagliUtilizzo.getText().toString().trim();

        // Salva tramite WizardDataManager
        dataManager.saveStep3Data(esperienza, modalitaSelezionate, dettagli, "");
    }

    private List<String> getModalitaSelezionate() {
        List<String> modalita = new ArrayList<>();

        if (cbDesktop.isChecked()) modalita.add("Desktop");
        if (cbProgrammazione.isChecked()) modalita.add("Programmazione");
        if (cbServer.isChecked()) modalita.add("Server");
        if (cbGaming.isChecked()) modalita.add("Gaming");
        if (cbSicurezza.isChecked()) modalita.add("Sicurezza");
        if (cbMultimedia.isChecked()) modalita.add("Multimedia");
        if (cbUfficio.isChecked()) modalita.add("Ufficio");
        if (cbEducazione.isChecked()) modalita.add("Educazione");

        return modalita;
    }

    private void loadSavedData() {
        Bundle savedData = dataManager.getStep3Data();

        // Carica esperienza
        String savedEsperienza = savedData.getString("esperienza", "");
        if (!savedEsperienza.isEmpty()) {
            setSpinnerSelection(spinnerEsperienza, savedEsperienza);
        }

        // Carica modalità selezionate
        ArrayList<String> savedModalita = savedData.getStringArrayList("modalita");
        if (savedModalita != null) {
            for (String modalita : savedModalita) {
                setCheckboxByModalita(modalita, true);
            }
        }

        // Carica dettagli
        String savedDettagli = savedData.getString("dettagli", "");
        etDettagliUtilizzo.setText(savedDettagli);

        updateButtonState();
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        int position = adapter.getPosition(value);
        if (position >= 0) {
            spinner.setSelection(position);
        }
    }

    private void setCheckboxByModalita(String modalita, boolean checked) {
        switch (modalita) {
            case "Desktop": cbDesktop.setChecked(checked); break;
            case "Programmazione": cbProgrammazione.setChecked(checked); break;
            case "Server": cbServer.setChecked(checked); break;
            case "Gaming": cbGaming.setChecked(checked); break;
            case "Sicurezza": cbSicurezza.setChecked(checked); break;
            case "Multimedia": cbMultimedia.setChecked(checked); break;
            case "Ufficio": cbUfficio.setChecked(checked); break;
            case "Educazione": cbEducazione.setChecked(checked); break;
        }
    }

    private void updateButtonState() {
        boolean hasEsperienza = spinnerEsperienza.getSelectedItemPosition() > 0;
        boolean hasModalita = !getModalitaSelezionate().isEmpty();
        boolean hasDettagli = etDettagliUtilizzo.getText().toString().trim().length() >= 10;

        boolean isValid = hasEsperienza && hasModalita && hasDettagli;

        btnAvanti.setEnabled(isValid);

        if (isValid) {
            btnAvanti.setText("Avanti - Selezione Esperti");
            btnAvanti.setAlpha(1.0f);
        } else {
            List<String> missing = new ArrayList<>();
            if (!hasEsperienza) missing.add("esperienza");
            if (!hasModalita) missing.add("modalità utilizzo");
            if (!hasDettagli) missing.add("descrizione dettagliata");

            btnAvanti.setText("Manca: " + String.join(", ", missing));
            btnAvanti.setAlpha(0.5f);
        }
    }

    private void proceedToNextStep() {
        Intent intent = new Intent(this, Step4SelezionaEspertiActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        saveCurrentData();
        super.onBackPressed();
    }
}