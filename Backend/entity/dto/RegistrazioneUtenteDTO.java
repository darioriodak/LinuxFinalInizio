package entity.dto;

import org.json.JSONException;
import org.json.JSONObject;
import enumerazioni.LivelloEsperienza;

public class RegistrazioneUtenteDTO {
	
	private String email;
    private String password;
    
    // Dati profilo utente (obbligatori)
    private String livelloEsperienza;  // String per JSON, convertito in enum
    private String scopoUso;
    
    // Costruttori
    public RegistrazioneUtenteDTO() {}
    
    public RegistrazioneUtenteDTO(String email, String password, String livelloEsperienza, String scopoUso) {
        this.email = email;
        this.password = password;
        this.livelloEsperienza = livelloEsperienza;
        this.scopoUso = scopoUso;
    }
    
    /**
     * Deserializza JSON dal frontend per registrazione utente
     * Formato atteso:
     * {
     *   "email": "user@example.com",
     *   "password": "securePassword123",
     *   "livelloEsperienza": "INTERMEDIO",
     *   "scopoUso": "Sviluppo software e uso quotidiano"
     * }
     */
    public static RegistrazioneUtenteDTO fromJSON(String jsonString) throws JSONException {
        JSONObject json = new JSONObject(jsonString);
        
        // Validazione campi obbligatori
        if (!json.has("email") || !json.has("password") || 
            !json.has("livelloEsperienza") || !json.has("scopoUso")) {
            throw new JSONException("Parametri obbligatori mancanti: email, password, livelloEsperienza, scopoUso");
        }
        
        String email = json.getString("email").trim();
        String password = json.getString("password");
        String livelloEsperienza = json.getString("livelloEsperienza").trim().toUpperCase();
        String scopoUso = json.getString("scopoUso").trim();
        
        // Validazioni business logic
        if (!isValidEmail(email)) {
            throw new JSONException("Formato email non valido");
        }
        
        if (password.length() < 8) {
            throw new JSONException("La password deve essere di almeno 8 caratteri");
        }
        
        if (!isValidLivelloEsperienza(livelloEsperienza)) {
            throw new JSONException("Livello esperienza deve essere: PRINCIPIANTE, INTERMEDIO, o AVANZATO");
        }
        
        if (scopoUso.length() < 10) {
            throw new JSONException("Scopo uso deve essere di almeno 10 caratteri");
        }
        
        return new RegistrazioneUtenteDTO(email, password, livelloEsperienza, scopoUso);
    }
    
    /**
     * Serializza per conferma registrazione (senza password!)
     */
    public String toJSONString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("email", email);
        json.put("livelloEsperienza", livelloEsperienza);
        json.put("scopoUso", scopoUso);
        // NON includere password per sicurezza!
        return json.toString();
    }
    
    // Metodi di validazione
    private static boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".") && email.length() > 5;
    }
    
    private static boolean isValidLivelloEsperienza(String livello) {
        try {
            LivelloEsperienza.valueOf(livello);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    // Metodo di utilità per conversione enum
    public LivelloEsperienza getLivelloEsperienzaEnum() {
        return LivelloEsperienza.valueOf(livelloEsperienza.toUpperCase());
    }
    
    // Getters e Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getLivelloEsperienza() { return livelloEsperienza; }
    public void setLivelloEsperienza(String livelloEsperienza) { this.livelloEsperienza = livelloEsperienza; }
    
    public String getScopoUso() { return scopoUso; }
    public void setScopoUso(String scopoUso) { this.scopoUso = scopoUso; }
    
    @Override
    public String toString() {
        return "RegistrazioneUtenteDTO{" +
                "email='" + email + '\'' +
                ", livelloEsperienza='" + livelloEsperienza + '\'' +
                ", scopoUso='" + scopoUso + '\'' +
                '}'; // Password omessa per sicurezza
    }

}
