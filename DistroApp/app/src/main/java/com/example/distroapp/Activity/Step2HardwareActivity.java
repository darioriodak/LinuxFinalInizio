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
    private EditText etCpu, etStorageSize, etGpuDettagli;
    private Spinner spinnerRam, spinnerStorageType;
    private RadioGroup radioGroupGpu;
    private RadioButton rbGpuIntegrata, rbGpuDedicata, rbGpuNessuna;

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
        etStorageSize = findViewById(R.id.et_storage_size);
        etGpuDettagli = findViewById(R.id.et_gpu_dettagli);

        spinnerRam = findViewById(R.id.spinner_ram);
        spinnerStorageType = findViewById(R.id.spinner_storage_type);

        radioGroupGpu = findViewById(R.id.radio_group_gpu);
        rbGpuIntegrata = findViewById(R.id.rb_gpu_integrata);
        rbGpuDedicata = findViewById(R.id.rb_gpu_dedicata);
        rbGpuNessuna = findViewById(R.id.rb_gpu_nessuna);

        btnIndietro = findViewById(R.id.btn_indietro);
        btnAvanti = findViewById(R.id.btn_avanti);
    }

    private void setupProgressIndicator() {
        tvTitolo.setText("Specifiche Hardware");
        tvDescrizione.setText("Inserisci le specifiche del tuo hardware attuale o desiderato");
        tvStepCounter.setText("Step 2 di 5");

        // Evidenzia step corrente
        progressStep1.setBackgroundResource(R.drawable.step_completed);
        progressStep2.setBackgroundResource(R.drawable.step_active);
        progressStep3.setBackgroundResource(R.drawable.step_inactive);
        progressStep4.setBackgroundResource(R.drawable.step_inactive);
        progressStep5.setBackgroundResource(R.drawable.step_inactive);
    }

    private void setupSpinners() {
        // Setup RAM Spinner
        String[] ramOptions = {
                "Seleziona RAM...",
                "2 GB", "4 GB", "8 GB", "16 GB", "32 GB", "64 GB", "Più di 64 GB"
        };
        ArrayAdapter<String> ramAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, ramOptions);
        ramAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRam.setAdapter(ramAdapter);

        // Setup Storage Type Spinner
        String[] storageOptions = {
                "Seleziona tipo storage...",
                "HDD (Hard Disk Drive)",
                "SSD (Solid State Drive)",
                "NVMe SSD",
                "eMMC",
                "HDD + SSD (Ibrido)"
        };
        ArrayAdapter<String> storageAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, storageOptions);
        storageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStorageType.setAdapter(storageAdapter);
    }

    private void setupRadioGroups() {
        radioGroupGpu.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_gpu_dedicata) {
                etGpuDettagli.setVisibility(View.VISIBLE);
                etGpuDettagli.setHint("Specifica marca e modello GPU (es: NVIDIA GTX 1060)");
            } else if (checkedId == R.id.rb_gpu_integrata) {
                etGpuDettagli.setVisibility(View.VISIBLE);
                etGpuDettagli.setHint("Specifica GPU integrata (es: Intel UHD Graphics)");
            } else {
                etGpuDettagli.setVisibility(View.GONE);
                etGpuDettagli.setText("");
            }

            saveCurrentData();
        });
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
        etCpu.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) saveCurrentData();
        });

        etStorageSize.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) saveCurrentData();
        });

        etGpuDettagli.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) saveCurrentData();
        });

        // Auto-save sui spinner
        spinnerRam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) saveCurrentData(); // Evita il salvataggio del placeholder
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerStorageType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) saveCurrentData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private boolean validateStep() {
        boolean isValid = true;
        StringBuilder errors = new StringBuilder();

        // Validazione CPU
        if (TextUtils.isEmpty(etCpu.getText().toString().trim())) {
            errors.append("• Inserisci le specifiche del processore\n");
            etCpu.setError("Campo obbligatorio");
            isValid = false;
        }

        // Validazione RAM
        if (spinnerRam.getSelectedItemPosition() == 0) {
            errors.append("• Seleziona la quantità di RAM\n");
            isValid = false;
        }

        // Validazione Storage Size
        String storageSize = etStorageSize.getText().toString().trim();
        if (TextUtils.isEmpty(storageSize)) {
            errors.append("• Inserisci la dimensione dello storage\n");
            etStorageSize.setError("Campo obbligatorio");
            isValid = false;
        }

        // Validazione Storage Type
        if (spinnerStorageType.getSelectedItemPosition() == 0) {
            errors.append("• Seleziona il tipo di storage\n");
            isValid = false;
        }

        // Validazione GPU
        if (radioGroupGpu.getCheckedRadioButtonId() == -1) {
            errors.append("• Seleziona il tipo di scheda grafica\n");
            isValid = false;
        } else {
            int selectedGpu = radioGroupGpu.getCheckedRadioButtonId();
            if ((selectedGpu == R.id.rb_gpu_dedicata || selectedGpu == R.id.rb_gpu_integrata) &&
                    TextUtils.isEmpty(etGpuDettagli.getText().toString().trim())) {
                errors.append("• Specifica i dettagli della scheda grafica\n");
                etGpuDettagli.setError("Campo obbligatorio per questo tipo di GPU");
                isValid = false;
            }
        }

        if (!isValid) {
            Toast.makeText(this, "Completa tutti i campi obbligatori:\n" + errors.toString(),
                    Toast.LENGTH_LONG).show();
        }

        return isValid;
    }

    private void saveCurrentData() {
        String cpu = etCpu.getText().toString().trim();
        String ram = spinnerRam.getSelectedItemPosition() > 0 ?
                spinnerRam.getSelectedItem().toString() : "";
        String storageSize = etStorageSize.getText().toString().trim();
        String storageType = spinnerStorageType.getSelectedItemPosition() > 0 ?
                spinnerStorageType.getSelectedItem().toString() : "";

        String gpuType = "";
        String gpuDetails = etGpuDettagli.getText().toString().trim();

        int checkedGpuId = radioGroupGpu.getCheckedRadioButtonId();
        if (checkedGpuId == R.id.rb_gpu_integrata) {
            gpuType = "Integrata";
        } else if (checkedGpuId == R.id.rb_gpu_dedicata) {
            gpuType = "Dedicata";
        } else if (checkedGpuId == R.id.rb_gpu_nessuna) {
            gpuType = "Nessuna";
            gpuDetails = ""; // Reset dettagli se "Nessuna"
        }

        dataManager.saveStep2Data(cpu, ram, storageSize, storageType, gpuType, gpuDetails);
    }

    private void loadSavedData() {
        Bundle savedData = dataManager.getStep2Data();

        // Carica CPU
        etCpu.setText(savedData.getString("cpu", ""));

        // Carica RAM
        String savedRam = savedData.getString("ram", "");
        if (!savedRam.isEmpty()) {
            ArrayAdapter<String> ramAdapter = (ArrayAdapter<String>) spinnerRam.getAdapter();
            int ramPosition = ramAdapter.getPosition(savedRam);
            if (ramPosition >= 0) {
                spinnerRam.setSelection(ramPosition);
            }
        }

        // Carica Storage
        etStorageSize.setText(savedData.getString("storageSize", ""));
        String savedStorageType = savedData.getString("storageType", "");
        if (!savedStorageType.isEmpty()) {
            ArrayAdapter<String> storageAdapter = (ArrayAdapter<String>) spinnerStorageType.getAdapter();
            int storagePosition = storageAdapter.getPosition(savedStorageType);
            if (storagePosition >= 0) {
                spinnerStorageType.setSelection(storagePosition);
            }
        }

        // Carica GPU
        String savedGpuType = savedData.getString("gpuType", "");
        switch (savedGpuType) {
            case "Integrata":
                rbGpuIntegrata.setChecked(true);
                etGpuDettagli.setVisibility(View.VISIBLE);
                etGpuDettagli.setHint("Specifica GPU integrata (es: Intel UHD Graphics)");
                break;
            case "Dedicata":
                rbGpuDedicata.setChecked(true);
                etGpuDettagli.setVisibility(View.VISIBLE);
                etGpuDettagli.setHint("Specifica marca e modello GPU (es: NVIDIA GTX 1060)");
                break;
            case "Nessuna":
                rbGpuNessuna.setChecked(true);
                etGpuDettagli.setVisibility(View.GONE);
                break;
        }

        etGpuDettagli.setText(savedData.getString("gpuDetails", ""));
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