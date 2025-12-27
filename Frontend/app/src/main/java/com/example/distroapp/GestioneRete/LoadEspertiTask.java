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
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Task per caricare TUTTI gli esperti disponibili dal server
 * Versione semplificata senza filtri
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

    // ✅ SEMPLIFICATO: Non serve più Bundle wizardData
    public LoadEspertiTask(OnEspertiLoadedListener listener) {
        this.listenerRef = new WeakReference<>(listener);
    }

    @Override
    protected TaskResult doInBackground(Void... voids) {
        HttpURLConnection connection = null;

        try {
            // ✅ SEMPLIFICATO: GET request senza parametri di filtro
            URL url = new URL("http://10.0.2.2:8080/LinuxFinal/DatiSupportoServlet/esperti");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET"); // ← Cambiato da POST a GET
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

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
     * Parsing del JSON response per creare lista esperti
     * Versione robusta con gestione errori
     */
    /**
     * Parsing del JSON response per creare lista esperti
     * ✅ ADATTATO per il formato JSON del tuo server
     */
    private List<EspertoSelezionatoDTO> parseEspertiFromJson(String jsonString) throws JSONException {
        List<EspertoSelezionatoDTO> esperti = new ArrayList<>();

        try {
            android.util.Log.d("LoadEspertiTask", "JSON ricevuto: " + jsonString);

            // ✅ CORREZIONE: Il tuo server potrebbe restituire direttamente un array
            JSONArray espertiArray;

            // Prova prima come oggetto con array "esperti"
            try {
                JSONObject response = new JSONObject(jsonString);
                if (response.has("esperti")) {
                    espertiArray = response.getJSONArray("esperti");
                } else {
                    // Se non ha il wrapper, prova come array diretto
                    espertiArray = new JSONArray(jsonString);
                }
            } catch (JSONException e) {
                // Se fallisce, prova come array diretto
                espertiArray = new JSONArray(jsonString);
            }

            android.util.Log.d("LoadEspertiTask", "Parsing " + espertiArray.length() + " esperti");

            for (int i = 0; i < espertiArray.length(); i++) {
                try {
                    JSONObject espertoJson = espertiArray.getJSONObject(i);
                    EspertoSelezionatoDTO esperto = new EspertoSelezionatoDTO();

                    // ✅ CAMPI DEL TUO JSON - Obbligatori
                    esperto.setId(espertoJson.optInt("id", -1));
                    if (esperto.getId() == -1) {
                        android.util.Log.w("LoadEspertiTask", "Esperto senza ID valido, saltato");
                        continue;
                    }

                    // ✅ CORREZIONE: Il tuo JSON usa "nome" per l'email
                    String nomeEmail = espertoJson.optString("nome", "Esperto");
                    if (nomeEmail.contains("@")) {
                        // È un'email, estrai il nome
                        String nomeEstratto = nomeEmail.split("@")[0].replace(".", " ");
                        esperto.setNome(nomeEstratto);
                        esperto.setEmail(nomeEmail);
                    } else {
                        esperto.setNome(nomeEmail);
                        esperto.setEmail("");
                    }

                    // ✅ CAMPI MANCANTI - Usa valori di default
                    esperto.setCognome(""); // Non presente nel tuo JSON

                    esperto.setSpecializzazione(espertoJson.optString("specializzazione", "Esperto Linux"));
                    esperto.setAnniEsperienza(espertoJson.optInt("anniEsperienza", 1));

                    // ✅ CAMPI CON DEFAULT
                    esperto.setLivelloEsperienza("ESPERTO"); // Non presente, usa default
                    esperto.setFeedbackMedio(espertoJson.optDouble("feedbackMedio", 0.0));
                    esperto.setNumeroValutazioni(espertoJson.optInt("numeroValutazioni", 0));
                    esperto.setDisponibile(true); // Non presente, assume disponibile
                    esperto.setBio(null); // Non presente nel tuo JSON

                    esperti.add(esperto);

                    android.util.Log.d("LoadEspertiTask", "Aggiunto esperto: " + esperto.getNome() +
                            " - " + esperto.getSpecializzazione());

                } catch (Exception e) {
                    android.util.Log.e("LoadEspertiTask", "Errore parsing esperto " + i, e);
                    // Continua con il prossimo esperto
                }
            }

            android.util.Log.d("LoadEspertiTask", "Parsing completato: " + esperti.size() + " esperti validi");

        } catch (JSONException e) {
            android.util.Log.e("LoadEspertiTask", "Errore parsing JSON", e);
            throw e;
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