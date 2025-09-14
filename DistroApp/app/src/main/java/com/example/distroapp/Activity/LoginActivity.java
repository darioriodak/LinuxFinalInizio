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

    private  static class LoginTask extends AsyncTask<String, Void, Integer> {

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
        protected Integer doInBackground(String... params) {

            // return 200; // Simula un successo per test
            String jsonInputString = params[0];
            HttpURLConnection urlConnection = null;
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
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return responseCode;
        }

        @Override
        protected void onPostExecute(Integer responseCode) {
            LoginActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            activity.binding.buttonLogin.setEnabled(true);

            if (responseCode == 200) { // 200 OK
                // Invece di andare  a InputActivity,
                // vai alla nuova DashboardActivity
                Toast.makeText(activity, "Login Avvenuto con successo!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(activity, DashboardActivity.class);
                // ✅ AGGIUNGI: Passa dati utente (per ora mock, poi useremo response JSON)
                Utente mockUser = new Utente(1, "test@example.com", "Test", "User", 2);
                intent.putExtra("USER_DATA", mockUser);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
                activity.finish();
            } else {
                Toast.makeText(activity, "Email o password non validi.", Toast.LENGTH_LONG).show();
            }
        }
    }

}