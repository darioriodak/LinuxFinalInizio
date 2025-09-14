package entity.dto;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrazioneEspertoDTO {
	
	private String email;
    private String password;
    
    // Dati profilo base utente (obbligatori per tutti gli utenti)
    private String livelloEsperienza;  // Sempre "AVANZATO" per esperti
    private String scopoUso;          // Es: "Consulenza tecnica e supporto community"
    
    // Dati specifici esperto (obbligatori)
    private String specializzazione;  // Es: "Gaming Linux", "Server Administration"
    private int anniEsperienza;       // Anni di esperienza con Linux
    private String motivazione;       // Perché vuole diventare esperto
    
    
    // Costruttori
    public RegistrazioneEspertoDTO() {}
    
    public RegistrazioneEspertoDTO(String email, String password, String specializzazione, 
                                  int anniEsperienza, String motivazione) {
        this.email = email;
        this.password = password;
        this.livelloEsperienza = "AVANZATO"; // Default per esperti
        this.scopoUso = "Consulenza tecnica e supporto community";
        this.specializzazione = specializzazione;
        this.anniEsperienza = anniEsperienza;
        this.motivazione = motivazione;
    }
    
    /**
     * Deserializza JSON dal frontend per registrazione esperto
     * Formato atteso:
     * {
     *   "email": "esperto@example.com",
     *   "password": "securePassword123",
     *   "specializzazione": "Gaming e multimedia su Linux",
     *   "anniEsperienza": 5,
     *   "motivazione": "Voglio aiutare altri utenti a scegliere la distribuzione giusta...",
     *   "certificazioni": "LPIC-1, Red Hat Certified System Administrator",
     *   "linkedin": "https://linkedin.com/in/esperto-linux",
     *   "github": "https://github.com/esperto-linux",
     *   "biografia": "Sviluppatore senior con 5+ anni di esperienza..."
     * }
     */
    public static RegistrazioneEspertoDTO fromJSON(String jsonString) throws JSONException {
        JSONObject json = new JSONObject(jsonString);
        
        // Validazione campi obbligatori
        if (!json.has("email") || !json.has("password") || !json.has("specializzazione") ||
            !json.has("anniEsperienza") || !json.has("motivazione")) {
            throw new JSONException("Parametri obbligatori mancanti: email, password, specializzazione, anniEsperienza, motivazione");
        }
        
        String email = json.getString("email").trim();
        String password = json.getString("password");
        String specializzazione = json.getString("specializzazione").trim();
        int anniEsperienza = json.getInt("anniEsperienza");
        String motivazione = json.getString("motivazione").trim();
        
        // Validazioni business logic
        if (!isValidEmail(email)) {
            throw new JSONException("Formato email non valido");
        }
        
        if (password.length() < 8) {
            throw new JSONException("La password deve essere di almeno 8 caratteri");
        }
        
        if (specializzazione.length() < 5) {
            throw new JSONException("Specializzazione deve essere di almeno 5 caratteri");
        }
        
        if (anniEsperienza < 1 || anniEsperienza > 50) {
            throw new JSONException("Anni esperienza deve essere tra 1 e 50");
        }
        
        if (motivazione.length() < 50) {
            throw new JSONException("Motivazione deve essere di almeno 50 caratteri");
        }
        
        RegistrazioneEspertoDTO dto = new RegistrazioneEspertoDTO(
            email, password, specializzazione, anniEsperienza, motivazione
        );
        
        return dto;
    }
    
    /**
     * Serializza per conferma registrazione (senza password!)
     */
    public String toJSONString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("email", email);
        json.put("specializzazione", specializzazione);
        json.put("anniEsperienza", anniEsperienza);
        json.put("motivazione", motivazione);
     
        
        // NON includere password per sicurezza!
        return json.toString();
    }
    
    // Metodi di validazione
    private static boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".") && email.length() > 5;
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
    
    public String getSpecializzazione() { return specializzazione; }
    public void setSpecializzazione(String specializzazione) { this.specializzazione = specializzazione; }
    
    public int getAnniEsperienza() { return anniEsperienza; }
    public void setAnniEsperienza(int anniEsperienza) { this.anniEsperienza = anniEsperienza; }
    
    public String getMotivazione() { return motivazione; }
    public void setMotivazione(String motivazione) { this.motivazione = motivazione; }
    
    
    @Override
    public String toString() {
        return "RegistrazioneEspertoDTO{" +
                "email='" + email + '\'' +
                ", specializzazione='" + specializzazione + '\'' +
                ", anniEsperienza=" + anniEsperienza +
                ", motivazione='" + motivazione.substring(0, Math.min(50, motivazione.length())) + "...'" +
                '}'; // Password omessa per sicurezza
    }

}
