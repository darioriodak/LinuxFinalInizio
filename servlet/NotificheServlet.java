package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import dao.*;
import entity.database.*;
import entity.dto.*;
import enumerazioni.*;

/**
 * Servlet per gestione notifiche di utenti ed esperti
 * Endpoints: /esperto, /utente, /{id}/letta, /count
 */

public class NotificheServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // DAO instances
    private NotificaDAO notificaDAO;
    private UtenteDAO utenteDAO;
    private EspertoDAO espertoDAO;
    private RichiestaDAO richiestaDAO;
    private ValutazioneEspertoDAO valutazioneDAO;
    private RichiestaEspertoDAO richiestaEspertoDAO;
    
    // Configurazioni da web.xml
    private int maxNotifichePerPagina;
    private int giorniArchiviazioneAutomatica;
    
    @Override
    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        // Parametri database
        String ip = getInitParameter("ip");
        String port = getInitParameter("port");
        String dbName = getInitParameter("dbName");
        String userName = getInitParameter("userName");
        String password = getInitParameter("password");
        
        // Configurazioni business
        this.maxNotifichePerPagina = Integer.parseInt(getInitParameter("max.notifiche.per.pagina"));
        this.giorniArchiviazioneAutomatica = Integer.parseInt(getInitParameter("giorni.archiviazione.automatica"));
        
        // Inizializza DAO
        this.notificaDAO = new NotificaDAOImpl(ip, port, dbName, userName, password);
        this.utenteDAO = new UtenteDAOImpl(ip, port, dbName, userName, password);
        this.espertoDAO = new EspertoDAOImpl(ip, port, dbName, userName, password);
        this.richiestaDAO = new RichiestaDAOImpl(ip, port, dbName, userName, password);
        this.valutazioneDAO = new ValutazioneEspertoDAOImpl(ip, port, dbName, userName, password);
        this.richiestaEspertoDAO = new RichiestaEspertoDAOImpl(ip, port, dbName, userName, password);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null) {
            inviaErrore(response, 400, "Endpoint mancante");
            return;
        }
        
        try {
            switch (pathInfo) {
                case "/esperto":
                    gestisciGetNotificheEsperto(request, response);
                    break;
                case "/utente":
                    gestisciGetNotificheUtente(request, response);
                    break;
                case "/count":
                    gestisciGetContaNotifiche(request, response);
                    break;
                default:
                    inviaErrore(response, 404, "Endpoint GET non trovato: " + pathInfo);
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            inviaErrore(response, 500, "Errore database: " + e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            inviaErrore(response, 400, "Errore serializzazione JSON: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            inviaErrore(response, 500, "Errore interno del server");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null) {
            inviaErrore(response, 400, "Endpoint mancante");
            return;
        }
        
        try {
            // Pattern per marca come letta: /{id}/letta
            if (pathInfo.matches("^/\\d+/letta$")) {
                gestisciMarcaComeLetta(request, response, pathInfo);
            } else {
                inviaErrore(response, 404, "Endpoint POST non trovato: " + pathInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            inviaErrore(response, 500, "Errore database: " + e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            inviaErrore(response, 400, "JSON malformato: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            inviaErrore(response, 500, "Errore interno del server");
        }
    }
    
    /**
     * GET /NotificheServlet/esperto
     * Recupera notifiche per esperto loggato
     */
    private void gestisciGetNotificheEsperto(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException, JSONException {
        
        Esperto espertoLoggato = getEspertoLoggato(request);
        if (espertoLoggato == null) {
            inviaErrore(response, 401, "Accesso riservato agli esperti - login richiesto");
            return;
        }
        
        // Parametri di paginazione opzionali
        int limit = getParametroInt(request, "limit", maxNotifichePerPagina);
        boolean soloNonLette = getParametroBoolean(request, "nonLette", false);
        
        List<Notifica> notifiche;
        if (soloNonLette) {
            notifiche = notificaDAO.getNotificheNonLetteByEsperto(espertoLoggato.getIdEsperto());
        } else {
            notifiche = notificaDAO.getTutteNotificheByEsperto(espertoLoggato.getIdEsperto());
        }
        
        // Limita risultati se necessario
        if (notifiche.size() > limit) {
            notifiche = notifiche.subList(0, limit);
        }
        
        // Conta notifiche non lette
        int numeroNonLette = notificaDAO.contaNotificheNonLette(espertoLoggato.getIdEsperto());
        
        // Crea DTO per esperti
        NotificheEspertoDTO notificheDTO = new NotificheEspertoDTO();
        notificheDTO.setNumeroNonLette(numeroNonLette);
        
        for (Notifica notifica : notifiche) {
            NotificheEspertoDTO.NotificaSingolaDTO singolaDTO = new NotificheEspertoDTO.NotificaSingolaDTO(
                notifica.getIdNotifica(),
                notifica.getIdRichiesta(),
                notifica.getTitolo()
            );
            singolaDTO.setMessaggio(notifica.getMessaggio());
            singolaDTO.setDataCreazione(notifica.getDataCreazione());
            singolaDTO.setStato(notifica.getStato().toString());
            singolaDTO.setPriorita(notifica.getPriorita().toString());
            singolaDTO.setTipoNotifica(notifica.getTipoNotifica().toString());
            
            notificheDTO.aggiungiNotifica(singolaDTO);
        }
        
        // Risposta
        JSONObject responseJson = new JSONObject();
        responseJson.put("success", true);
        responseJson.put("tipoUtente", "esperto");
        responseJson.put("idEsperto", espertoLoggato.getIdEsperto());
        
        // Parse del JSON dal DTO
        JSONObject notificheJson = new JSONObject(notificheDTO.toJSONString());
        responseJson.put("notifiche", notificheJson);
        
        PrintWriter out = response.getWriter();
        out.print(responseJson.toString());
        out.flush();
        response.setStatus(200);
    }
    
    /**
     * GET /NotificheServlet/utente
     * Recupera notifiche per utente loggato (simulate da richieste)
     */
    private void gestisciGetNotificheUtente(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException, JSONException {
        
        Utente utenteLoggato = getUtenteLoggato(request);
        if (utenteLoggato == null) {
            inviaErrore(response, 401, "Accesso non autorizzato - login richiesto");
            return;
        }
        
        // Parametri di paginazione opzionali
        int limit = getParametroInt(request, "limit", maxNotifichePerPagina);
        
        // Genera notifiche basate sulle richieste dell'utente
        NotificheUtenteDTO notificheDTO = generaNotificheUtente(utenteLoggato, limit);
        
        // Risposta
        JSONObject responseJson = new JSONObject();
        responseJson.put("success", true);
        responseJson.put("tipoUtente", "utente");
        responseJson.put("idUtente", utenteLoggato.getIdUtente());
        
        // Parse del JSON dal DTO
        JSONObject notificheJson = new JSONObject(notificheDTO.toJSONString());
        responseJson.put("notifiche", notificheJson);
        
        PrintWriter out = response.getWriter();
        out.print(responseJson.toString());
        out.flush();
        response.setStatus(200);
    }
    
    /**
     * GET /NotificheServlet/count
     * Conta notifiche non lette per utente loggato
     */
    private void gestisciGetContaNotifiche(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException, JSONException {
        
        // Determina se è utente o esperto
        Esperto espertoLoggato = getEspertoLoggato(request);
        Utente utenteLoggato = getUtenteLoggato(request);
        
        if (espertoLoggato == null && utenteLoggato == null) {
            inviaErrore(response, 401, "Accesso non autorizzato - login richiesto");
            return;
        }
        
        int numeroNonLette = 0;
        String tipoUtente = "";
        
        if (espertoLoggato != null) {
            numeroNonLette = notificaDAO.contaNotificheNonLette(espertoLoggato.getIdEsperto());
            tipoUtente = "esperto";
        } else if (utenteLoggato != null) {
            // Per gli utenti, conta in base alle richieste attive
            List<Richiesta> richieste = richiestaDAO.findByUtente(utenteLoggato.getIdUtente());
            numeroNonLette = contaNotificheUtenteNonLette(richieste);
            tipoUtente = "utente";
        }
        
        // Risposta
        JSONObject responseJson = new JSONObject();
        responseJson.put("success", true);
        responseJson.put("tipoUtente", tipoUtente);
        responseJson.put("numeroNonLette", numeroNonLette);
        responseJson.put("timestamp", System.currentTimeMillis());
        
        PrintWriter out = response.getWriter();
        out.print(responseJson.toString());
        out.flush();
        response.setStatus(200);
    }
    
    /**
     * POST /NotificheServlet/{id}/letta
     * Marca una notifica come letta
     */
    private void gestisciMarcaComeLetta(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws SQLException, IOException, JSONException {
        
        try {
            // Estrae ID da /{id}/letta
            String[] parts = pathInfo.split("/");
            int idNotifica = Integer.parseInt(parts[1]);
            
            // Verifica autorizzazione (solo esperto per ora, gli utenti non hanno notifiche dirette nel DB)
            Esperto espertoLoggato = getEspertoLoggato(request);
            if (espertoLoggato == null) {
                inviaErrore(response, 401, "Accesso riservato agli esperti");
                return;
            }
            
            // Verifica che la notifica appartenga all'esperto
            List<Notifica> notificheEsperto = notificaDAO.getTutteNotificheByEsperto(espertoLoggato.getIdEsperto());
            boolean notificaTrovata = notificheEsperto.stream()
                .anyMatch(n -> n.getIdNotifica() == idNotifica);
            
            if (!notificaTrovata) {
                inviaErrore(response, 403, "Accesso negato a questa notifica");
                return;
            }
            
            // Marca come letta
            notificaDAO.marcaComeLetta(idNotifica);
            
            // Risposta di successo
            JSONObject responseJson = new JSONObject();
            responseJson.put("success", true);
            responseJson.put("message", "Notifica marcata come letta");
            responseJson.put("idNotifica", idNotifica);
            responseJson.put("timestamp", System.currentTimeMillis());
            
            PrintWriter out = response.getWriter();
            out.print(responseJson.toString());
            out.flush();
            response.setStatus(200);
            
        } catch (NumberFormatException e) {
            inviaErrore(response, 400, "ID notifica non valido");
        }
    }
    
    // ===== METODI DI UTILITÀ =====
    
    /**
     * Recupera esperto loggato dalla sessione
     */
    private Esperto getEspertoLoggato(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (Esperto) session.getAttribute("expert");
        }
        return null;
    }
    
    /**
     * Recupera utente loggato dalla sessione
     */
    private Utente getUtenteLoggato(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (Utente) session.getAttribute("user");
        }
        return null;
    }
    
    /**
     * Recupera parametro intero dalla richiesta con valore default
     */
    private int getParametroInt(HttpServletRequest request, String nomeParametro, int defaultValue) {
        String param = request.getParameter(nomeParametro);
        if (param != null) {
            try {
                return Integer.parseInt(param);
            } catch (NumberFormatException e) {
                // Return default se non parsabile
            }
        }
        return defaultValue;
    }
    
    /**
     * Recupera parametro boolean dalla richiesta con valore default
     */
    private boolean getParametroBoolean(HttpServletRequest request, String nomeParametro, boolean defaultValue) {
        String param = request.getParameter(nomeParametro);
        if (param != null) {
            return "true".equalsIgnoreCase(param) || "1".equals(param);
        }
        return defaultValue;
    }
    
    /**
     * Genera notifiche simulate per utenti basate sulle loro richieste
     */
    private NotificheUtenteDTO generaNotificheUtente(Utente utente, int limit) throws SQLException {
        NotificheUtenteDTO notificheDTO = new NotificheUtenteDTO();
        
        List<Richiesta> richieste = richiestaDAO.findByUtente(utente.getIdUtente());
        
        // Limita le richieste se necessario
        if (richieste.size() > limit) {
            richieste = richieste.subList(0, limit);
        }
        
        int notificheNonLette = 0;
        
        for (Richiesta richiesta : richieste) {
            // Conta esperti assegnati e valutazioni ricevute
            int espertiTotali = richiestaEspertoDAO.contaEspertiByRichiesta(richiesta.getIdRichiesta());
            int valutazioniRicevute = valutazioneDAO.contaValutazioni(richiesta.getIdRichiesta());
            
            // Determina tipo di notifica in base allo stato
            TipoNotificaUtente tipoNotifica;
            String titolo;
            String messaggio;
            StatoNotificaLettura stato = StatoNotificaLettura.NON_LETTA;
            
            if (richiesta.getStatoRichiesta() == StatoRichiesta.COMPLETATA) {
                tipoNotifica = TipoNotificaUtente.VALUTAZIONE_COMPLETATA;
                titolo = "Valutazione completata!";
                messaggio = "Tutti gli esperti hanno valutato la tua richiesta. Visualizza i risultati.";
                stato = StatoNotificaLettura.LETTA; // Assumiamo che siano state lette
            } else if (valutazioniRicevute > 0 && valutazioniRicevute < espertiTotali) {
                tipoNotifica = TipoNotificaUtente.VALUTAZIONE_PARZIALE;
                titolo = "Nuove valutazioni ricevute";
                messaggio = String.format("Ricevute %d/%d valutazioni. In attesa degli altri esperti.", 
                                        valutazioniRicevute, espertiTotali);
                notificheNonLette++;
            } else if (valutazioniRicevute == 1) {
                tipoNotifica = TipoNotificaUtente.PRIMA_VALUTAZIONE;
                titolo = "Prima valutazione ricevuta!";
                messaggio = "Il primo esperto ha valutato la tua richiesta.";
                notificheNonLette++;
            } else {
                tipoNotifica = TipoNotificaUtente.RICHIESTA_INVIATA;
                titolo = "Richiesta inviata con successo";
                messaggio = String.format("La tua richiesta è stata assegnata a %d esperti.", espertiTotali);
                stato = StatoNotificaLettura.LETTA;
            }
            
            // Crea DTO notifica singola
            NotificheUtenteDTO.NotificaSingolaUtenteDTO singolaDTO = new NotificheUtenteDTO.NotificaSingolaUtenteDTO(
                richiesta.getIdRichiesta(), // Usa ID richiesta come ID notifica per semplicità
                richiesta.getIdRichiesta(),
                tipoNotifica.toString(),
                titolo
            );
            
            singolaDTO.setMessaggio(messaggio);
            singolaDTO.setDataCreazione(richiesta.getDataOrarioCreazione());
            singolaDTO.setStato(stato.toString());
            singolaDTO.setPriorita(PrioritaNotifica.NORMALE.toString());
            
            // Progresso valutazioni
            if (espertiTotali > 0) {
                NotificheUtenteDTO.ProgressoValutazioniDTO progresso = 
                    new NotificheUtenteDTO.ProgressoValutazioniDTO(valutazioniRicevute, espertiTotali);
                singolaDTO.setProgressoValutazioni(progresso);
            }
            
            // Link azione se appropriato
            if (richiesta.getStatoRichiesta() == StatoRichiesta.COMPLETATA) {
                singolaDTO.setLinkAzione("/RichiesteServlet/" + richiesta.getIdRichiesta() + "/risultati");
                singolaDTO.setTestoAzione("Visualizza risultati");
            } else if (valutazioniRicevute > 0) {
                singolaDTO.setLinkAzione("/RichiesteServlet/" + richiesta.getIdRichiesta());
                singolaDTO.setTestoAzione("Visualizza progresso");
            }
            
            notificheDTO.aggiungiNotifica(singolaDTO);
        }
        
        notificheDTO.setNumeroNonLette(notificheNonLette);
        notificheDTO.setNumeroTotali(richieste.size());
        
        return notificheDTO;
    }
    
    /**
     * Conta notifiche non lette per utente (simulazione)
     */
    private int contaNotificheUtenteNonLette(List<Richiesta> richieste) throws SQLException {
        int count = 0;
        
        for (Richiesta richiesta : richieste) {
            if (richiesta.getStatoRichiesta() == StatoRichiesta.IN_VALUTAZIONE) {
                int valutazioniRicevute = valutazioneDAO.contaValutazioni(richiesta.getIdRichiesta());
                if (valutazioniRicevute > 0) {
                    count++; // Nuove valutazioni non ancora "lette"
                }
            }
        }
        
        return count;
    }
    
    /**
     * Utility per inviare errori JSON standardizzati
     */
    private void inviaErrore(HttpServletResponse response, int statusCode, String messaggio) 
            throws IOException {
        
        try {
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
            
        } catch (JSONException e) {
            e.printStackTrace();
            response.getWriter().print("{\"success\":false,\"error\":\"Errore interno JSON\"}");
        }
    }
}
