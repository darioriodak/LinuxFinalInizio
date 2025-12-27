package com.example.distroapp.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.distroapp.Entity.Utente;
import com.example.distroapp.databinding.ActivityLoginBinding;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.distroapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //lettura
                String email = binding.editTextEmailLogin.getText().toString().trim();
                String password = binding.editTextPasswordLogin.getText().toString();
                // validazione dei campi
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Email e password sono obbligatori", Toast.LENGTH_SHORT).show();
                    return;
                }
                // creazione del JSON
                JSONObject loginData = new JSONObject();
                try {
                    loginData.put("email", email);
                    loginData.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }

                // avvio la chiamata di rete
                String jsonLogin = loginData.toString();
                new LoginTask(LoginActivity.this).execute(jsonLogin);

            }
        });

        binding.textViewVaiARegistrazione.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v){

                Intent intentRegistrazione = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intentRegistrazione);
            }

        });

    }

    private static class LoginTask extends AsyncTask<String, Void, String> {

        private final WeakReference<LoginActivity> activityReference;

        LoginTask(LoginActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            LoginActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            activity.binding.buttonLogin.setEnabled(false);
        }

        @Override
        protected String doInBackground(String... params) {  // ← Cambiato return type
            String jsonInputString = params[0];
            HttpURLConnection urlConnection = null;
            String responseBody = null;  // ← Nuovo: leggiamo il body della risposta
            int responseCode = -1;

            try {
                URL url = new URL("http://10.0.2.2:8080/LinuxFinal/AuthServlet/login");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
                urlConnection.setDoOutput(true);
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);

                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                responseCode = urlConnection.getResponseCode();

                // ✅ NUOVO: Leggi il body della risposta se login OK
                if (responseCode == 200) {
                    responseBody = leggiStream(urlConnection.getInputStream());
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            // ✅ NUOVO: Ritorna il JSON response invece del solo status code
            if (responseCode == 200 && responseBody != null) {
                return responseBody;
            } else {
                return String.valueOf(responseCode); // Per errori, ritorna solo il codice
            }
        }

        @Override
        protected void onPostExecute(String result) {  // ← Cambiato parametro
            LoginActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            activity.binding.buttonLogin.setEnabled(true);

            // ✅ NUOVO: Controlla se è un JSON (login OK) o un codice errore
            if (result != null && result.startsWith("{")) {
                // È un JSON response - login riuscito
                try {
                    // ✅ USA il metodo parseFromLoginResponse della classe Utente
                    JSONObject fullResponse = new JSONObject(result);
                    JSONObject userData = fullResponse.getJSONObject("user"); // ← Prendi solo questa parte!
                    Utente user = Utente.parseFromLoginResponse(userData.toString());

                    Toast.makeText(activity, "Login Avvenuto con successo!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(activity, DashboardActivity.class);

                    // ✅ PASSA l'utente REALE invece del mock
                    intent.putExtra("USER_DATA", user);

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                    activity.finish();

                } catch (Exception e) {
                    // Se parsing fallisce, fallback su utente mock
                    e.printStackTrace();
                    Toast.makeText(activity, "Login OK ma errore nel parsing dati utente", Toast.LENGTH_SHORT).show();

                    // Fallback: crea utente mock normale
                    Utente mockUser = new Utente(1, "test@example.com", "INTERMEDIO");
                    Intent intent = new Intent(activity, DashboardActivity.class);
                    intent.putExtra("USER_DATA", mockUser);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                    activity.finish();
                }

            } else {
                // È un codice di errore
                Toast.makeText(activity, "Email o password non validi.", Toast.LENGTH_LONG).show();
            }
        }

        //Helper method per leggere response stream
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

}