package com.example.distroapp.Entity;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrazioneUtenteDTO {

    private String email;
    private String password;
    private String livelloEsperienza; // "PRINCIPIANTE", "INTERMEDIO", "ESPERTO"

    private String scopoUso;

    public RegistrazioneUtenteDTO(String email, String password, String livelloEsperienza, String scopoUso) {
        this.email = email;
        this.password = password;
        this.livelloEsperienza = livelloEsperienza;
        this.scopoUso = scopoUso ;
    }

    /**
     * Serializza in JSON per invio al server
     */
    public String toJSONString() throws JSONException {
        JSONObject json = new JSONObject();

        json.put("email", email);
        json.put("password", password);
        json.put("livelloEsperienza", livelloEsperienza);
        json.put("scopoUso", scopoUso);

        return json.toString();
    }

    // Getters
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getLivelloEsperienza() { return livelloEsperienza; }
    public String getScopoUso() { return scopoUso; }
}
