package servlet;



import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import dao.*;
import entity.database.*;
import entity.dto.*;
import enumerazioni.LivelloEsperienza;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import entity.database.BCrypt;

/**
 * Servlet per gestione autenticazione, registrazione utenti ed esperti
 * Endpoints: /register-user, /register-expert, /login, /logout
 */

public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // DAO instances
    private UtenteDAO utenteDAO;
    private EspertoDAO espertoDAO;
    
    // Configurazioni da web.xml
    private int sessionTimeout;
    private int bcryptRounds;
    
    @Override
    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        // Recupera parametri database
        String ip = getInitParameter("ip");
        String port = getInitParameter("port");
        String dbName = getInitParameter("dbName");
        String userName = getInitParameter("userName");
        String password = getInitParameter("password");
        
        // Parametri specifici autenticazione
        this.sessionTimeout = Integer.parseInt(getInitParameter("session.timeout"));
        this.bcryptRounds = Integer.parseInt(getInitParameter("bcrypt.rounds"));
        
        // Inizializza DAO
        this.utenteDAO = new UtenteDAOImpl(ip, port, dbName, userName, password);
        this.espertoDAO = new EspertoDAOImpl(ip, port, dbName, userName, password);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Configura response per JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null) {
            try {
				inviaErrore(response, 400, "Endpoint mancante");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return;
        }
        
        try {
            switch (pathInfo) {
                case "/register-user":
                    gestisciRegistrazioneUtente(request, response);
                    break;
                case "/register-expert":
                    gestisciRegistrazioneEsperto(request, response);
                    break;
                case "/login":
                    gestisciLogin(request, response);
                    break;
                case "/logout":
                    gestisciLogout(request, response);
                    break;
                default:
                    inviaErrore(response, 404, "Endpoint non trovato: " + pathInfo);
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
				inviaErrore(response, 500, "Errore database: " + e.getMessage());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        } catch (JSONException e) {
            e.printStackTrace();
            try {
				inviaErrore(response, 400, "JSON malformato: " + e.getMessage());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        } catch (Exception e) {
            e.printStackTrace();
            try {
				inviaErrore(response, 500, "Errore interno del server");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
    }
    
    /**
     * POST /AuthServlet/register-user
     * Registra un nuovo utente normale
     */
    private void gestisciRegistrazioneUtente(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException, JSONException {
        
        String jsonString = leggiCorpoRichiesta(request);
        RegistrazioneUtenteDTO registrazioneDTO = RegistrazioneUtenteDTO.fromJSON(jsonString);
        
        // Controlla se utente esiste già
        if (utenteDAO.findByEmail(registrazioneDTO.getEmail()) != null) {
            inviaErrore(response, 409, "Un utente con questa email esiste già");
            return;
        }
        
        // Hash della password con BCrypt
        String passwordHash = hashPassword(registrazioneDTO.getPassword());
        
        // Crea entità utente
        Utente nuovoUtente = new Utente(
            registrazioneDTO.getEmail(),
            passwordHash,
            registrazioneDTO.getLivelloEsperienzaEnum(),
            registrazioneDTO.getScopoUso()
        );
        
        // Salva nel database
        int idUtente = utenteDAO.salva(nuovoUtente);
        
        if (idUtente > 0) {
            JSONObject response_json = new JSONObject();
            response_json.put("success", true);
            response_json.put("message", "Utente registrato con successo");
            response_json.put("idUtente", idUtente);
            response_json.put("email", registrazioneDTO.getEmail());
            
            PrintWriter out = response.getWriter();
            out.print(response_json.toString());
            out.flush();
            response.setStatus(201);
        } else {
            inviaErrore(response, 500, "Errore nella registrazione utente");
        }
    }
    
    /**
     * POST /AuthServlet/register-expert
     * Registra un nuovo esperto (crea sia utente che esperto)
     */
    private void gestisciRegistrazioneEsperto(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException, JSONException {
        
        String jsonString = leggiCorpoRichiesta(request);
        RegistrazioneEspertoDTO registrazioneDTO = RegistrazioneEspertoDTO.fromJSON(jsonString);
        
        // Controlla se utente esiste già
        if (utenteDAO.findByEmail(registrazioneDTO.getEmail()) != null) {
            inviaErrore(response, 409, "Un utente con questa email esiste già");
            return;
        }
        
        // Hash della password
        String passwordHash = hashPassword(registrazioneDTO.getPassword());
        
        // 1. Crea utente base (sempre AVANZATO per esperti)
        Utente nuovoUtente = new Utente(
            registrazioneDTO.getEmail(),
            passwordHash,
            LivelloEsperienza.AVANZATO,
            registrazioneDTO.getScopoUso()
        );
        
        int idUtente = utenteDAO.salva(nuovoUtente);
        
        if (idUtente <= 0) {
            inviaErrore(response, 500, "Errore nella creazione utente base");
            return;
        }
        
        // 2. Crea profilo esperto
        Esperto nuovoEsperto = new Esperto(
            idUtente,
            registrazioneDTO.getSpecializzazione(),
            registrazioneDTO.getAnniEsperienza()
        );
        
        int idEsperto = espertoDAO.salva(nuovoEsperto);
        
        if (idEsperto > 0) {
            JSONObject response_json = new JSONObject();
            response_json.put("success", true);
            response_json.put("message", "Esperto registrato con successo");
            response_json.put("idUtente", idUtente);
            response_json.put("idEsperto", idEsperto);
            response_json.put("email", registrazioneDTO.getEmail());
            response_json.put("specializzazione", registrazioneDTO.getSpecializzazione());
            
            PrintWriter out = response.getWriter();
            out.print(response_json.toString());
            out.flush();
            response.setStatus(201);
        } else {
            // Rollback: elimina utente se creazione esperto fallisce
            // In un sistema reale useresti transazioni
            inviaErrore(response, 500, "Errore nella creazione profilo esperto");
        }
    }
    
    /**
     * POST /AuthServlet/login
     * Autentica utente e crea sessione
     */
    private void gestisciLogin(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException, JSONException {
        
        String jsonString = leggiCorpoRichiesta(request);
        JSONObject loginData = new JSONObject(jsonString);
        
        if (!loginData.has("email") || !loginData.has("password")) {
            inviaErrore(response, 400, "Email e password sono obbligatori");
            return;
        }
        
        String email = loginData.getString("email");
        String password = loginData.getString("password");
        
        // Trova utente nel database
        Utente utente = utenteDAO.findByEmail(email);
        
        if (utente == null) {
            inviaErrore(response, 401, "Credenziali non valide");
            return;
        }
        
        // Verifica password con BCrypt
        if (!verificaPassword(password, utente.getPassword())) {
            inviaErrore(response, 401, "Credenziali non valide");
            return;
        }
        
        // Login riuscito: crea sessione
        HttpSession session = request.getSession(true);
        session.setAttribute("user", utente);
        session.setAttribute("userId", utente.getIdUtente());
        session.setAttribute("userEmail", utente.getMail());
        session.setMaxInactiveInterval(sessionTimeout);
        
        // Verifica se è anche un esperto
        Esperto esperto = espertoDAO.findByIdUtente(utente.getIdUtente());
        boolean isEsperto = (esperto != null);
        
        if (isEsperto) {
            session.setAttribute("expert", esperto);
            session.setAttribute("expertId", esperto.getIdEsperto());
        }
        
        // Risposta di successo
        JSONObject response_json = new JSONObject();
        response_json.put("success", true);
        response_json.put("message", "Login effettuato con successo");
        response_json.put("user", createUserResponseData(utente, esperto));
        
        PrintWriter out = response.getWriter();
        out.print(response_json.toString());
        out.flush();
        response.setStatus(200);
    }
    
    /**
     * POST /AuthServlet/logout
     * Distrugge la sessione utente
     * @throws JSONException 
     */
    private void gestisciLogout(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, JSONException {
        
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            session.invalidate();
        }
        
        JSONObject response_json = new JSONObject();
        response_json.put("success", true);
        response_json.put("message", "Logout effettuato con successo");
        
        PrintWriter out = response.getWriter();
        out.print(response_json.toString());
        out.flush();
        response.setStatus(200);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if ("/check-session".equals(pathInfo)) {
            try {
				gestisciCheckSession(request, response);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } else {
            try {
				inviaErrore(response, 405, "Metodo GET supportato solo per /check-session");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
    
    /**
     * GET /AuthServlet/check-session
     * Verifica se utente è loggato
     * @throws JSONException 
     */
    private void gestisciCheckSession(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, JSONException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession(false);
        JSONObject response_json = new JSONObject();
        
        if (session != null && session.getAttribute("user") != null) {
            Utente utente = (Utente) session.getAttribute("user");
            Esperto esperto = (Esperto) session.getAttribute("expert");
            
            response_json.put("loggedIn", true);
            response_json.put("user", createUserResponseData(utente, esperto));
        } else {
            response_json.put("loggedIn", false);
        }
        
        PrintWriter out = response.getWriter();
        out.print(response_json.toString());
        out.flush();
        response.setStatus(200);
    }
    
    // ===== METODI DI UTILITÀ =====
    
    /**
     * Legge il corpo della richiesta HTTP come stringa
     */
    private String leggiCorpoRichiesta(HttpServletRequest request) throws IOException {
        StringBuilder jsonBuffer = new StringBuilder();
        String line;
        
        try (var reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }
        }
        return jsonBuffer.toString();
    }
    
    /**
     * Hash della password con BCrypt
     */
    private String hashPassword(String plainPassword) {
        // Implementa BCrypt - potresti usare la classe dal tuo progetto originale
        // O una libreria come Spring Security BCrypt
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(bcryptRounds));
    }
    
    /**
     * Verifica password con BCrypt
     */
    private boolean verificaPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
    
    /**
     * Crea dati utente per risposta (senza password)
     * @throws JSONException 
     */
    private JSONObject createUserResponseData(Utente utente, Esperto esperto) throws JSONException {
        JSONObject userData = new JSONObject();
        userData.put("id", utente.getIdUtente());
        userData.put("email", utente.getMail());
        userData.put("livelloEsperienza", utente.getLivelloEsperienza().toString());
        userData.put("scopoUso", utente.getScopoUso());
        userData.put("dataRegistrazione", utente.getDataRegistrazione().toString());
        
        if (esperto != null) {
            JSONObject expertData = new JSONObject();
            expertData.put("id", esperto.getIdEsperto());
            expertData.put("specializzazione", esperto.getSpecializzazione());
            expertData.put("anniEsperienza", esperto.getAnniEsperienza());
            expertData.put("feedbackMedio", esperto.getFeedbackMedio());
            expertData.put("numeroValutazioni", esperto.getNumeroValutazioni());
            
            userData.put("isEsperto", true);
            userData.put("profiloEsperto", expertData);
        } else {
            userData.put("isEsperto", false);
        }
        
        return userData;
    }
    
    /**
     * Utility per inviare errori JSON standardizzati
     * @throws JSONException 
     */
    private void inviaErrore(HttpServletResponse response, int statusCode, String messaggio) 
            throws IOException, JSONException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(statusCode);
        
        JSONObject errorJson = new JSONObject();
        errorJson.put("success", false);
        errorJson.put("error", messaggio);
        errorJson.put("statusCode", statusCode);
        
        PrintWriter out = response.getWriter();
        out.print(errorJson.toString());
        out.flush();
    }
}

	
	

	
