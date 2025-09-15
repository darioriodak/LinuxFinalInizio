package com.example.distroapp.GestioneRete;

import android.os.AsyncTask;
import android.os.Bundle;
import com.example.distroapp.Entity.EspertoSelezionatoDTO;
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

/**
 * Task per caricare la lista degli esperti disponibili dal server
 * Filtra gli esperti basandosi sui dati del wizard (distribuzioni, esperienza, etc.)
 */
public class LoadEspertiTask extends AsyncTask<Void, Void, LoadEspertiTask.TaskResult> {

    public interface OnEspertiLoadedListener {
        void onEspertiLoaded(List<EspertoSelezionatoDTO> esperti);
        void onEspertiError(String error);
    }

    public static class TaskResult {
        public List<EspertoSelezionatoDTO> esperti;
        public String error;
        public boolean success;

        public TaskResult(List<EspertoSelezionatoDTO> esperti) {
            this.esperti = esperti;
            this.success = true;
        }

        public TaskResult(String error) {
            this.error = error;
            this.success = false;
        }
    }

    private WeakReference<OnEspertiLoadedListener> listenerRef;
    private Bundle wizardData;  // Dati del wizard per filtrare esperti

    public LoadEspertiTask(OnEspertiLoadedListener listener, Bundle wizardData) {
        this.listenerRef = new WeakReference<>(listener);
        this.wizardData = wizardData;
    }

    @Override
    protected TaskResult doInBackground(Void... voids) {
        HttpURLConnection connection = null;

        try {
            // URL per caricare esperti (puoi filtrare lato server o client)
            URL url = new URL("http://10.0.2.2:8080/LinuxFinal/EspertiServlet/disponibili");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            // Invia i dati del wizard per filtrare gli esperti appropriati
            String jsonRequest = createFilterRequest();
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonRequest.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String jsonResponse = readStream(connection.getInputStream());
                List<EspertoSelezionatoDTO> esperti = parseEspertiFromJson(jsonResponse);
                return new TaskResult(esperti);
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
        OnEspertiLoadedListener listener = listenerRef.get();
        if (listener == null) return;

        if (result.success) {
            listener.onEspertiLoaded(result.esperti);
        } else {
            listener.onEspertiError(result.error);
        }
    }

    /**
     * Crea la richiesta JSON con i dati del wizard per filtrare esperti appropriati
     */
    private String createFilterRequest() throws JSONException {
        JSONObject request = new JSONObject();

        if (wizardData != null) {
            // Dati Step 3: Esperienza utente
            Bundle esperienza = wizardData.getBundle("esperienza");
            if (esperienza != null) {
                String livelloEsperienza = esperienza.getString("esperienza", "");
                if (!livelloEsperienza.isEmpty()) {
                    request.put("livelloUtenteEsperienza", livelloEsperienza);
                }

                ArrayList<String> modalita = esperienza.getStringArrayList("modalita");
                if (modalita != null && !modalita.isEmpty()) {
                    JSONArray modalitaArray = new JSONArray();
                    for (String mod : modalita) {
                        modalitaArray.put(mod);
                    }
                    request.put("modalitaUtilizzo", modalitaArray);
                }
            }

            // Dati Step 1: Distribuzioni (per matching specializzazioni)
            @SuppressWarnings("unchecked")
            ArrayList<Object> distribuzioni = (ArrayList<Object>) wizardData.getSerializable("distribuzioni");
            if (distribuzioni != null && !distribuzioni.isEmpty()) {
                JSONArray distroArray = new JSONArray();
                // Note: Potresti dover convertire gli oggetti DistroSummaryDTO
                request.put("distribuzioniInteresse", distroArray);
            }

            // Parametri di filtro
            request.put("soloDisponibili", true);
            request.put("maxEsperti", 10);  // Limite risultati
        }

        return request.toString();
    }

    /**
     * Parsing del JSON response per creare lista esperti
     */
    private List<EspertoSelezionatoDTO> parseEspertiFromJson(String jsonString) throws JSONException {
        List<EspertoSelezionatoDTO> esperti = new ArrayList<>();

        JSONObject response = new JSONObject(jsonString);
        if (!response.optBoolean("success", false)) {
            throw new JSONException("Response indicates failure");
        }

        JSONArray espertiArray = response.getJSONArray("esperti");

        for (int i = 0; i < espertiArray.length(); i++) {
            JSONObject espertoJson = espertiArray.getJSONObject(i);

            EspertoSelezionatoDTO esperto = new EspertoSelezionatoDTO();

            // Mapping campi obbligatori
            esperto.setId(espertoJson.getInt("id"));
            esperto.setNome(espertoJson.getString("nome"));
            esperto.setCognome(espertoJson.getString("cognome"));
            esperto.setEmail(espertoJson.getString("email"));

            // Campi profilo esperto
            if (espertoJson.has("specializzazione")) {
                esperto.setSpecializzazione(espertoJson.getString("specializzazione"));
            }

            if (espertoJson.has("anniEsperienza")) {
                esperto.setAnniEsperienza(espertoJson.getInt("anniEsperienza"));
            }

            if (espertoJson.has("livelloEsperienza")) {
                esperto.setLivelloEsperienza(espertoJson.getString("livelloEsperienza"));
            }

            // Feedback e valutazioni
            if (espertoJson.has("feedbackMedio")) {
                esperto.setFeedbackMedio(espertoJson.getDouble("feedbackMedio"));
            }

            if (espertoJson.has("numeroValutazioni")) {
                esperto.setNumeroValutazioni(espertoJson.getInt("numeroValutazioni"));
            }

            // Disponibilità
            esperto.setDisponibile(espertoJson.optBoolean("disponibile", true));

            // Bio opzionale
            if (espertoJson.has("bio")) {
                esperto.setBio(espertoJson.getString("bio"));
            }

            esperti.add(esperto);
        }

        return esperti;
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