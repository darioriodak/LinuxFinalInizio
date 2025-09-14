package com.example.distroapp.Entity;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrazioneEspertoDTO {

    private String email;
    private String password;
    private String nome;
    private String cognome;
    private String livelloEsperienza;
    private String specializzazione;
    private int anniEsperienza;
    private String motivazione;
    private String certificazioni;
    private String linkedin;
    private String github;
    private String biografia;

    public RegistrazioneEspertoDTO(String email, String password, String nome, String cognome,
                                   String livelloEsperienza, String specializzazione, int anniEsperienza,
                                   String motivazione) {
        this.email = email;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
        this.livelloEsperienza = livelloEsperienza;
        this.specializzazione = specializzazione;
        this.anniEsperienza = anniEsperienza;
        this.motivazione = motivazione;

    }

    /**
     * Serializza in JSON per invio al server - CORRISPONDE ESATTAMENTE AL TUO ESEMPIO
     */
    public String toJSONString() throws JSONException {
        JSONObject json = new JSONObject();

        json.put("email", email);
        json.put("password", password);
        json.put("nome", nome);
        json.put("cognome", cognome);
        json.put("livelloEsperienza", livelloEsperienza);
        json.put("specializzazione", specializzazione);
        json.put("anniEsperienza", anniEsperienza);
        json.put("motivazione", motivazione);

        return json.toString();
    }

    // Getters
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getNome() { return nome; }
    public String getCognome() { return cognome; }
    public String getLivelloEsperienza() { return livelloEsperienza; }
    public String getSpecializzazione() { return specializzazione; }
    public int getAnniEsperienza() { return anniEsperienza; }
    public String getMotivazione() { return motivazione; }
}
