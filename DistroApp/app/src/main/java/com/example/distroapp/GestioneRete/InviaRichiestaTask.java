package com.example.distroapp.GestioneRete;

import android.os.AsyncTask;
import com.example.distroapp.Entity.RichiestaUtente;
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

/**
 * Task per inviare la richiesta finale al server
 * Utilizzata nello Step5InviaRichiestaActivity
 */
public class InviaRichiestaTask extends AsyncTask<RichiestaUtente, Void, InviaRichiestaTask.TaskResult> {

    public interface OnRichiestaInviataListener {
        void onRichiestaInviata(String richiestaId);
        void onRichiestaError(String error);
    }

    public static class TaskResult {
        public String richiestaId;
        public String error;
        public boolean success;

        public TaskResult(String richiestaId) {
            this.richiestaId = richiestaId;
            this.success = true;
        }

        public TaskResult(String error, boolean success) {
            this.error = error;
            this.success = success;
        }
    }

    private WeakReference<OnRichiestaInviataListener> listenerRef;

    public InviaRichiestaTask(OnRichiestaInviataListener listener) {
        this.listenerRef = new WeakReference<>(listener);
    }

    @Override
    protected TaskResult doInBackground(RichiestaUtente... richieste) {
        if (richieste.length == 0) {
            return new TaskResult("Nessuna richiesta da inviare", false);
        }

        RichiestaUtente richiesta = richieste[0];
        HttpURLConnection connection = null;

        try {
            // URL del tuo endpoint esistente
            URL url = new URL("http://10.0.2.2:8080/LinuxFinal/RichiesteServlet/crea");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);

            // Converti la richiesta in JSON (puoi riutilizzare la tua logica esistente)
            String jsonRichiesta = richiesta.toJSONString();

            // Invia il JSON
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonRichiesta.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                String jsonResponse = readStream(connection.getInputStream());
                return parseSuccessResponse(jsonResponse);
            } else {
                String errorResponse = readStream(connection.getErrorStream());
                return new TaskResult("Errore del server: " + responseCode + " - " + errorResponse, false);
            }

        } catch (IOException e) {
            return new TaskResult("Errore di connessione: " + e.getMessage(), false);
        } catch (JSONException e) {
            return new TaskResult("Errore nella creazione richiesta: " + e.getMessage(), false);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Override
    protected void onPostExecute(TaskResult result) {
        OnRichiestaInviataListener listener = listenerRef.get();
        if (listener == null) return;

        if (result.success) {
            listener.onRichiestaInviata(result.richiestaId);
        } else {
            listener.onRichiestaError(result.error);
        }
    }

    /**
     * Parsing della risposta di successo dal server
     */
    private TaskResult parseSuccessResponse(String jsonString) throws JSONException {
        JSONObject response = new JSONObject(jsonString);

        if (response.getBoolean("success")) {
            // Il server dovrebbe restituire l'ID della richiesta creata
            String richiestaId = response.optString("richiestaId", "unknown");
            return new TaskResult(richiestaId);
        } else {
            String errore = response.optString("message", "Errore sconosciuto");
            return new TaskResult(errore, false);
        }
    }

    private String readStream(InputStream inputStream) throws IOException {
        if (inputStream == null) return "";

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