package com.example.distroapp.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.distroapp.Entity.RegistrazioneUtenteDTO;
import com.example.distroapp.R;
import com.example.distroapp.databinding.ActivityRegisterBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 📝 LETTURA CAMPI AGGIORNATA
                String email = binding.editTextEmailRegister.getText().toString().trim();
                String password = binding.editTextPasswordRegister.getText().toString();
                String confirmPassword = binding.editTextConfirmPasswordRegister.getText().toString();
                String scopoUso = binding.editTextScopoUso.getText().toString().trim();
                String livelloEsperienza = binding.spinnerLivelloEsperienza.getSelectedItem().toString();

                // ✅ VALIDAZIONI AGGIORNATE
                if (email.isEmpty() || password.isEmpty() || scopoUso.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Email, password e scopo uso sono obbligatori", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    binding.editTextConfirmPasswordRegister.setError("Le password non coincidono");
                    return;
                }

                if (scopoUso.length() < 3) {
                    binding.editTextScopoUso.setError("Descrivi come userai Linux (minimo 3 caratteri)");
                    return;
                }

                // 🆕 CREA DTO AGGIORNATO (senza nome/cognome, con scopoUso)
                RegistrazioneUtenteDTO registrationData = new RegistrazioneUtenteDTO(
                        email, password, livelloEsperienza, scopoUso
                );

                try {
                    new RegisterTask(RegisterActivity.this).execute(registrationData.toJSONString());
                } catch (Exception e) {
                    Toast.makeText(RegisterActivity.this, "Errore nella preparazione dei dati", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        binding.textViewVaiALogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentLogin = new Intent (RegisterActivity.this,LoginActivity.class);
                startActivity(intentLogin);

            }
        });
    }


    private static class RegisterTask extends AsyncTask<String, Void, Integer> {

        private final WeakReference<RegisterActivity> activityReference;

        RegisterTask(RegisterActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            RegisterActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            activity.binding.buttonRegister.setEnabled(false);
            Toast.makeText(activity, "Registrazione in corso...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            String jsonInputString = params[0];
            HttpURLConnection urlConnection = null;
            int responseCode = -1;

            try {

                URL url = new URL("http://10.0.2.2:8080/LinuxFinal/AuthServlet/register-user");
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
            RegisterActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            activity.binding.buttonRegister.setEnabled(true);


            if (responseCode == 200) { //statuscode creato correttamente

                Toast.makeText(activity, "Registrazione avvenuta con successo!", Toast.LENGTH_LONG).show();

                // Reindirizza l'utente alla schermata di login
                Intent intent = new Intent(activity, LoginActivity.class);
                activity.startActivity(intent);
                activity.finish(); // Chiude la RegisterActivity

            } else if (responseCode == 409) { // statuscode conflitto
                Toast.makeText(activity, "Utente già Esistente", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(activity, "Errore durante la registrazione", Toast.LENGTH_LONG).show();
            }
        }
    }
}