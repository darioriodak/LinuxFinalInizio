package com.example.distroapp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.distroapp.R;
import com.example.distroapp.Adapter.DistroSummaryAdapter;
import com.example.distroapp.Entity.DistroSummaryDTO;
import com.example.distroapp.GestioneRete.LoadDistroSummaryTask;

import java.util.ArrayList;
import java.util.List;

public class Step1SelezionaDistroActivity extends AppCompatActivity
        implements LoadDistroSummaryTask.OnDistroSummaryLoadedListener {

    // UI Components
    private TextView tvTitolo, tvDescrizione, tvStepCounter;
    private View progressStep1, progressStep2, progressStep3, progressStep4;
    private RecyclerView recyclerViewDistribuzioni;
    private ProgressBar progressBarDistribuzioni;
    private TextView tvErroreDistribuzioni;
    private Button btnAvanti;

    // Data
    private DistroSummaryAdapter distroAdapter;
    private List<DistroSummaryDTO> distribuzioniDisponibili = new ArrayList<>();
    private List<DistroSummaryDTO> distribuzioniSelezionate = new ArrayList<>();

    // SharedPreferences per salvare il progresso
    private SharedPreferences wizardPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step1_seleziona_distro);

        initViews();
        setupProgressIndicator();
        setupRecyclerView();
        loadDistribuzioni();
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

        // Content
        recyclerViewDistribuzioni = findViewById(R.id.recycler_view_distribuzioni);
        progressBarDistribuzioni = findViewById(R.id.progress_bar_distribuzioni);
        tvErroreDistribuzioni = findViewById(R.id.tv_errore_distribuzioni);


        btnAvanti = findViewById(R.id.btn_avanti);

        // SharedPreferences
        wizardPrefs = getSharedPreferences("wizard_richiesta", MODE_PRIVATE);
    }

    private void setupProgressIndicator() {
        tvTitolo.setText("Seleziona Distribuzioni");
        tvDescrizione.setText("Scegli le distribuzioni Linux che ti interessano di più");
        tvStepCounter.setText("Step 1 di 4");

        // Evidenzia step corrente
        progressStep1.setBackgroundResource(R.drawable.step_active);
        progressStep2.setBackgroundResource(R.drawable.step_inactive);
        progressStep3.setBackgroundResource(R.drawable.step_inactive);
        progressStep4.setBackgroundResource(R.drawable.step_inactive);
    }

    private void setupRecyclerView() {
        distroAdapter = new DistroSummaryAdapter(distribuzioniDisponibili, new DistroSummaryAdapter.OnDistroClickListener() {
            @Override
            public void onDistroClick(DistroSummaryDTO distro) {
                toggleDistroSelection(distro);
            }
        });

        recyclerViewDistribuzioni.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDistribuzioni.setAdapter(distroAdapter);

        recyclerViewDistribuzioni.setVisibility(View.VISIBLE);
        android.util.Log.d("Step1Activity", "RecyclerView FORZATO VISIBLE");
    }

    private void loadDistribuzioni() {
        progressBarDistribuzioni.setVisibility(View.VISIBLE);
        tvErroreDistribuzioni.setVisibility(View.GONE);

        LoadDistroSummaryTask task = new LoadDistroSummaryTask(this);
        task.execute();
    }

    private void toggleDistroSelection(DistroSummaryDTO distro) {
        if (distribuzioniSelezionate.contains(distro)) {
            distribuzioniSelezionate.remove(distro);
            distro.setSelected(false);
        } else {
            distribuzioniSelezionate.add(distro);
            distro.setSelected(true);
        }

        distroAdapter.notifyDataSetChanged();
        updateButtonState();
        saveCurrentData();
    }

    private void updateButtonState() {
        btnAvanti.setEnabled(!distribuzioniSelezionate.isEmpty());

        if (distribuzioniSelezionate.isEmpty()) {
            btnAvanti.setText("Seleziona almeno una distribuzione");
            btnAvanti.setAlpha(0.5f);
        } else {
            btnAvanti.setText("Avanti (" + distribuzioniSelezionate.size() + " selezionate)");
            btnAvanti.setAlpha(1.0f);
        }
    }

    private void setupButtons() {
        btnAvanti.setOnClickListener(v -> {
            if (validateStep()) {
                saveCurrentData();
                proceedToNextStep();
            }
        });

        updateButtonState();
    }

    private boolean validateStep() {
        if (distribuzioniSelezionate.isEmpty()) {
            Toast.makeText(this, "Seleziona almeno una distribuzione", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveCurrentData() {
        SharedPreferences.Editor editor = wizardPrefs.edit();

        // Salva IDs distribuzioni selezionate
        StringBuilder selectedIds = new StringBuilder();
        for (int i = 0; i < distribuzioniSelezionate.size(); i++) {
            selectedIds.append(distribuzioniSelezionate.get(i).getId());
            if (i < distribuzioniSelezionate.size() - 1) {
                selectedIds.append(",");
            }
        }
        editor.putString("distribuzioni_selezionate", selectedIds.toString());



        editor.apply();
    }

    private void loadSavedData() {
        // Carica dati salvati se esistenti
        String savedDistros = wizardPrefs.getString("distribuzioni_selezionate", "");




        // Le distribuzioni verranno caricate dopo aver ricevuto la lista dal server
    }

    private void proceedToNextStep() {
        Intent intent = new Intent(this, Step2HardwareActivity.class);

        // Passa i dati al prossimo step
        ArrayList<Integer> selectedIds = new ArrayList<>();
        for (DistroSummaryDTO distro : distribuzioniSelezionate) {
            selectedIds.add(distro.getId());
        }
        intent.putIntegerArrayListExtra("distribuzioni_selezionate", selectedIds);

        startActivity(intent);
    }

    // Implementazione callback per il caricamento distribuzioni
    @Override
    public void onDistroSummaryLoaded(List<DistroSummaryDTO> distribuzioni) {
        progressBarDistribuzioni.setVisibility(View.GONE);

        android.util.Log.d("Step1Activity", "Ricevute " + (distribuzioni != null ? distribuzioni.size() : 0) + " distribuzioni");

        if (distribuzioni != null && !distribuzioni.isEmpty()) {
            android.util.Log.d("Step1Activity", "Prima distribuzione: " + distribuzioni.get(0).getNomeDisplay());

            distribuzioniDisponibili.clear();
            distribuzioniDisponibili.addAll(distribuzioni);
            distroAdapter.notifyDataSetChanged();

            // ✅ SOLUZIONE: Forza sempre la visibilità
            recyclerViewDistribuzioni.setVisibility(View.VISIBLE);
            recyclerViewDistribuzioni.requestLayout(); // Forza il ricalcolo del layout

            // Ripristina selezioni salvate
            restoreSavedSelections();

            android.util.Log.d("Step1Activity", "RecyclerView FORZATO visible con " + distribuzioni.size() + " items");
        } else {
            android.util.Log.e("Step1Activity", "Lista distribuzioni vuota o null");
            tvErroreDistribuzioni.setText("Nessuna distribuzione disponibile");
            tvErroreDistribuzioni.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDistroSummaryError(String error) {
        progressBarDistribuzioni.setVisibility(View.GONE);
        tvErroreDistribuzioni.setText("Errore nel caricamento: " + error);
        tvErroreDistribuzioni.setVisibility(View.VISIBLE);
    }

    private void restoreSavedSelections() {
        String savedDistros = wizardPrefs.getString("distribuzioni_selezionate", "");
        if (!savedDistros.isEmpty()) {
            String[] ids = savedDistros.split(",");
            for (String idStr : ids) {
                try {
                    int id = Integer.parseInt(idStr);
                    for (DistroSummaryDTO distro : distribuzioniDisponibili) {
                        if (distro.getId() == id) {
                            distro.setSelected(true);
                            distribuzioniSelezionate.add(distro);
                            break;
                        }
                    }
                } catch (NumberFormatException e) {
                    // Ignora ID non validi
                }
            }
            distroAdapter.notifyDataSetChanged();
            updateButtonState();
        }
    }

    @Override
    public void onBackPressed() {
        // Salva dati prima di uscire
        saveCurrentData();
        super.onBackPressed();
    }
}