package com.example.distroapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.distroapp.Entity.Utente;
import com.example.distroapp.Entity.Utente;
import com.example.distroapp.R;
import com.example.distroapp.Util.PreferencesManager;
import com.example.distroapp.databinding.ActivityDashboardBinding;

public class DashboardActivity extends AppCompatActivity {

    private ActivityDashboardBinding binding;
    private Utente currentUser;
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup toolbar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Dashboard");

        // Inizializza PreferencesManager
        preferencesManager = new PreferencesManager(this);

        // Recupera utente da Intent o da SharedPreferences
        setupUserData();

        // Setup UI
        setupDashboard();
        setupClickListeners();
    }

    private void setupUserData() {
        // Prova prima a recuperare dall'Intent (login appena fatto)
        currentUser = getIntent().getParcelableExtra("USER_DATA");

        // Se null, prova dalle SharedPreferences (sessione salvata)
        if (currentUser == null && preferencesManager.isLoggedIn()) {
            // Crea mock user da preferences (in futuro: chiamata API per dati completi)
            currentUser = new Utente(
                    preferencesManager.getUserId(),
                    preferencesManager.getUserEmail(),
                    "INTERMEDIO"  // livelloEsperienza mock (o da preferences se salvato)
            );
        }

        // Se ancora null, redirect al login
        if (currentUser == null) {
            redirectToLogin();
            return;
        }

        // Salva sessione se non già salvata
        if (!preferencesManager.isLoggedIn()) {
            preferencesManager.saveUserSession(currentUser);
        }
    }

    private void setupDashboard() {
        // Personalizza messaggio di benvenuto
        String welcomeMessage = "Ciao " + currentUser.getEmail() + "!";
        binding.textViewWelcome.setText(welcomeMessage);

        // Mostra tipo account
        if (currentUser.isEsperto()) {
            binding.textViewAccountType.setText("Account Esperto");
            binding.textViewAccountType.setVisibility(View.VISIBLE);

            // Nascondi card "Nuova Richiesta" per esperti
            binding.cardNuovaRichiesta.setVisibility(View.GONE);

            // Mostra card specifica per esperti
            binding.cardEspertoActions.setVisibility(View.VISIBLE);
        } else {
            binding.textViewAccountType.setText("Account Utente");
            binding.textViewAccountType.setVisibility(View.VISIBLE);

            // Mostra card per utenti normali
            binding.cardNuovaRichiesta.setVisibility(View.VISIBLE);
            binding.cardEspertoActions.setVisibility(View.GONE);
        }

        // TODO: Carica contatori reali dal server
        // Per ora usa valori mock
        binding.textViewContatoreRichieste.setText("Le mie richieste (0)");
        binding.textViewContatoreNotifiche.setText("Notifiche (0)");
    }

    private void setupClickListeners() {
        // Card Nuova Richiesta (UTENTI)
        binding.cardNuovaRichiesta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Avvia il TUO wizard 4-step!
                Intent intent = new Intent(DashboardActivity.this, CreaRichiestaActivity.class);
                intent.putExtra("USER_DATA", currentUser);
                startActivity(intent);
            }
        });

        // Card Valuta Richieste (ESPERTI)
        binding.cardEspertoActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Aprire lista richieste da valutare
                // Intent intent = new Intent(DashboardActivity.this, ValutaRichiesteActivity.class);
                // startActivity(intent);
            }
        });

        // Card Le mie richieste
        binding.cardMieRichieste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Aprire lista richieste utente
                // Intent intent = new Intent(DashboardActivity.this, ListaRichiesteActivity.class);
                // startActivity(intent);
            }
        });

        // Card Notifiche
        binding.cardNotifiche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Aprire lista notifiche
                // Intent intent = new Intent(DashboardActivity.this, NotificheActivity.class);
                // startActivity(intent);
            }
        });

        // Card Profilo
        binding.cardProfilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Aprire profilo utente
                // Intent intent = new Intent(DashboardActivity.this, ProfiloActivity.class);
                // startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            logout();
            return true;
        } else if (id == R.id.action_settings) {
            // TODO: Aprire impostazioni
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        // Pulisci sessione
        preferencesManager.clearUserSession();

        // Redirect al login
        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ricarica contatori quando torni al dashboard
        // TODO: Implementare ricaricamento dati dal server
        refreshCounters();
    }

    private void refreshCounters() {
        // TODO: Chiamate API per aggiornare contatori
        // binding.textViewContatoreRichieste.setText("Le mie richieste (" + count + ")");
        // binding.textViewContatoreNotifiche.setText("Notifiche (" + count + ")");
    }

    // Getters per eventuali Fragment (future)
    public Utente getCurrentUser() {
        return currentUser;
    }

    public boolean isUserExpert() {
        return currentUser != null && currentUser.isEsperto();
    }
}