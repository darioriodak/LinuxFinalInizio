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
    private ViewFlipper viewFlipper;
    private Button btnNext, btnPrev, btnFinish;
    private TextView tvStepIndicator, tvStepTitle;
    private ProgressBar progressBar;

    // Step 1: Selezione Distribuzioni
    private TextView tvStepDescription;
    private RecyclerView recyclerDistroSummary;
    private DistroSummaryAdapter distroSummaryAdapter;
    private List<DistroSummaryDTO> distribuzioniDisponibili = new ArrayList<>();
    private List<DistroSummaryDTO> distribuzioniSelezionate = new ArrayList<>();

    // Step 2: Hardware Opzionale + Note
    private CheckBox cbAggiungiHardware;
    private LinearLayout layoutHardware;
    private EditText etCpu, etRam, etSpazio, etSchedaVideo, etNoteAggiuntive;
    private Spinner spinnerTipoSistema;

    // Step 3: Selezione Esperti + Conferma
    private RadioGroup radioGroupModalita;
    private LinearLayout layoutEspertiManuali;
    private RecyclerView recyclerEsperti;
    private EspertiAdapter espertiAdapter;
    private List<EspertoSelezionatoDTO> espertiDisponibili = new ArrayList<>();
    private List<EspertoSelezionatoDTO> espertiSelezionati = new ArrayList<>();
    private TextView tvRiepilogoFinale;

    // ===== DATI E STATO =====
    private Utente currentUser;
    private int currentStep = 0;
    private final int TOTAL_STEPS = 3;

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
        setupViewFlipper();
        setupClickListeners();
        setupStep1();

        // Carica distribuzioni summary
        new LoadDistroSummaryTask(this).execute();
    }

    private void initViews() {
        // Controlli navigazione
        viewFlipper = findViewById(R.id.viewFlipper);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);
        btnFinish = findViewById(R.id.btnFinish);
        tvStepIndicator = findViewById(R.id.tvStepIndicator);
        tvStepTitle = findViewById(R.id.tvStepTitle);
        progressBar = findViewById(R.id.progressBar);

        // Step 1 - Distribuzioni Summary
        tvStepDescription = findViewById(R.id.tvStepDescription);
        recyclerDistroSummary = findViewById(R.id.recyclerDistroSummary);

        // Step 2 - Hardware
        cbAggiungiHardware = findViewById(R.id.cbAggiungiHardware);
        layoutHardware = findViewById(R.id.layoutHardware);
        etCpu = findViewById(R.id.etCpu);
        etRam = findViewById(R.id.etRam);
        etSpazio = findViewById(R.id.etSpazio);
        etSchedaVideo = findViewById(R.id.etSchedaVideo);
        spinnerTipoSistema = findViewById(R.id.spinnerTipoSistema);
        etNoteAggiuntive = findViewById(R.id.etNoteAggiuntive);

        // Step 3 - Esperti
        radioGroupModalita = findViewById(R.id.radioGroupModalita);
        layoutEspertiManuali = findViewById(R.id.layoutEspertiManuali);
        recyclerEsperti = findViewById(R.id.recyclerEsperti);
        tvRiepilogoFinale = findViewById(R.id.tvRiepilogoFinale);
    }

    private void setupViewFlipper() {
        updateStepIndicator();
        updateNavigationButtons();
    }

    private void setupClickListeners() {
        btnNext.setOnClickListener(v -> {
            if (validateCurrentStep()) {
                nextStep();
            }
        });

        btnPrev.setOnClickListener(v -> previousStep());

        btnFinish.setOnClickListener(v -> {
            if (validateCurrentStep()) {
                inviaRichiesta();
            }
        });

        // Checkbox hardware
        cbAggiungiHardware.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutHardware.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        // Gestione modalità selezione esperti
        radioGroupModalita.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioManuale) {
                layoutEspertiManuali.setVisibility(View.VISIBLE);
                if (espertiDisponibili.isEmpty()) {
                    new LoadEspertiTask(this).execute();
                }
            } else {
                layoutEspertiManuali.setVisibility(View.GONE);
            }
        });
    }

    private void setupStep1() {
        tvStepTitle.setText("Scegli le Distribuzioni");
        tvStepDescription.setText("Seleziona una o più distribuzioni Linux che ti interessano");
    }

    // ===== NAVIGAZIONE STEPS =====

    private void nextStep() {
        if (currentStep < TOTAL_STEPS - 1) {
            currentStep++;
            viewFlipper.setDisplayedChild(currentStep);
            updateStepIndicator();
            updateNavigationButtons();

            // Setup specifico per ogni step
            switch (currentStep) {
                case 1:
                    setupStep2();
                    break;
                case 2:
                    setupStep3();
                    break;
            }
        }
    }

    private void previousStep() {
        if (currentStep > 0) {
            currentStep--;
            viewFlipper.setDisplayedChild(currentStep);
            updateStepIndicator();
            updateNavigationButtons();
        }
    }

    private void updateStepIndicator() {
        String[] stepTitles = {
                "Distribuzioni",
                "Hardware & Note",
                "Esperti & Conferma"
        };

        tvStepIndicator.setText("Step " + (currentStep + 1) + " di " + TOTAL_STEPS);
        tvStepTitle.setText(stepTitles[currentStep]);
        progressBar.setProgress((int) (((float) (currentStep + 1) / TOTAL_STEPS) * 100));
    }

    private void updateNavigationButtons() {
        btnPrev.setVisibility(currentStep > 0 ? View.VISIBLE : View.GONE);

        if (currentStep == TOTAL_STEPS - 1) {
            btnNext.setVisibility(View.GONE);
            btnFinish.setVisibility(View.VISIBLE);
        } else {
            btnNext.setVisibility(View.VISIBLE);
            btnFinish.setVisibility(View.GONE);
        }
    }

    // ===== SETUP STEPS SPECIFICI =====

    private void setupStep2() {
        tvStepDescription.setText("Aggiungi informazioni sul tuo hardware per consigli più personalizzati (opzionale)");

        // Pre-compila alcuni campi se possibile
        // Qui potresti usare dati salvati o rilevamento automatico
    }

    private void setupStep3() {
        tvStepDescription.setText("Scegli come selezionare gli esperti che valuteranno la tua richiesta");

        // Setup RecyclerView esperti se modalità manuale
        if (recyclerEsperti.getAdapter() == null && !espertiDisponibili.isEmpty()) {
            espertiAdapter = new EspertiAdapter(espertiDisponibili, esperti -> {
                espertiSelezionati.clear();
                espertiSelezionati.addAll(esperti);
                updateRiepilogoFinale();
            });
            recyclerEsperti.setLayoutManager(new LinearLayoutManager(this));
            recyclerEsperti.setAdapter(espertiAdapter);
        }

        updateRiepilogoFinale();
    }

    private void updateRiepilogoFinale() {
        StringBuilder riepilogo = new StringBuilder();
        riepilogo.append("🎯 RIEPILOGO RICHIESTA\n\n");

        // Distribuzioni selezionate
        riepilogo.append("💻 Distribuzioni selezionate (").append(distribuzioniSelezionate.size()).append("):\n");
        for (DistroSummaryDTO distro : distribuzioniSelezionate) {
            riepilogo.append("• ").append(distro.getNomeDisplay());
            if (distro.getLivelloDifficolta() != null) {
                riepilogo.append(" (").append(distro.getLivelloDifficolta()).append(")");
            }
            riepilogo.append("\n");
        }
        riepilogo.append("\n");

        // Hardware
        if (cbAggiungiHardware.isChecked()) {
            riepilogo.append("🔧 Hardware specificato:\n");
            if (!etCpu.getText().toString().trim().isEmpty()) {
                riepilogo.append("• CPU: ").append(etCpu.getText().toString().trim()).append("\n");
            }
            if (!etRam.getText().toString().trim().isEmpty()) {
                riepilogo.append("• RAM: ").append(etRam.getText().toString().trim()).append("\n");
            }
            if (!etSpazio.getText().toString().trim().isEmpty()) {
                riepilogo.append("• Spazio: ").append(etSpazio.getText().toString().trim()).append("\n");
            }
            riepilogo.append("\n");
        } else {
            riepilogo.append("🔧 Hardware: Non specificato\n\n");
        }

        // Modalità esperti
        riepilogo.append("👥 Selezione esperti: ");
        int checkedId = radioGroupModalita.getCheckedRadioButtonId();
        if (checkedId == R.id.radioAutomatica) {
            riepilogo.append("Automatica (tutti gli esperti disponibili)\n");
        } else if (checkedId == R.id.radioManuale) {
            riepilogo.append("Manuale (").append(espertiSelezionati.size()).append(" esperti scelti)\n");
            for (EspertoSelezionatoDTO esperto : espertiSelezionati) {
                riepilogo.append("  • ").append(esperto.getNome()).append(" - ").append(esperto.getSpecializzazione()).append("\n");
            }
        } else {
            riepilogo.append("Da selezionare\n");
        }

        // Note
        String note = etNoteAggiuntive.getText().toString().trim();
        if (!note.isEmpty()) {
            riepilogo.append("\n📝 Note: ").append(note);
        }

        tvRiepilogoFinale.setText(riepilogo.toString());
    }

    // ===== VALIDAZIONI =====

    private boolean validateCurrentStep() {
        switch (currentStep) {
            case 0:
                return validateStep1();
            case 1:
                return validateStep2();
            case 2:
                return validateStep3();
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
        if (cbAggiungiHardware.isChecked()) {
            String ram = etRam.getText().toString().trim();
            if (!ram.isEmpty()) {
                try {
                    int ramValue = Integer.parseInt(ram);
                    if (ramValue < 1 || ramValue > 128) {
                        etRam.setError("RAM deve essere tra 1 e 128 GB");
                        return false;
                    }
                } catch (NumberFormatException e) {
                    etRam.setError("Inserisci un numero valido per la RAM");
                    return false;
                }
            }

            String spazio = etSpazio.getText().toString().trim();
            if (!spazio.isEmpty()) {
                try {
                    int spazioValue = Integer.parseInt(spazio);
                    if (spazioValue < 1 || spazioValue > 3000) {
                        etSpazio.setError("Spazio deve essere tra 1 e 3000 GB");
                        return false;
                    }
                } catch (NumberFormatException e) {
                    etSpazio.setError("Inserisci un numero valido per lo spazio");
                    return false;
                }
            }
        }
        return true;
    }

    private boolean validateStep3() {
        int checkedId = radioGroupModalita.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(this, "Seleziona una modalità di selezione esperti", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (checkedId == R.id.radioManuale && espertiSelezionati.isEmpty()) {
            Toast.makeText(this, "Seleziona almeno un esperto per la modalità manuale", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
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

        // Modalità selezione
        int checkedId = radioGroupModalita.getCheckedRadioButtonId();
        String modalita = (checkedId == R.id.radioAutomatica) ? "AUTOMATICA" : "MANUALE";
        json.put("modalitaSelezione", modalita);

        // Distribuzioni candidate (da DistroSummary)
        JSONArray distribuzioniArray = new JSONArray();
        for (DistroSummaryDTO distro : distribuzioniSelezionate) {
            JSONObject distroJson = new JSONObject();
            distroJson.put("id", distro.getIdDistribuzione()); // Usa idDistribuzione dal summary
            distroJson.put("nome", distro.getNomeDisplay());
            distribuzioniArray.put(distroJson);
        }
        json.put("distribuzioniCandidate", distribuzioniArray);

        // Esperti (se modalità manuale)
        if ("MANUALE".equals(modalita)) {
            JSONArray espertiArray = new JSONArray();
            for (EspertoSelezionatoDTO esperto : espertiSelezionati) {
                JSONObject espertoJson = new JSONObject();
                espertoJson.put("id", esperto.getId());
                espertoJson.put("nome", esperto.getNome());
                espertoJson.put("specializzazione", esperto.getSpecializzazione());
                espertoJson.put("anniEsperienza", esperto.getAnniEsperienza());
                espertoJson.put("feedbackMedio", esperto.getFeedbackMedio());
                espertiArray.put(espertoJson);
            }
            json.put("espertiSelezionati", espertiArray);
        } else {
            json.put("espertiSelezionati", new JSONArray());
        }

        // Hardware opzionale
        if (cbAggiungiHardware.isChecked()) {
            String cpu = etCpu.getText().toString().trim();
            String ram = etRam.getText().toString().trim();
            String spazio = etSpazio.getText().toString().trim();
            String schedaVideo = etSchedaVideo.getText().toString().trim();
            String tipoSistema = spinnerTipoSistema.getSelectedItem().toString();

            if (!cpu.isEmpty()) json.put("cpu", cpu);
            if (!ram.isEmpty()) json.put("ram", ram + " GB");
            if (!spazio.isEmpty()) json.put("spazioArchiviazione", spazio + " GB");
            if (!schedaVideo.isEmpty()) json.put("schedaVideo", schedaVideo);
            if (!"Seleziona...".equals(tipoSistema)) json.put("tipoSistema", tipoSistema);
        }

        // Note aggiuntive
        String note = etNoteAggiuntive.getText().toString().trim();
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
                // Mostra loading se necessario
                activity.findViewById(R.id.progressLoadingDistro).setVisibility(View.VISIBLE);
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
            activity.findViewById(R.id.progressLoadingDistro).setVisibility(View.GONE);

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
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, "Errore nel caricamento distribuzioni", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(activity, "Impossibile caricare le distribuzioni", Toast.LENGTH_SHORT).show();
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
        recyclerDistroSummary.setLayoutManager(gridLayoutManager);
        recyclerDistroSummary.setAdapter(distroSummaryAdapter);
    }

    // TODO: Implementare LoadEspertiTask e InviaRichiestaTask
    // TODO: Implementare DistroSummaryAdapter e EspertiAdapter
}