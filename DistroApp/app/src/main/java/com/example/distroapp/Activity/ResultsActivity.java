package com.example.distroapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.distroapp.Entity.Distro;
import com.example.distroapp.Adapter.DistroAdapter;
import com.example.distroapp.R;

import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewRisultati;
    private DistroAdapter adapter;
    private ArrayList<Distro> raccomandazioni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Ottieni l'Intent che ha avviato questa activity
        Intent intent = getIntent();

        // Estrai l'ArrayList di oggetti DistroDto
        raccomandazioni = intent.getParcelableArrayListExtra("LISTA_RISULTATI");

        // Trova la RecyclerView nel layout
        recyclerViewRisultati = findViewById(R.id.recyclerViewRisultati);

        // Controlla se abbiamo ricevuto dei dati validi
        if (raccomandazioni != null && !raccomandazioni.isEmpty()) {

            adapter = new DistroAdapter(raccomandazioni);

            // 2. Imposta un LayoutManager per la RecyclerView (dice come disporre gli elementi)
            //    LinearLayoutManager li dispone come una lista verticale standard.
            recyclerViewRisultati.setLayoutManager(new LinearLayoutManager(this));

            // 3. Collega l'adapter alla RecyclerView
            recyclerViewRisultati.setAdapter(adapter);
        } else {
            Toast.makeText(this, "Nessun risultato da mostrare.", Toast.LENGTH_LONG).show();
        }
    }

}