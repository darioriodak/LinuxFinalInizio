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

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import dao.*;
import entity.database.*;
import entity.dto.*;
import enumerazioni.*;

/**
 * Servlet per gestione valutazioni degli esperti
 * Endpoints: /richieste-pending, /richiesta/{id}, /invia, /mie-completate
 */
@WebServlet("/ValutazioniServlet/*")
public class ValutazioniServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // DAO instances
    private RichiestaDAO richiestaDAO;
    private RichiestaEspertoDAO richiestaEspertoDAO;
    private RichiestaDistribuzioneDAO richiestaDistribuzioneDAO;
    private ValutazioneEspertoDAO valutazioneDAO;
    private EspertoDAO espertoDAO;
    private DistroDAO distroDAO;
    private UtenteDAO utenteDAO;
    private ProfiloHardwareDAO profiloHardwareDAO;
    private NotificaDAO notificaDAO;
    
    // Configurazioni da web.xml
    private double punteggioMinimo;
    private double punteggioMassimo;
    private int commentoMinimoCaratteri;
    
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
        this.punteggioMinimo = Double.parseDouble(getInitParameter("punteggio.minimo"));
        this.punteggioMassimo = Double.parseDouble(getInitParameter("punteggio.massimo"));
        this.commentoMinimoCaratteri = Integer.parseInt(getInitParameter("commento.minimo.caratteri"));
        
        // Inizializza DAO
        this.richiestaDAO = new RichiestaDAOImpl(ip, port, dbName, userName, password);
        this.richiestaEspertoDAO = new RichiestaEspertoDAOImpl(ip, port, dbName, userName, password);
        this.richiestaDistribuzioneDAO = new RichiestaDistribuzioneDAOImpl(ip, port, dbName, userName, password);
        this.valutazioneDAO = new ValutazioneEspertoDAOImpl(ip, port, dbName, userName, password);
        this.espertoDAO = new EspertoDAOImpl(ip, port, dbName, userName, password);
        this.distroDAO = new DistroDAOImpl(ip, port, dbName, userName, password);
        this.utenteDAO = new UtenteDAOImpl(ip, port, dbName, userName, password);
        this.profiloHardwareDAO = new ProfiloHardwareDAOImpl(ip, port, dbName, userName, password);
        this.notificaDAO = new NotificaDAOImpl(ip, port, dbName, userName, password);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Verifica che sia un esperto autenticato
        Esperto espertoLoggato = getEspertoLoggato(request);
        if (espertoLoggato == null) {
            inviaErrore(response, 401, "Accesso riservato agli esperti - login richiesto");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null) {
            inviaErrore(response, 400, "Endpoint mancante");
            return;
        }
        
        try {
            switch (pathInfo) {
                case "/invia":
                    gestisciInviaValutazione(request, response, espertoLoggato);
                    break;
                default:
                    inviaErrore(response, 404, "Endpoint POST non trovato: " + pathInfo);
                    break;
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
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Verifica che sia un esperto autenticato
        Esperto espertoLoggato = getEspertoLoggato(request);
        if (espertoLoggato == null) {
            inviaErrore(response, 401, "Accesso riservato agli esperti - login richiesto");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null) {
            inviaErrore(response, 400, "Endpoint mancante");
            return;
        }
        
        try {
            switch (pathInfo) {
                case "/richieste-pending":
                    gestisciGetRichiestePending(request, response, espertoLoggato);
                    break;
                case "/mie-completate":
                    gestisciGetValutazioniCompletate(request, response, espertoLoggato);
                    break;
                default:
                    // Pattern per richiesta specifica: /richiesta/{id}
                    if (pathInfo.matches("^/richiesta/\\d+$")) {
                        gestisciGetDettagliRichiesta(request, response, espertoLoggato, pathInfo);
                    } else {
                        inviaErrore(response, 404, "Endpoint GET non trovato: " + pathInfo);
                    }
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
    
    /**
     * POST /ValutazioniServlet/invia
     * Invia valutazioni dell'esperto per una richiesta
     */
    private void gestisciInviaValutazione(HttpServletRequest request, HttpServletResponse response,
            Esperto espertoLoggato) throws SQLException, IOException, JSONException {
        
        String jsonString = leggiCorpoRichiesta(request);
        ValutazioneEspertoDTO valutazioneDTO = ValutazioneEspertoDTO.fromJSON(jsonString, espertoLoggato.getIdEsperto());
        
        // Verifica che l'esperto sia assegnato a questa richiesta
        if (!richiestaEspertoDAO.esisteAssegnazione(valutazioneDTO.getIdRichiesta(), espertoLoggato.getIdEsperto())) {
            inviaErrore(response, 403, "Non sei assegnato a questa richiesta");
            return;
        }
        
        // Verifica che non abbia già valutato
        if (valutazioneDAO.haValutatoEsperto(espertoLoggato.getIdEsperto(), valutazioneDTO.getIdRichiesta())) {
            inviaErrore(response, 409, "Hai già valutato questa richiesta");
            return;
        }
        
        // Verifica che la richiesta sia in stato valutabile
        Richiesta richiesta = richiestaDAO.findById(valutazioneDTO.getIdRichiesta());
        if (richiesta == null || richiesta.getStatoRichiesta() != StatoRichiesta.IN_VALUTAZIONE) {
            inviaErrore(response, 400, "Richiesta non più valutabile");
            return;
        }
        
        // Validazioni dei punteggi
        for (ValutazioneEspertoDTO.ValutazioneSingolaDistro valutazione : valutazioneDTO.getValutazioni()) {
            if (valutazione.getPunteggio() < punteggioMinimo || valutazione.getPunteggio() > punteggioMassimo) {
                inviaErrore(response, 400, "Punteggio deve essere tra " + punteggioMinimo + " e " + punteggioMassimo);
                return;
            }
            
            if (valutazione.getSuggerimento() != null && 
                valutazione.getSuggerimento().length() < commentoMinimoCaratteri) {
                inviaErrore(response, 400, "Suggerimento deve essere di almeno " + commentoMinimoCaratteri + " caratteri");
                return;
            }
        }
        
        // Salva tutte le valutazioni
        int valutazioniSalvate = 0;
        for (ValutazioneEspertoDTO.ValutazioneSingolaDistro valutazione : valutazioneDTO.getValutazioni()) {
            // Verifica che la distribuzione sia effettivamente nella richiesta
            if (!richiestaDistribuzioneDAO.esisteAssociazione(valutazioneDTO.getIdRichiesta(), valutazione.getIdDistribuzione())) {
                continue; // Skip distribuzioni non associate
            }
            
            ValutazioneEsperto valutazioneEntity = new ValutazioneEsperto(
                valutazioneDTO.getIdRichiesta(),
                valutazione.getIdDistribuzione(),
                espertoLoggato.getIdEsperto(),
                valutazione.getPunteggio()
            );
            valutazioneEntity.setSuggerimento(valutazione.getSuggerimento());
            valutazioneEntity.setMotivazione(valutazione.getMotivazione());
            
            int idValutazione = valutazioneDAO.salva(valutazioneEntity);
            if (idValutazione > 0) {
                valutazioniSalvate++;
            }
        }
        
        if (valutazioniSalvate == 0) {
            inviaErrore(response, 500, "Nessuna valutazione salvata correttamente");
            return;
        }
        
        // Aggiorna stato notifica come completata
        List<RichiestaEsperto> richiesteEsperto = richiestaEspertoDAO.getByRichiesta(valutazioneDTO.getIdRichiesta());
        for (RichiestaEsperto re : richiesteEsperto) {
            if (re.getIdEsperto() == espertoLoggato.getIdEsperto()) {
                re.setStatoNotifica(StatoNotifica.LETTA);
                re.setDataLettura(new java.sql.Timestamp(System.currentTimeMillis()));
                richiestaEspertoDAO.aggiorna(re);
                break;
            }
        }
        
        // Controlla se tutti gli esperti hanno completato la valutazione
        verificaCompletamentoRichiesta(valutazioneDTO.getIdRichiesta());
        
        // Risposta di successo
        JSONObject responseJson = new JSONObject();
        responseJson.put("success", true);
        responseJson.put("message", "Valutazioni inviate con successo");
        responseJson.put("idRichiesta", valutazioneDTO.getIdRichiesta());
        responseJson.put("valutazioniSalvate", valutazioniSalvate);
        
        PrintWriter out = response.getWriter();
        out.print(responseJson.toString());
        out.flush();
        response.setStatus(201);
    }
    
    /**
     * GET /ValutazioniServlet/richieste-pending
     * Recupera richieste in attesa di valutazione dall'esperto
     */
    private void gestisciGetRichiestePending(HttpServletRequest request, HttpServletResponse response,
            Esperto espertoLoggato) throws SQLException, IOException, JSONException {
        
        List<RichiestaEsperto> richiesteAssegnate = richiestaEspertoDAO.getByEsperto(espertoLoggato.getIdEsperto());
        
        JSONObject responseJson = new JSONObject();
        responseJson.put("success", true);
        
        JSONArray richiestePendingArray = new JSONArray();
        
        for (RichiestaEsperto re : richiesteAssegnate) {
            // Controlla se già valutata
            if (valutazioneDAO.haValutatoEsperto(espertoLoggato.getIdEsperto(), re.getIdRichiesta())) {
                continue; // Skip già valutate
            }
            
            Richiesta richiesta = richiestaDAO.findById(re.getIdRichiesta());
            if (richiesta == null || richiesta.getStatoRichiesta() != StatoRichiesta.IN_VALUTAZIONE) {
                continue; // Skip non più valutabili
            }
            
            JSONObject richiestaJson = new JSONObject();
            richiestaJson.put("idRichiesta", richiesta.getIdRichiesta());
            richiestaJson.put("dataCreazione", richiesta.getDataOrarioCreazione().toString());
            richiestaJson.put("dataAssegnazione", re.getDataAssegnazione().toString());
            richiestaJson.put("statoNotifica", re.getStatoNotifica().toString());
            
            if (richiesta.getScadenza() != null) {
                richiestaJson.put("scadenza", richiesta.getScadenza().toString());
            }
            
            // Informazioni utente anonimizzate
            Utente utente = utenteDAO.findById(richiesta.getIdUtente());
            if (utente != null) {
                JSONObject infoUtenteJson = new JSONObject();
                infoUtenteJson.put("livelloEsperienza", utente.getLivelloEsperienza().toString());
                infoUtenteJson.put("scopoUso", utente.getScopoUso());
                richiestaJson.put("infoUtente", infoUtenteJson);
            }
            
            // Conta distribuzioni da valutare
            int numeroDistribuzioni = richiestaDistribuzioneDAO.getByRichiesta(richiesta.getIdRichiesta()).size();
            richiestaJson.put("numeroDistribuzioniDaValutare", numeroDistribuzioni);
            
            richiestePendingArray.put(richiestaJson);
        }
        
        responseJson.put("count", richiestePendingArray.length());
        responseJson.put("richiestePending", richiestePendingArray);
        
        PrintWriter out = response.getWriter();
        out.print(responseJson.toString());
        out.flush();
        response.setStatus(200);
    }
    
    /**
     * GET /ValutazioniServlet/richiesta/{id}
     * Recupera dettagli completi di una richiesta per valutazione
     */
    private void gestisciGetDettagliRichiesta(HttpServletRequest request, HttpServletResponse response,
            Esperto espertoLoggato, String pathInfo) throws SQLException, IOException, JSONException {
        
        try {
            // Estrae ID da /richiesta/{id}
            String[] parts = pathInfo.split("/");
            int idRichiesta = Integer.parseInt(parts[2]);
            
            // Verifica assegnazione
            if (!richiestaEspertoDAO.esisteAssegnazione(idRichiesta, espertoLoggato.getIdEsperto())) {
                inviaErrore(response, 403, "Non sei assegnato a questa richiesta");
                return;
            }
            
            Richiesta richiesta = richiestaDAO.findById(idRichiesta);
            if (richiesta == null) {
                inviaErrore(response, 404, "Richiesta non trovata");
                return;
            }
            
            // Genera DettagliRichiestaPerEspertoDTO
            DettagliRichiestaPerEspertoDTO dettagli = generaDettagliPerEsperto(richiesta, espertoLoggato);
            
            JSONObject responseJson = new JSONObject();
            responseJson.put("success", true);
            
            // Parse del JSON dal DTO
            JSONObject dettagliJson = new JSONObject(dettagli.toJSONString());
            responseJson.put("dettagliRichiesta", dettagliJson);
            
            PrintWriter out = response.getWriter();
            out.print(responseJson.toString());
            out.flush();
            response.setStatus(200);
            
        } catch (NumberFormatException e) {
            inviaErrore(response, 400, "ID richiesta non valido");
        }
    }
    
    /**
     * GET /ValutazioniServlet/mie-completate
     * Recupera valutazioni già completate dall'esperto
     */
    private void gestisciGetValutazioniCompletate(HttpServletRequest request, HttpServletResponse response,
            Esperto espertoLoggato) throws SQLException, IOException, JSONException {
        
        List<RichiestaEsperto> richiesteAssegnate = richiestaEspertoDAO.getByEsperto(espertoLoggato.getIdEsperto());
        
        JSONObject responseJson = new JSONObject();
        responseJson.put("success", true);
        
        JSONArray valutazioniCompletateArray = new JSONArray();
        
        for (RichiestaEsperto re : richiesteAssegnate) {
            // Solo quelle già valutate
            if (!valutazioneDAO.haValutatoEsperto(espertoLoggato.getIdEsperto(), re.getIdRichiesta())) {
                continue;
            }
            
            Richiesta richiesta = richiestaDAO.findById(re.getIdRichiesta());
            if (richiesta == null) continue;
            
            JSONObject valutazioneJson = new JSONObject();
            valutazioneJson.put("idRichiesta", richiesta.getIdRichiesta());
            valutazioneJson.put("dataCreazione", richiesta.getDataOrarioCreazione().toString());
            valutazioneJson.put("statoRichiesta", richiesta.getStatoRichiesta().toString());
            
            if (re.getDataLettura() != null) {
                valutazioneJson.put("dataValutazione", re.getDataLettura().toString());
            }
            
            // Conta valutazioni date
            List<ValutazioneEsperto> valutazioni = valutazioneDAO.getByRichiestaEEsperto(
                richiesta.getIdRichiesta(), espertoLoggato.getIdEsperto()
            );
            valutazioneJson.put("numeroDistribuzioniValutate", valutazioni.size());
            
            // Punteggio medio dato dall'esperto
            double punteggioMedio = valutazioni.stream()
                .mapToDouble(ValutazioneEsperto::getPunteggio)
                .average()
                .orElse(0.0);
            valutazioneJson.put("punteggioMedioAssegnato", punteggioMedio);
            
            valutazioniCompletateArray.put(valutazioneJson);
        }
        
        responseJson.put("count", valutazioniCompletateArray.length());
        responseJson.put("valutazioniCompletate", valutazioniCompletateArray);
        
        PrintWriter out = response.getWriter();
        out.print(responseJson.toString());
        out.flush();
        response.setStatus(200);
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
     * Legge il corpo della richiesta HTTP
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
     * Genera DettagliRichiestaPerEspertoDTO completo
     */
    private DettagliRichiestaPerEspertoDTO generaDettagliPerEsperto(Richiesta richiesta, Esperto espertoLoggato) 
            throws SQLException {
        
        DettagliRichiestaPerEspertoDTO dettagli = new DettagliRichiestaPerEspertoDTO(
            richiesta.getIdRichiesta(),
            richiesta.getDataOrarioCreazione(),
            richiesta.getStatoRichiesta().toString()
        );
        
        dettagli.setScadenza(richiesta.getScadenza());
        dettagli.setModalitaSelezione(richiesta.getModalitaSelezione().toString());
        dettagli.setNoteAggiuntive(richiesta.getNoteAggiuntive());
        
        // Verifica se già valutata
        boolean giaValutata = valutazioneDAO.haValutatoEsperto(espertoLoggato.getIdEsperto(), richiesta.getIdRichiesta());
        dettagli.setGiaValutata(giaValutata);
        
        // Informazioni utente anonimizzate
        Utente utente = utenteDAO.findById(richiesta.getIdUtente());
        if (utente != null) {
            InfoUtenteAnonimizzataDTO infoUtente = new InfoUtenteAnonimizzataDTO(
                utente.getLivelloEsperienza().toString(),
                utente.getScopoUso()
            );
            infoUtente.setEtaApprossimativa("Non specificata"); // Privacy
            dettagli.setInfoUtente(infoUtente);
        }
        
        // Hardware (se presente)
        if (richiesta.getIdHardware() != null) {
            ProfiloHardware hardware = profiloHardwareDAO.findById(richiesta.getIdHardware());
            if (hardware != null) {
                HardwareInfoDTO hardwareDTO = new HardwareInfoDTO(
                    hardware.getCpu(),
                    hardware.getRam(),
                    hardware.getSpazioArchiviazione()
                );
                hardwareDTO.setSchedaVideo(hardware.getSchedaVideo());
                hardwareDTO.setTipoSistema(hardware.getTipoSistema() != null ? hardware.getTipoSistema().toString() : null);
                dettagli.setHardware(hardwareDTO);
            }
        }
        
        // Distribuzioni da valutare
        List<RichiestaDistribuzione> distribuzioni = richiestaDistribuzioneDAO.getByRichiesta(richiesta.getIdRichiesta());
        for (RichiestaDistribuzione rd : distribuzioni) {
            DistribuzioneCompletaDTO distroDTO = distroDAO.getDistribuzioneCompletaById(rd.getIdDistribuzione());
            if (distroDTO != null) {
                dettagli.aggiungiDistribuzione(distroDTO);
            }
        }
        
        // Altri esperti (per trasparenza)
        List<RichiestaEsperto> altriEsperti = richiestaEspertoDAO.getByRichiesta(richiesta.getIdRichiesta());
        for (RichiestaEsperto re : altriEsperti) {
            if (re.getIdEsperto() == espertoLoggato.getIdEsperto()) {
                continue; // Skip se stesso
            }
            
            Esperto altroEsperto = espertoDAO.findById(re.getIdEsperto());
            if (altroEsperto != null) {
                EspertoCollegaDTO collegaDTO = new EspertoCollegaDTO(
                    "Esperto_" + altroEsperto.getIdEsperto(), // Anonimizzato
                    altroEsperto.getSpecializzazione()
                );
                
                boolean haValutato = valutazioneDAO.haValutatoEsperto(altroEsperto.getIdEsperto(), richiesta.getIdRichiesta());
                collegaDTO.setHaValutato(haValutato);
                
                dettagli.aggiungiEspertoCollega(collegaDTO);
            }
        }
        
        return dettagli;
    }
    
    /**
     * Verifica se tutti gli esperti hanno completato la valutazione
     */
    private void verificaCompletamentoRichiesta(int idRichiesta) throws SQLException {
        List<RichiestaEsperto> espertiAssegnati = richiestaEspertoDAO.getByRichiesta(idRichiesta);
        
        boolean tuttiHannoValutato = true;
        for (RichiestaEsperto re : espertiAssegnati) {
            if (!valutazioneDAO.haValutatoEsperto(re.getIdEsperto(), idRichiesta)) {
                tuttiHannoValutato = false;
                break;
            }
        }
        
        if (tuttiHannoValutato) {
            richiestaDAO.aggiornaStato(idRichiesta, StatoRichiesta.COMPLETATA);
        }
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
