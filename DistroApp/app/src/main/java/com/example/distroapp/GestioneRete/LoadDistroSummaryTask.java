package com.example.distroapp.GestioneRete;

import android.os.AsyncTask;
import com.example.distroapp.Entity.DistroSummaryDTO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Task per caricare la lista delle distribuzioni summary dal server
 * Utilizzata nello Step1SelezionaDistroActivity
 */
public class LoadDistroSummaryTask extends AsyncTask<Void, Void, LoadDistroSummaryTask.TaskResult> {

    // Interface per comunicare i risultati all'activity
    public interface OnDistroSummaryLoadedListener {
        void onDistroSummaryLoaded(List<DistroSummaryDTO> distribuzioni);
        void onDistroSummaryError(String error);
    }

    // Classe per wrappare risultato e errore
    public static class TaskResult {
        public List<DistroSummaryDTO> distribuzioni;
        public String error;
        public boolean success;

        public TaskResult(List<DistroSummaryDTO> distribuzioni) {
            this.distribuzioni = distribuzioni;
            this.success = true;
        }

        public TaskResult(String error) {
            this.error = error;
            this.success = false;
        }
    }

    private WeakReference<OnDistroSummaryLoadedListener> listenerRef;

    public LoadDistroSummaryTask(OnDistroSummaryLoadedListener listener) {
        this.listenerRef = new WeakReference<>(listener);
    }

    @Override
    protected TaskResult doInBackground(Void... voids) {
        HttpURLConnection connection = null;

        try {
            // URL del tuo endpoint esistente
            URL url = new URL("http://10.0.2.2:8080/LinuxFinal/DatiSupportoServlet/distribuzioni-summary");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String jsonResponse = readStream(connection.getInputStream());
                List<DistroSummaryDTO> distribuzioni = parseDistribuzioniFromJson(jsonResponse);
                return new TaskResult(distribuzioni);
            } else {
                return new TaskResult("Errore del server: " + responseCode);
            }

        } catch (IOException e) {
            return new TaskResult("Errore di connessione: " + e.getMessage());
        } catch (JSONException e) {
            return new TaskResult("Errore nel parsing dei dati: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Override
    protected void onPostExecute(TaskResult result) {
        OnDistroSummaryLoadedListener listener = listenerRef.get();
        if (listener == null) return;

        if (result.success) {
            listener.onDistroSummaryLoaded(result.distribuzioni);
        } else {
            listener.onDistroSummaryError(result.error);
        }
    }

    /**
     * Parsing del JSON response usando la stessa struttura del tuo codice esistente
     */
    private List<DistroSummaryDTO> parseDistribuzioniFromJson(String jsonString) throws JSONException {
        List<DistroSummaryDTO> distribuzioni = new ArrayList<>();

        JSONObject response = new JSONObject(jsonString);
        if (!response.getBoolean("success")) {
            throw new JSONException("Response indicates failure");
        }

        JSONArray distribuzioniArray = response.getJSONArray("distribuzioni");

        for (int i = 0; i < distribuzioniArray.length(); i++) {
            JSONObject distroJson = distribuzioniArray.getJSONObject(i);

            DistroSummaryDTO distro = new DistroSummaryDTO();

            // Mapping dei campi dal JSON (usando la struttura del tuo server)
            distro.setId(distroJson.getInt("id"));
            distro.setIdDistribuzione(distroJson.getInt("idDistribuzione"));
            distro.setNomeDisplay(distroJson.getString("nomeDisplay"));
            distro.setDescrizioneBreve(distroJson.getString("descrizioneBreve"));

            // Campi opzionali
            if (distroJson.has("descrizioneDettaglio")) {
                distro.setDescrizioneDettaglio(distroJson.getString("descrizioneDettaglio"));
            }

            if (distroJson.has("icona")) {
                distro.setIcona(distroJson.getString("icona"));
            }

            if (distroJson.has("coloreHex")) {
                distro.setColoreHex(distroJson.getString("coloreHex"));
            }

            if (distroJson.has("livelloDifficolta")) {
                distro.setLivelloDifficolta(distroJson.getString("livelloDifficolta"));
            }

            if (distroJson.has("punteggioPopolarita")) {
                distro.setPunteggioPopolarita(distroJson.getInt("punteggioPopolarita"));
            }

            distribuzioni.add(distro);
        }

        return distribuzioni;
    }

    /**
     * Helper method per leggere l'InputStream
     */
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