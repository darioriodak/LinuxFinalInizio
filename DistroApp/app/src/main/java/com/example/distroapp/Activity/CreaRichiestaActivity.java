package com.example.distroapp.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.distroapp.Entity.Utente;
import com.example.distroapp.Entity.DistroSummaryDTO;
import com.example.distroapp.Entity.EspertoSelezionatoDTO;
import com.example.distroapp.Adapter.DistroSummaryAdapter;
import com.example.distroapp.Adapter.EspertiAdapter;
import com.example.distroapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CreaRichiestaActivity extends AppCompatActivity {

    // ===== COMPONENTI UI =====
    private TextView tvTitoloStep, tvDescrizioneStep, tvStepCounter;
    private View[] progressSteps = new View[4];
    private ScrollView[] stepScrollViews = new ScrollView[4];
    private Button btnIndietro, btnAvanti, btnInvia;

    // Step 1: Selezione Distribuzioni
    private RecyclerView recyclerViewDistribuzioni;
    private ProgressBar progressBarDistribuzioni;
    private TextView tvErroreDistribuzioni;
    private EditText etMotivazioneDistro;
    private DistroSummaryAdapter distroSummaryAdapter;
    private List<DistroSummaryDTO> distribuzioniDisponibili = new ArrayList<>();
    private List<DistroSummaryDTO> distribuzioniSelezionate = new ArrayList<>();

    // Step 2: Hardware
    private EditText etCpu, etStorageSize, etGpuDettagli;
    private Spinner spinnerRam, spinnerStorageType;
    private RadioGroup radioGroupGpu;

    // Step 3: Esperienza
    private RadioGroup radioGroupEsperienza;
    private CheckBox checkDesktop, checkSviluppo, checkServer, checkGaming, checkSicurezza, checkEducativo;
    private EditText etNoteEsperienza;

    // Step 4: Riepilogo
    private TextView tvRiepilogoDistro, tvRiepilogoHardware, tvRiepilogoEsperienza;
    private CheckBox checkConsenso;

    // ===== DATI E STATO =====
    private Utente currentUser;
    private int currentStep = 1;
    private final int TOTAL_STEPS = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea_richiesta);

        // Recupera utente
        currentUser = getIntent().getParcelableExtra("USER_DATA");
        if (currentUser == null) {
            finish();
            return;
        }

        initViews();
        setupStepNavigation();
        setupSpinners();

        // Carica distribuzioni summary
        new LoadDistroSummaryTask(this).execute();

        // Mostra primo step
        showStep(1);
    }

    private void initViews() {
        // Header components
        tvTitoloStep = findViewById(R.id.tvTitoloStep);
        tvDescrizioneStep = findViewById(R.id.tvDescrizioneStep);
        tvStepCounter = findViewById(R.id.tvStepCounter);

        // Progress indicators
        progressSteps[0] = findViewById(R.id.progressStep1);
        progressSteps[1] = findViewById(R.id.progressStep2);
        progressSteps[2] = findViewById(R.id.progressStep3);
        progressSteps[3] = findViewById(R.id.progressStep4);

        // Step containers
        stepScrollViews[0] = findViewById(R.id.scrollStep1);
        stepScrollViews[1] = findViewById(R.id.scrollStep2);
        stepScrollViews[2] = findViewById(R.id.scrollStep3);
        stepScrollViews[3] = findViewById(R.id.scrollStep4);

        // Navigation buttons
        btnIndietro = findViewById(R.id.btnIndietro);
        btnAvanti = findViewById(R.id.btnAvanti);
        btnInvia = findViewById(R.id.btnInvia);

        // Step 1 components
        recyclerViewDistribuzioni = findViewById(R.id.recyclerViewDistribuzioni);
        progressBarDistribuzioni = findViewById(R.id.progressBarDistribuzioni);
        tvErroreDistribuzioni = findViewById(R.id.tvErroreDistribuzioni);
        etMotivazioneDistro = findViewById(R.id.etMotivazioneDistro);

        // Step 2 components
        etCpu = findViewById(R.id.etCpu);
        etStorageSize = findViewById(R.id.etStorageSize);
        etGpuDettagli = findViewById(R.id.etGpuDettagli);
        spinnerRam = findViewById(R.id.spinnerRam);
        spinnerStorageType = findViewById(R.id.spinnerStorageType);
        radioGroupGpu = findViewById(R.id.radioGroupGpu);

        // Step 3 components
        radioGroupEsperienza = findViewById(R.id.radioGroupEsperienza);
        checkDesktop = findViewById(R.id.checkDesktop);
        checkSviluppo = findViewById(R.id.checkSviluppo);
        checkServer = findViewById(R.id.checkServer);
        checkGaming = findViewById(R.id.checkGaming);
        checkSicurezza = findViewById(R.id.checkSicurezza);
        checkEducativo = findViewById(R.id.checkEducativo);
        etNoteEsperienza = findViewById(R.id.etNoteEsperienza);

        // Step 4 components
        tvRiepilogoDistro = findViewById(R.id.tvRiepilogoDistro);
        tvRiepilogoHardware = findViewById(R.id.tvRiepilogoHardware);
        tvRiepilogoEsperienza = findViewById(R.id.tvRiepilogoEsperienza);
        checkConsenso = findViewById(R.id.checkConsenso);
    }

    private void setupStepNavigation() {
        btnAvanti.setOnClickListener(v -> {
            if (validateCurrentStep()) {
                if (currentStep < TOTAL_STEPS) {
                    showStep(currentStep + 1);
                }
            }
        });

        btnIndietro.setOnClickListener(v -> {
            if (currentStep > 1) {
                showStep(currentStep - 1);
            }
        });

        btnInvia.setOnClickListener(v -> {
            if (validateCurrentStep()) {
                inviaRichiesta();
            }
        });
    }

    private void setupSpinners() {
        // Spinner RAM
        String[] ramOptions = {"2GB", "4GB", "8GB", "16GB", "32GB", "64GB", "128GB"};
        ArrayAdapter<String> ramAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ramOptions);
        ramAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRam.setAdapter(ramAdapter);
        spinnerRam.setSelection(2); // Default 8GB

        // Spinner Storage Type
        String[] storageTypes = {"SSD", "HDD", "NVMe SSD", "Ibrido (SSHD)"};
        ArrayAdapter<String> storageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, storageTypes);
        storageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStorageType.setAdapter(storageAdapter);
    }

    // ===== GESTIONE STEP =====
    private void showStep(int stepNumber) {
        currentStep = stepNumber;

        // Nascondi tutti gli step
        for (ScrollView scrollView : stepScrollViews) {
            if (scrollView != null) {
                scrollView.setVisibility(View.GONE);
            }
        }

        // Mostra lo step corrente
        if (stepScrollViews[currentStep - 1] != null) {
            stepScrollViews[currentStep - 1].setVisibility(View.VISIBLE);
        }

        // Aggiorna UI
        updateStepIndicator();
        updateNavigationButtons();
        updateStepContent();
    }

    private void updateStepIndicator() {
        // Aggiorna il contatore step
        tvStepCounter.setText("Step " + currentStep + " di " + TOTAL_STEPS);

        // Aggiorna progress indicator
        for (int i = 0; i < progressSteps.length; i++) {
            if (i < currentStep) {
                progressSteps[i].setAlpha(1.0f);
            } else {
                progressSteps[i].setAlpha(0.3f);
            }
        }

        // Aggiorna titoli
        String[] titoli = {
                "Seleziona Distribuzioni",
                "Specifiche Hardware",
                "Esperienza e Esperti",
                "Riepilogo e Conferma"
        };

        String[] descrizioni = {
                "Quali distribuzioni Linux ti interessano?",
                "Dettagli del tuo sistema (opzionale)",
                "Il tuo livello, uso previsto e selezione esperti",
                "Verifica i dati prima di inviare"
        };

        if (currentStep <= titoli.length) {
            tvTitoloStep.setText(titoli[currentStep - 1]);
            tvDescrizioneStep.setText(descrizioni[currentStep - 1]);
        }
    }

    private void updateNavigationButtons() {
        // Pulsante Indietro
        if (currentStep > 1) {
            btnIndietro.setVisibility(View.VISIBLE);
        } else {
            btnIndietro.setVisibility(View.GONE);
        }

        // Pulsanti Avanti/Invia
        if (currentStep < TOTAL_STEPS) {
            btnAvanti.setVisibility(View.VISIBLE);
            btnInvia.setVisibility(View.GONE);
        } else {
            btnAvanti.setVisibility(View.GONE);
            btnInvia.setVisibility(View.VISIBLE);
        }
    }

    private void updateStepContent() {
        switch (currentStep) {
            case 1:
                // Step distribuzioni - già gestito nel LoadDistroSummaryTask
                break;

            case 2:
                // Step hardware - niente di speciale
                break;

            case 3:
                // Step esperienza - niente di speciale
                break;

            case 4:
                // Step riepilogo
                updateRiepilogoFinale();
                break;
        }
    }

    // ===== VALIDAZIONI =====
    private boolean validateCurrentStep() {
        switch (currentStep) {
            case 1:
                return validateStep1();
            case 2:
                return validateStep2();
            case 3:
                return validateStep3();
            case 4:
                return validateStep4();
            default:
                return true;
        }
    }

    private boolean validateStep1() {
        if (distribuzioniSelezionate.isEmpty()) {
            Toast.makeText(this, "Seleziona almeno una distribuzione", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (distribuzioniSelezionate.size() > 5) {
            Toast.makeText(this, "Massimo 5 distribuzioni selezionabili", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean validateStep2() {
        // Validazioni hardware opzionali
        String storageSize = etStorageSize.getText().toString().trim();
        if (!storageSize.isEmpty()) {
            try {
                int size = Integer.parseInt(storageSize);
                if (size < 1 || size > 10000) {
                    etStorageSize.setError("Dimensione deve essere tra 1 e 10000 GB");
                    return false;
                }
            } catch (NumberFormatException e) {
                etStorageSize.setError("Inserisci un numero valido");
                return false;
            }
        }

        return true;
    }

    private boolean validateStep3() {
        // Controlla che sia selezionato un livello di esperienza
        if (radioGroupEsperienza.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Seleziona il tuo livello di esperienza con Linux", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Controlla che sia selezionato almeno un uso previsto
        if (!checkDesktop.isChecked() && !checkSviluppo.isChecked() &&
                !checkServer.isChecked() && !checkGaming.isChecked() &&
                !checkSicurezza.isChecked() && !checkEducativo.isChecked()) {
            Toast.makeText(this, "Seleziona almeno un uso previsto per Linux", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean validateStep4() {
        // Controlla che sia accettato il consenso
        if (!checkConsenso.isChecked()) {
            Toast.makeText(this, "Devi accettare la condivisione delle informazioni per procedere", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // ===== AGGIORNAMENTO RIEPILOGO =====
    private void updateRiepilogoFinale() {
        // Distribuzioni
        StringBuilder distroText = new StringBuilder();
        for (int i = 0; i < distribuzioniSelezionate.size(); i++) {
            if (i > 0) distroText.append(", ");
            distroText.append(distribuzioniSelezionate.get(i).getNomeDisplay());
        }
        tvRiepilogoDistro.setText(distroText.toString());

        // Hardware
        StringBuilder hwText = new StringBuilder();
        String cpu = etCpu.getText().toString().trim();
        if (!cpu.isEmpty()) {
            hwText.append("CPU: ").append(cpu).append("\n");
        }

        String ram = spinnerRam.getSelectedItem().toString();
        hwText.append("RAM: ").append(ram).append("\n");

        String storageSize = etStorageSize.getText().toString().trim();
        String storageType = spinnerStorageType.getSelectedItem().toString();
        if (!storageSize.isEmpty()) {
            hwText.append("Storage: ").append(storageType).append(" ").append(storageSize).append("GB\n");
        }

        int selectedGpuId = radioGroupGpu.getCheckedRadioButtonId();
        if (selectedGpuId != -1) {
            RadioButton selectedGpu = findViewById(selectedGpuId);
            hwText.append("GPU: ").append(selectedGpu.getText());

            String gpuDetails = etGpuDettagli.getText().toString().trim();
            if (!gpuDetails.isEmpty()) {
                hwText.append(" (").append(gpuDetails).append(")");
            }
        }

        tvRiepilogoHardware.setText(hwText.toString());

        // Esperienza
        StringBuilder expText = new StringBuilder();
        int selectedExpId = radioGroupEsperienza.getCheckedRadioButtonId();
        if (selectedExpId != -1) {
            RadioButton selectedExp = findViewById(selectedExpId);
            String expLevel = selectedExp.getText().toString();
            // Rimuovi emoji per il riepilogo
            expLevel = expLevel.replaceAll("[🌱🔧⚡🎯]", "").trim();
            expText.append("Livello: ").append(expLevel).append("\n");
        }

        // Usi previsti
        List<String> usi = new ArrayList<>();
        if (checkDesktop.isChecked()) usi.add("Desktop");
        if (checkSviluppo.isChecked()) usi.add("Sviluppo");
        if (checkServer.isChecked()) usi.add("Server");
        if (checkGaming.isChecked()) usi.add("Gaming");
        if (checkSicurezza.isChecked()) usi.add("Sicurezza");
        if (checkEducativo.isChecked()) usi.add("Educativo");

        if (!usi.isEmpty()) {
            expText.append("Uso: ");
            for (int i = 0; i < usi.size(); i++) {
                if (i > 0) expText.append(", ");
                expText.append(usi.get(i));
            }
        }

        tvRiepilogoEsperienza.setText(expText.toString());
    }

    // ===== INVIO RICHIESTA =====
    private void inviaRichiesta() {
        try {
            JSONObject richiestaJson = creaJSONRichiesta();
            new InviaRichiestaTask(this).execute(richiestaJson.toString());
        } catch (JSONException e) {
            Toast.makeText(this, "Errore nella preparazione dati", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private JSONObject creaJSONRichiesta() throws JSONException {
        JSONObject json = new JSONObject();

        // Dati base dal profilo utente
        json.put("livelloEsperienza", currentUser.getLivelloEsperienza());
        json.put("scopoUso", currentUser.isEsperto() ? "Consulenza tecnica" : currentUser.getLivelloEsperienza());

        // Modalità selezione (sempre automatica per ora)
        json.put("modalitaSelezione", "AUTOMATICA");

        // Distribuzioni candidate (da DistroSummary)
        JSONArray distribuzioniArray = new JSONArray();
        for (DistroSummaryDTO distro : distribuzioniSelezionate) {
            JSONObject distroJson = new JSONObject();
            distroJson.put("id", distro.getIdDistribuzione()); // Usa idDistribuzione dal summary
            distroJson.put("nome", distro.getNomeDisplay());
            distribuzioniArray.put(distroJson);
        }
        json.put("distribuzioniCandidate", distribuzioniArray);

        // Esperti (sempre array vuoto per modalità automatica)
        json.put("espertiSelezionati", new JSONArray());

        // Hardware opzionale
        String cpu = etCpu.getText().toString().trim();
        if (!cpu.isEmpty()) json.put("cpu", cpu);

        String ram = spinnerRam.getSelectedItem().toString();
        json.put("ram", ram);

        String storageSize = etStorageSize.getText().toString().trim();
        if (!storageSize.isEmpty()) {
            json.put("spazioArchiviazione", storageSize + " GB");
            json.put("tipoStorage", spinnerStorageType.getSelectedItem().toString());
        }

        int selectedGpuId = radioGroupGpu.getCheckedRadioButtonId();
        if (selectedGpuId != -1) {
            RadioButton selectedGpu = findViewById(selectedGpuId);
            json.put("gpu", selectedGpu.getText().toString());

            String gpuDetails = etGpuDettagli.getText().toString().trim();
            if (!gpuDetails.isEmpty()) {
                json.put("gpuDettagli", gpuDetails);
            }
        }

        // Esperienza Linux
        int selectedExpId = radioGroupEsperienza.getCheckedRadioButtonId();
        if (selectedExpId != -1) {
            RadioButton selectedExp = findViewById(selectedExpId);
            String expLevel = selectedExp.getText().toString();
            expLevel = expLevel.replaceAll("[🌱🔧⚡🎯]", "").trim();
            json.put("livelloEsperienzaLinux", expLevel);
        }

        // Usi previsti
        JSONArray usiArray = new JSONArray();
        if (checkDesktop.isChecked()) usiArray.put("Desktop uso quotidiano");
        if (checkSviluppo.isChecked()) usiArray.put("Sviluppo software");
        if (checkServer.isChecked()) usiArray.put("Server / Hosting");
        if (checkGaming.isChecked()) usiArray.put("Gaming");
        if (checkSicurezza.isChecked()) usiArray.put("Sicurezza / Penetration testing");
        if (checkEducativo.isChecked()) usiArray.put("Scopi educativi");
        json.put("usiPrevisti", usiArray);

        // Motivazione e note
        String motivazione = etMotivazioneDistro.getText().toString().trim();
        if (!motivazione.isEmpty()) {
            json.put("motivazioneDistro", motivazione);
        }

        String note = etNoteEsperienza.getText().toString().trim();
        if (!note.isEmpty()) {
            json.put("noteAggiuntive", note);
        }

        return json;
    }

    // ===== NETWORK TASKS =====
    private static class LoadDistroSummaryTask extends AsyncTask<Void, Void, String> {
        private WeakReference<CreaRichiestaActivity> activityRef;

        LoadDistroSummaryTask(CreaRichiestaActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            CreaRichiestaActivity activity = activityRef.get();
            if (activity != null) {
                activity.progressBarDistribuzioni.setVisibility(View.VISIBLE);
                activity.recyclerViewDistribuzioni.setVisibility(View.GONE);
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://10.0.2.2:8080/LinuxFinal/DatiSupportoServlet/distribuzioni-summary");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return readStream(connection.getInputStream());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            CreaRichiestaActivity activity = activityRef.get();
            if (activity == null) return;

            // Nascondi loading
            activity.progressBarDistribuzioni.setVisibility(View.GONE);

            if (result != null) {
                try {
                    JSONObject response = new JSONObject(result);
                    if (response.getBoolean("success")) {
                        JSONArray distribuzioni = response.getJSONArray("distribuzioni");

                        activity.distribuzioniDisponibili.clear();
                        for (int i = 0; i < distribuzioni.length(); i++) {
                            JSONObject distroJson = distribuzioni.getJSONObject(i);

                            DistroSummaryDTO distro = new DistroSummaryDTO();
                            distro.setId(distroJson.getInt("id"));
                            distro.setIdDistribuzione(distroJson.getInt("idDistribuzione"));
                            distro.setNomeDisplay(distroJson.getString("nomeDisplay"));
                            distro.setDescrizioneBreve(distroJson.getString("descrizioneBreve"));
                            distro.setDescrizioneDettaglio(distroJson.optString("descrizioneDettaglio"));
                            distro.setIcona(distroJson.optString("icona"));
                            distro.setColoreHex(distroJson.optString("coloreHex"));
                            distro.setLivelloDifficolta(distroJson.optString("livelloDifficolta"));
                            distro.setPunteggioPopolarita(distroJson.optInt("punteggioPopolarita"));

                            activity.distribuzioniDisponibili.add(distro);
                        }

                        // Setup RecyclerView con layout a griglia per le card
                        activity.setupDistroSummaryRecyclerView();
                        activity.recyclerViewDistribuzioni.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, "Errore nel caricamento distribuzioni", Toast.LENGTH_SHORT).show();
                }
            } else {
                activity.tvErroreDistribuzioni.setVisibility(View.VISIBLE);
                activity.tvErroreDistribuzioni.setText("Impossibile caricare le distribuzioni");
            }
        }

        private String readStream(InputStream inputStream) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
            return result.toString();
        }
    }

    private void setupDistroSummaryRecyclerView() {
        distroSummaryAdapter = new DistroSummaryAdapter(distribuzioniDisponibili, distribuzioni -> {
            distribuzioniSelezionate.clear();
            distribuzioniSelezionate.addAll(distribuzioni);

            // Aggiorna UI se necessario
            Toast.makeText(this, distribuzioniSelezionate.size() + " distribuzioni selezionate", Toast.LENGTH_SHORT).show();
        });

        // Layout a griglia per mostrare le card delle distribuzioni
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerViewDistribuzioni.setLayoutManager(gridLayoutManager);
        recyclerViewDistribuzioni.setAdapter(distroSummaryAdapter);
    }

    private static class InviaRichiestaTask extends AsyncTask<String, Void, String> {
        private WeakReference<CreaRichiestaActivity> activityRef;

        InviaRichiestaTask(CreaRichiestaActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            CreaRichiestaActivity activity = activityRef.get();
            if (activity != null) {
                // Disabilita il pulsante di invio e mostra loading
                activity.btnInvia.setEnabled(false);
                activity.btnInvia.setText("Invio in corso...");
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonRichiesta = params[0];

            try {
                URL url = new URL("http://10.0.2.2:8080/LinuxFinal/RichiesteServlet/crea");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);

                // Scrivi il JSON nella richiesta
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonRichiesta.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    return readStream(connection.getInputStream());
                } else {
                    return "ERROR:" + responseCode;
                }

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            CreaRichiestaActivity activity = activityRef.get();
            if (activity == null) return;

            // Riabilita il pulsante
            activity.btnInvia.setEnabled(true);
            activity.btnInvia.setText("Invia Richiesta");

            if (result != null && !result.startsWith("ERROR:")) {
                try {
                    JSONObject response = new JSONObject(result);
                    if (response.getBoolean("success")) {
                        // Richiesta inviata con successo
                        Toast.makeText(activity, "Richiesta inviata con successo!", Toast.LENGTH_LONG).show();

                        // Torna al dashboard
                        Intent intent = new Intent(activity, DashboardActivity.class);
                        intent.putExtra("USER_DATA", activity.currentUser);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(intent);
                        activity.finish();

                    } else {
                        String errore = response.optString("message", "Errore sconosciuto");
                        Toast.makeText(activity, "Errore: " + errore, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, "Errore nel parsing della risposta", Toast.LENGTH_SHORT).show();
                }
            } else if (result != null && result.startsWith("ERROR:")) {
                String errorCode = result.substring(6);
                Toast.makeText(activity, "Errore del server: " + errorCode, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Errore di connessione", Toast.LENGTH_SHORT).show();
            }
        }

        private String readStream(InputStream inputStream) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
            return result.toString();
        }
    }
}