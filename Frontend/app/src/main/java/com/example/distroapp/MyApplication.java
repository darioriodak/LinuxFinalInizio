package com.example.distroapp;

import android.app.Application;

import java.net.CookieHandler;
import java.net.CookieManager;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Imposta un gestore di cookie predefinito per tutta l'applicazione.
        // Questa singola riga abiliterà la gestione automatica dei cookie
        // (come JSESSIONID) per tutte le chiamate HttpURLConnection.
        CookieHandler.setDefault(new CookieManager());
    }

}
