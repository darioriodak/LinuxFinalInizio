package com.example.distroapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.distroapp.R;
import com.example.distroapp.Util.WizardDataManager;

public class Step2HardwareActivity extends AppCompatActivity {

    // UI Components
    private TextView tvTitolo, tvDescrizione, tvStepCounter;
    private View progressStep1, progressStep2, progressStep3, progressStep4, progressStep5;

    // Hardware Form Components
    private EditText etCpu, etSchedaVideo;
    private Spinner spinnerRam, spinnerSpazioArchiviazione, spinnerTipoSistema;
    private RadioGroup radioGroupHardwareRequired;
    private RadioButton rbHardwareObbligatorio, rbHardwareOpzionale;

    private Button btnIndietro, btnAvanti;

    // Data Manager
    private WizardDataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step2_hardware);

        dataManager = new WizardDataManager(this);

        initViews();
        setupProgressIndicator();
        setupSpinners();
        setupRadioGroups();
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

        // Hardware form
        etCpu = findViewById(R.id.et_cpu);
        etSchedaVideo = findViewById(R.id.et_scheda_video);

        spinnerRam = findViewById(R.id.spinner_ram);
        spinnerSpazioArchiviazione = findViewById(R.id.spinner_spazio_archiviazione);
        spinnerTipoSistema = findViewById(R.id.spinner_tipo_sistema);

        radioGroupHardwareRequired = findViewById(R.id.radio_group_hardware_required);
        rbHardwareObbligatorio = findViewById(R.id.rb_hardware_obbligatorio);
        rbHardwareOpzionale = findViewById(R.id.rb_hardware_opzionale);

        btnIndietro = findViewById(R.id.btn_indietro);
        btnAvanti = findViewById(R.id.btn_avanti);
    }

    private void setupProgressIndicator() {
        tvTitolo.setText("Specifiche Hardware");
        tvDescrizione.setText("Inserisci le specifiche del tuo hardware (opzionale ma consigliato)");
        tvStepCounter.setText("Step 2 di 5");

        // Evidenzia step corrente
        progressStep1.setBackgroundResource(R.drawable.step_completed);
        progressStep2.setBackgroundResource(R.drawable.step_active);
        progressStep3.setBackgroundResource(R.drawable.step_inactive);
        progressStep4.setBackgroundResource(R.drawable.step_inactive);
        progressStep5.setBackgroundResource(R.drawable.step_inactive);
    }

    private void setupSpinners() {
        // Setup RAM Spinner - compatibile con server (stringa)
        String[] ramOptions = {
                "Non specificato",
                "2 GB o meno",
                "4 GB",
                "8 GB",
                "16 GB",
                "32 GB",
                "64 GB",
                "Più di 64 GB"
        };
        ArrayAdapter<String> ramAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, ramOptions);
        ramAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRam.setAdapter(ramAdapter);

        // Setup Spazio Archiviazione Spinner
        String[] spazioOptions = {
                "Non specificato",
                "128 GB o meno",
                "256 GB",
                "512 GB",
                "1 TB",
                "2 TB",
                "4 TB",
                "Più di 4 TB"
        };
        ArrayAdapter<String> spazioAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, spazioOptions);
        spazioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSpazioArchiviazione.setAdapter(spazioAdapter);

        // Setup Tipo Sistema Spinner - compatibile con TipoSistema enum
        String[] tipoSistemaOptions = {
                "Non specificato",
                "DESKTOP",
                "LAPTOP",
                "SERVER",
                "WORKSTATION",
                "MINI_PC",
                "ALL_IN_ONE"
        };
        ArrayAdapter<String> tipoSistemaAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, tipoSistemaOptions);
        tipoSistemaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoSistema.setAdapter(tipoSistemaAdapter);

        // Auto-save sui spinner
        AdapterView.OnItemSelectedListener autoSaveListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                saveCurrentData();
                updateButtonState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerRam.setOnItemSelectedListener(autoSaveListener);
        spinnerSpazioArchiviazione.setOnItemSelectedListener(autoSaveListener);
        spinnerTipoSistema.setOnItemSelectedListener(autoSaveListener);
    }

    private void setupRadioGroups() {
        // Setup radio group per decidere se l'hardware è obbligatorio
        radioGroupHardwareRequired.setOnCheckedChangeListener((group, checkedId) -> {
            toggleHardwareFields(checkedId == R.id.rb_hardware_obbligatorio);
            saveCurrentData();
            updateButtonState();
        });

        // Default: hardware opzionale
        rbHardwareOpzionale.setChecked(true);
        toggleHardwareFields(false);
    }

    private void toggleHardwareFields(boolean required) {
        // Mostra/nasconde campi hardware basandosi sulla selezione
        View[] hardwareFields = {etCpu, etSchedaVideo, spinnerRam,
                spinnerSpazioArchiviazione, spinnerTipoSistema};

        for (View field : hardwareFields) {
            field.setVisibility(required ? View.VISIBLE : View.VISIBLE); // Sempre visibili per ora
            // Potremmo nasconderli se l'utente sceglie "salta questo step"
        }

        // Aggiorna descrizione
        if (required) {
            tvDescrizione.setText("Inserisci le specifiche del tuo hardware attuale");
        } else {
            tvDescrizione.setText("Hardware opzionale - puoi saltare se non conosci le specifiche");
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
        View.OnFocusChangeListener autoSaveFocusListener = (v, hasFocus) -> {
            if (!hasFocus) {
                saveCurrentData();
                updateButtonState();
            }
        };

        etCpu.setOnFocusChangeListener(autoSaveFocusListener);
        etSchedaVideo.setOnFocusChangeListener(autoSaveFocusListener);

        updateButtonState();
    }

    private boolean validateStep() {
        // Se hardware è obbligatorio, valida i campi
        if (rbHardwareObbligatorio.isChecked()) {
            StringBuilder errors = new StringBuilder();
            boolean isValid = true;

            // Validazione CPU se obbligatorio
            if (TextUtils.isEmpty(etCpu.getText().toString().trim())) {
                errors.append("• Inserisci le specifiche del processore\n");
                etCpu.setError("Campo obbligatorio");
                isValid = false;
            }

            if (!isValid) {
                Toast.makeText(this, "Completa i campi obbligatori:\n" + errors.toString(),
                        Toast.LENGTH_LONG).show();
                return false;
            }
        }

        return true; // Hardware opzionale = sempre valido
    }

    private void saveCurrentData() {
        // Raccogli dati solo se alcuni campi sono compilati o hardware obbligatorio
        boolean isHardwareRequired = rbHardwareObbligatorio.isChecked();
        boolean hasHardwareData = hasAnyHardwareData();

        if (isHardwareRequired || hasHardwareData) {
            String cpu = etCpu.getText().toString().trim();
            String ram = getSpinnerValueOrNull(spinnerRam);
            String spazioArchiviazione = getSpinnerValueOrNull(spinnerSpazioArchiviazione);
            String schedaVideo = etSchedaVideo.getText().toString().trim();
            String tipoSistema = getSpinnerValueOrNull(spinnerTipoSistema);

            dataManager.saveStep2Data(cpu, ram, spazioArchiviazione, schedaVideo, tipoSistema, String.valueOf(isHardwareRequired));
        } else {
            // Pulisci dati se non necessari
            dataManager.clearStep2Data();
        }
    }

    private boolean hasAnyHardwareData() {
        return !TextUtils.isEmpty(etCpu.getText().toString().trim()) ||
                !TextUtils.isEmpty(etSchedaVideo.getText().toString().trim()) ||
                spinnerRam.getSelectedItemPosition() > 0 ||
                spinnerSpazioArchiviazione.getSelectedItemPosition() > 0 ||
                spinnerTipoSistema.getSelectedItemPosition() > 0;
    }

    private String getSpinnerValueOrNull(Spinner spinner) {
        if (spinner.getSelectedItemPosition() > 0) {
            return spinner.getSelectedItem().toString();
        }
        return null;
    }

    private void loadSavedData() {
        Bundle savedData = dataManager.getStep2Data();

        // Carica CPU
        etCpu.setText(savedData.getString("cpu", ""));

        // Carica RAM
        String savedRam = savedData.getString("ram", "");
        setSpinnerSelection(spinnerRam, savedRam);

        // Carica Spazio Archiviazione
        String savedSpazio = savedData.getString("spazioArchiviazione", "");
        setSpinnerSelection(spinnerSpazioArchiviazione, savedSpazio);

        // Carica Scheda Video
        etSchedaVideo.setText(savedData.getString("schedaVideo", ""));

        // Carica Tipo Sistema
        String savedTipoSistema = savedData.getString("tipoSistema", "");
        setSpinnerSelection(spinnerTipoSistema, savedTipoSistema);

        // Carica se hardware è obbligatorio
        boolean isRequired = savedData.getBoolean("isRequired", false);
        if (isRequired) {
            rbHardwareObbligatorio.setChecked(true);
        } else {
            rbHardwareOpzionale.setChecked(true);
        }
        toggleHardwareFields(isRequired);

        updateButtonState();
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value != null && !value.isEmpty()) {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
            int position = adapter.getPosition(value);
            if (position >= 0) {
                spinner.setSelection(position);
            }
        }
    }

    private void updateButtonState() {
        // Il bottone è sempre attivo perché l'hardware è opzionale
        boolean isValid = true;

        // Se hardware obbligatorio, verifica che almeno CPU sia inserito
        if (rbHardwareObbligatorio.isChecked()) {
            isValid = !TextUtils.isEmpty(etCpu.getText().toString().trim());
        }

        btnAvanti.setEnabled(isValid);

        if (isValid) {
            btnAvanti.setText("Avanti - Esperienza");
            btnAvanti.setAlpha(1.0f);
        } else {
            btnAvanti.setText("Completa i campi obbligatori");
            btnAvanti.setAlpha(0.5f);
        }
    }

    private void proceedToNextStep() {
        Intent intent = new Intent(this, Step3EsperienzaActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        saveCurrentData();
        super.onBackPressed();
    }
}