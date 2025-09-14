package com.example.distroapp.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.distroapp.Entity.Distro;
import com.example.distroapp.Entity.InfoUtente;
import com.example.distroapp.R;
import com.example.distroapp.databinding.ActivityInputBinding;

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

public class InputActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityInputBinding binding;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInputBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.buttonSuggerisci.setOnClickListener(this);


    }

// il lavoro pesante di connessione di rete viene fatto in un thread diverso dal main thread per non bloccare l'esecuzione dell'app
    private static class NetworkTask extends AsyncTask<InfoUtente, Void, String> {

        // Riferimento debole all'activity per evitare memory leak
        private WeakReference<InputActivity> activityReference; //

        // Costruttore
        NetworkTask(InputActivity context) {
            activityReference = new WeakReference<>(context);
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            InputActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            //activity.binding.progressBar.setVisibility(View.VISIBLE);
            activity.binding.buttonSuggerisci.setEnabled(false);
        }

        @Override
        protected String doInBackground(InfoUtente... params) {
            InfoUtente infoUtente = params[0];
            HttpURLConnection urlConnection = null;
            String serverResponseJsonString = null;

            // Serializza l'oggetto DTO in una stringa JSON.

            String jsonInputString = infoUtente.toJSONString();
            //per test, da rimuovere
            Log.d("NetworkTask", "JSON inviato al server: " + jsonInputString);
            
            try {
                URL url = new URL("http://10.0.2.2:8080/LinuxDistroFinder/DistroRecommendationServlet"); // URL del tuo server
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);
                urlConnection.setConnectTimeout(15000); // 15 secondi
                urlConnection.setReadTimeout(15000);    // 15 secondi

                // Scrivi il corpo JSON nella richiesta
                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Leggi la risposta dal server se la chiamata ha avuto successo
                    serverResponseJsonString = leggiStream(urlConnection.getInputStream());
                } else {

                    // Puoi leggere l'error stream per avere più dettagli sull'errore
                    serverResponseJsonString = leggiStream(urlConnection.getErrorStream());
                }

            } catch (IOException e) {
                Log.e("NetworkTask", "Errore durante la connessione di rete", e);
                return null; // Restituisce null in caso di errore
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return serverResponseJsonString;
        }

        @Override
        protected void onPostExecute(String resultJsonString) {
            super.onPostExecute(resultJsonString);
            InputActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) {
                return; // L'activity non esiste più, non fare nulla.
            }

            // Nascondi la ProgressBar e riabilita il pulsante
            //activity.binding.progressBar.setVisibility(View.GONE);
            activity.binding.buttonSuggerisci.setEnabled(true);

            if (resultJsonString != null && !resultJsonString.isEmpty()) {

                Log.d("NetworkTask", "Risposta JSON ricevuta: " + resultJsonString);

                // 1. Parsifica il risultato JSON nella lista di DTO
                //    (usando il tuo metodo statico manuale)
                List<Distro> raccomandazioni = null;
                try {
                    raccomandazioni = Distro.parsificaArrayDistro(resultJsonString);
                } catch (Exception e) {
                    Toast.makeText(activity, "Errore, i dati ricevuti dal server non sono validi.", Toast.LENGTH_LONG).show();
                    throw new RuntimeException(e);
                }

                // 2. CREA UN INTENT per avviare la nuova ResultsActivity
                Intent intent = new Intent(activity, ResultsActivity.class);

                // 3. INSERISCI I DATI nell'Intent come "extra".
                //    Perché questo funzioni, la classe DistroDto deve implementare l'interfaccia Parcelable.
                intent.putParcelableArrayListExtra("LISTA_RISULTATI", new ArrayList<>(raccomandazioni));

                // 4. AVVIA la ResultsActivity
                activity.startActivity(intent);

            } else {
                // Se c'è stato un errore di rete o il server ha risposto male
                Toast.makeText(activity, "Impossibile contattare il server. controlla la connessione.", Toast.LENGTH_LONG).show();
            }
        }

        private String leggiStream(InputStream in) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            return sb.toString();
        }

    }



    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.buttonSuggerisci) {

            //leggo i parametri inseriti dall'utente

            String architetturaCpu = binding.spinnerArchitetturaCpu.getSelectedItem().toString();
            String ramGBString = binding.editTextRamGB.getText().toString();
            String cpuTier = binding.spinnerCpuTier.getSelectedItem().toString();
            String spazioArchiviazioneString = binding.editTextSpazioArchiviazione.getText().toString();
            String tipoArchiviazione = binding.spinnerTipoArchiviazione.getSelectedItem().toString();
            String produttoreGpu = binding.spinnerProduttoreGpu.getSelectedItem().toString();
            String modelloGpu = binding.editTextModelloGpu.getText().toString();
            String livelloEsperienza = binding.spinnerLivelloEsperienza.getSelectedItem().toString();
            String useCase = binding.spinnerUseCase.getSelectedItem().toString();
            String ambienteDesktopPreferito = binding.spinnerAmbienteDesktopPreferito.getSelectedItem().toString();

            if (ramGBString.isEmpty() || architetturaCpu.isEmpty() || cpuTier.isEmpty() || livelloEsperienza.isEmpty()) {
                Toast.makeText(InputActivity.this, "Inserire Tutti i Campi Obbligatori : architettura cpu, ramMB, fascia cpu, spazio archiviazione", Toast.LENGTH_SHORT).show();
                return;
            }
            //parsing dei valori che devono essere numerici

            int ramGB = -1;
            int spazioArchiviazione = -1;

            try {
                ramGB = Integer.parseInt(ramGBString);
                if (ramGB < 1 || ramGB > 128) {
                    binding.editTextRamGB.setError("Il valore deve essere compreso tra 1 e 128 GB.");
                    return; // Interrompi l'esecuzione
                }
            } catch (NumberFormatException e) {
                binding.editTextRamGB.setError("Inserire un valore numerico valido");
                return;
            }

            if (!spazioArchiviazioneString.isEmpty()) {
                try {
                    spazioArchiviazione = Integer.parseInt(spazioArchiviazioneString);
                    if(spazioArchiviazione < 1 || spazioArchiviazione > 3000){
                        binding.editTextSpazioArchiviazione.setError("Inserire un valore in GB compreso tra 1 e 3000");
                        return;
                    }
                } catch (NumberFormatException e) {
                    throw new RuntimeException(e);
                }
            }
            //inizializzazione di infoUtente

            InfoUtente infoUtente = new InfoUtente(architetturaCpu, ramGB, cpuTier, livelloEsperienza);

            if (spazioArchiviazione != -1) {
                infoUtente.setSpazioArchiviazione(spazioArchiviazione);
            }
            if (!tipoArchiviazione.isEmpty()) {
                infoUtente.setTipoArchiviazione(tipoArchiviazione);
            }
            if (!produttoreGpu.isEmpty()) {
                infoUtente.setProduttoreGpu(produttoreGpu);
            }
            if (!modelloGpu.isEmpty()) {
                infoUtente.setModelloGpu(modelloGpu);
            }
            if (!useCase.isEmpty()) {
                infoUtente.setUseCase(useCase);
            }
            if (!ambienteDesktopPreferito.isEmpty()) {
                infoUtente.setAmbienteDesktopPreferito(ambienteDesktopPreferito);
            }

            if (infoUtente != null) {
                new NetworkTask(this).execute(infoUtente);

            }


        }
    }

}