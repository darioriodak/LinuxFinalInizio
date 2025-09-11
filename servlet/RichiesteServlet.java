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
import java.sql.Timestamp;
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
 * Servlet per gestione richieste utente
 * Endpoints: /nuova, /mie, /{id}, /{id}/risultati
 */
@WebServlet("/RichiesteServlet/*")
public class RichiesteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // DAO instances
    private RichiestaDAO richiestaDAO;
    private RichiestaDistribuzioneDAO richiestaDistribuzioneDAO;
    private RichiestaEspertoDAO richiestaEspertoDAO;
    private EspertoDAO espertoDAO;
    private DistroDAO distroDAO;
    private ProfiloHardwareDAO profiloHardwareDAO;
    private NotificaDAO notificaDAO;
    private ValutazioneEspertoDAO valutazioneDAO;
    private UtenteDAO utenteDAO;
    
    // Configurazioni da web.xml
    private int maxDistribuzioniPerRichiesta;
    private int maxEspertiPerRichiesta;
    private int scadenzaDefaultGiorni;
    
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
        this.maxDistribuzioniPerRichiesta = Integer.parseInt(getInitParameter("max.distribuzioni.per.richiesta"));
        this.maxEspertiPerRichiesta = Integer.parseInt(getInitParameter("max.esperti.per.richiesta"));
        this.scadenzaDefaultGiorni = Integer.parseInt(getInitParameter("scadenza.default.giorni"));
        
        // Inizializza DAO
        this.richiestaDAO = new RichiestaDAOImpl(ip, port, dbName, userName, password);
        this.richiestaDistribuzioneDAO = new RichiestaDistribuzioneDAOImpl(ip, port, dbName, userName, password);
        this.richiestaEspertoDAO = new RichiestaEspertoDAOImpl(ip, port, dbName, userName, password);
        this.espertoDAO = new EspertoDAOImpl(ip, port, dbName, userName, password);
        this.distroDAO = new DistroDAOImpl(ip, port, dbName, userName, password);
        this.profiloHardwareDAO = new ProfiloHardwareDAOImpl(ip, port, dbName, userName, password);
        this.notificaDAO = new NotificaDAOImpl(ip, port, dbName, userName, password);
        this.valutazioneDAO = new ValutazioneEspertoDAOImpl(ip, port, dbName, userName, password);
        this.utenteDAO = new UtenteDAOImpl(ip, port, dbName, userName, password);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Verifica autenticazione
        Utente utenteLoggato = getUtenteLoggato(request);
        if (utenteLoggato == null) {
            inviaErrore(response, 401, "Accesso non autorizzato - login richiesto");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null) {
            inviaErrore(response, 400, "Endpoint mancante");
            return;
        }
        
        try {
            switch (pathInfo) {
                case "/nuova":
                    gestisciNuovaRichiesta(request, response, utenteLoggato);
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
        
        // Verifica autenticazione
        Utente utenteLoggato = getUtenteLoggato(request);
        if (utenteLoggato == null) {
            inviaErrore(response, 401, "Accesso non autorizzato - login richiesto");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null) {
            inviaErrore(response, 400, "Endpoint mancante");
            return;
        }
        
        try {
            switch (pathInfo) {
                case "/mie":
                    gestisciGetRichiesteMie(request, response, utenteLoggato);
                    break;
                default:
                    // Pattern per richieste specifiche: /{id} o /{id}/risultati
                    if (pathInfo.matches("^/\\d+$")) {
                        gestisciGetRichiestaSingola(request, response, utenteLoggato, pathInfo);
                    } else if (pathInfo.matches("^/\\d+/risultati$")) {
                        gestisciGetRisultatiRichiesta(request, response, utenteLoggato, pathInfo);
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
     * POST /RichiesteServlet/nuova
     * Crea una nuova richiesta di consulenza
     */
    private void gestisciNuovaRichiesta(HttpServletRequest request, HttpServletResponse response, 
            Utente utenteLoggato) throws SQLException, IOException, JSONException {
        
        String jsonString = leggiCorpoRichiesta(request);
        NuovaRichiestaDTO richiestaDTO = NuovaRichiestaDTO.RichiestafromJSON(jsonString);
        
        // Validazioni business
        if (richiestaDTO.getDistribuzioniCandidate().size() > maxDistribuzioniPerRichiesta) {
            inviaErrore(response, 400, "Massimo " + maxDistribuzioniPerRichiesta + " distribuzioni per richiesta");
            return;
        }
        
        if ("MANUALE".equals(richiestaDTO.getModalitaSelezione()) && 
            richiestaDTO.getEspertiSelezionati().size() > maxEspertiPerRichiesta) {
            inviaErrore(response, 400, "Massimo " + maxEspertiPerRichiesta + " esperti per richiesta");
            return;
        }
        
        // 1. CREA RICHIESTA PRINCIPALE
        Richiesta richiesta = new Richiesta(
            utenteLoggato.getIdUtente(),
            ModalitaSelezione.valueOf(richiestaDTO.getModalitaSelezione())
        );
        richiesta.setNoteAggiuntive(richiestaDTO.getNoteAggiuntive());
        
        // Imposta scadenza (7 giorni default)
        long scadenzaMs = System.currentTimeMillis() + (scadenzaDefaultGiorni * 24L * 60L * 60L * 1000L);
        richiesta.setScadenza(new Timestamp(scadenzaMs));
        
        int idRichiesta = richiestaDAO.salva(richiesta);
        if (idRichiesta <= 0) {
            inviaErrore(response, 500, "Errore nella creazione della richiesta");
            return;
        }
        
        // 2. SALVA PROFILO HARDWARE (opzionale)
        if (hasProfiloHardware(richiestaDTO)) {
            ProfiloHardware profilo = creaProfiloHardware(richiestaDTO, utenteLoggato.getIdUtente());
            int idHardware = profiloHardwareDAO.salva(profilo);
            
            if (idHardware > 0) {
                richiesta.setIdHardware(idHardware);
                richiestaDAO.aggiorna(richiesta);
            }
        }
        
        // 3. ASSOCIA DISTRIBUZIONI CANDIDATE
        for (DistribuzioneSelezionataDTO distroDTO : richiestaDTO.getDistribuzioniCandidate()) {
            if (!distroDAO.esisteDistribuzione(distroDTO.getId())) {
                inviaErrore(response, 400, "Distribuzione con ID " + distroDTO.getId() + " non trovata");
                return;
            }
            
            RichiestaDistribuzione rd = new RichiestaDistribuzione(idRichiesta, distroDTO.getId());
            richiestaDistribuzioneDAO.salva(rd);
        }
        
        // 4. SELEZIONA E ASSEGNA ESPERTI
        List<Integer> espertiIds = selezionaEsperti(richiestaDTO);
        
        if (espertiIds.isEmpty()) {
            inviaErrore(response, 400, "Nessun esperto disponibile o selezionato");
            return;
        }
        
        for (Integer idEsperto : espertiIds) {
            if (!espertoDAO.esisteEsperto(idEsperto)) {
                continue; // Skip esperti non validi
            }
            
            RichiestaEsperto re = new RichiestaEsperto(idRichiesta, idEsperto);
            richiestaEspertoDAO.salva(re);
        }
        
        // 5. INVIA NOTIFICHE AGLI ESPERTI
        for (Integer idEsperto : espertiIds) {
            Notifica notifica = new Notifica(
                idEsperto,
                idRichiesta,
                TipoNotifica.NUOVA_RICHIESTA,
                "Nuova richiesta di consulenza disponibile"
            );
            notifica.setMessaggio(generaMessaggioNotifica(richiesta, utenteLoggato));
            notificaDAO.salva(notifica);
        }
        
        // 6. AGGIORNA STATO RICHIESTA
        richiestaDAO.aggiornaStato(idRichiesta, StatoRichiesta.IN_VALUTAZIONE);
        
        // Risposta di successo
        JSONObject responseJson = new JSONObject();
        responseJson.put("success", true);
        responseJson.put("message", "Richiesta creata con successo");
        responseJson.put("idRichiesta", idRichiesta);
        responseJson.put("espertiAssegnati", espertiIds.size());
        responseJson.put("distribuzioni", richiestaDTO.getDistribuzioniCandidate().size());
        
        PrintWriter out = response.getWriter();
        out.print(responseJson.toString());
        out.flush();
        response.setStatus(201);
    }
    
    /**
     * GET /RichiesteServlet/mie
     * Recupera tutte le richieste dell'utente loggato
     */
    private void gestisciGetRichiesteMie(HttpServletRequest request, HttpServletResponse response,
            Utente utenteLoggato) throws SQLException, IOException, JSONException {
        
        List<Richiesta> richieste = richiestaDAO.findByUtente(utenteLoggato.getIdUtente());
        
        JSONObject responseJson = new JSONObject();
        responseJson.put("success", true);
        responseJson.put("count", richieste.size());
        
        JSONArray richiesteArray = new JSONArray();
        for (Richiesta richiesta : richieste) {
            JSONObject richiestaJson = new JSONObject();
            richiestaJson.put("id", richiesta.getIdRichiesta());
            richiestaJson.put("dataCreazione", richiesta.getDataOrarioCreazione().toString());
            richiestaJson.put("stato", richiesta.getStatoRichiesta().toString());
            richiestaJson.put("modalitaSelezione", richiesta.getModalitaSelezione().toString());
            richiestaJson.put("noteAggiuntive", richiesta.getNoteAggiuntive());
            
            if (richiesta.getScadenza() != null) {
                richiestaJson.put("scadenza", richiesta.getScadenza().toString());
            }
            
            // Conta esperti assegnati
            int numeroEsperti = richiestaEspertoDAO.contaEspertiByRichiesta(richiesta.getIdRichiesta());
            richiestaJson.put("espertiAssegnati", numeroEsperti);
            
            // Conta valutazioni ricevute
            int valutazioniRicevute = valutazioneDAO.contaValutazioni(richiesta.getIdRichiesta());
            richiestaJson.put("valutazioniRicevute", valutazioniRicevute);
            
            richiesteArray.put(richiestaJson);
        }
        
        responseJson.put("richieste", richiesteArray);
        
        PrintWriter out = response.getWriter();
        out.print(responseJson.toString());
        out.flush();
        response.setStatus(200);
    }
    
    /**
     * GET /RichiesteServlet/{id}
     * Recupera dettagli di una richiesta specifica
     */
    private void gestisciGetRichiestaSingola(HttpServletRequest request, HttpServletResponse response,
            Utente utenteLoggato, String pathInfo) throws SQLException, IOException, JSONException {
        
        try {
            int idRichiesta = Integer.parseInt(pathInfo.substring(1)); // Remove leading "/"
            
            Richiesta richiesta = richiestaDAO.findById(idRichiesta);
            if (richiesta == null) {
                inviaErrore(response, 404, "Richiesta non trovata");
                return;
            }
            
            // Verifica che la richiesta appartenga all'utente loggato
            if (richiesta.getIdUtente() != utenteLoggato.getIdUtente()) {
                inviaErrore(response, 403, "Accesso negato a questa richiesta");
                return;
            }
            
            JSONObject richiestaJson = creaDettagliRichiesta(richiesta);
            
            JSONObject responseJson = new JSONObject();
            responseJson.put("success", true);
            responseJson.put("richiesta", richiestaJson);
            
            PrintWriter out = response.getWriter();
            out.print(responseJson.toString());
            out.flush();
            response.setStatus(200);
            
        } catch (NumberFormatException e) {
            inviaErrore(response, 400, "ID richiesta non valido");
        }
    }
    
    /**
     * GET /RichiesteServlet/{id}/risultati
     * Recupera i risultati delle valutazioni per una richiesta
     */
    private void gestisciGetRisultatiRichiesta(HttpServletRequest request, HttpServletResponse response,
            Utente utenteLoggato, String pathInfo) throws SQLException, IOException, JSONException {
        
        try {
            // Estrae ID da /{id}/risultati
            String[] parts = pathInfo.split("/");
            int idRichiesta = Integer.parseInt(parts[1]);
            
            Richiesta richiesta = richiestaDAO.findById(idRichiesta);
            if (richiesta == null) {
                inviaErrore(response, 404, "Richiesta non trovata");
                return;
            }
            
            // Verifica ownership
            if (richiesta.getIdUtente() != utenteLoggato.getIdUtente()) {
                inviaErrore(response, 403, "Accesso negato a questa richiesta");
                return;
            }
            
            // Genera risultati usando RispostaValutazioneDTO
            RispostaValutazioneDTO risultati = generaRisultatiValutazione(idRichiesta, richiesta);
            
            JSONObject responseJson = new JSONObject();
            responseJson.put("success", true);
            
            // Parse del JSON dal DTO (non ideale, ma mantiene compatibilità)
            JSONObject risultatiJson = new JSONObject(risultati.toJSONString());
            responseJson.put("risultati", risultatiJson);
            
            PrintWriter out = response.getWriter();
            out.print(responseJson.toString());
            out.flush();
            response.setStatus(200);
            
        } catch (NumberFormatException e) {
            inviaErrore(response, 400, "ID richiesta non valido");
        }
    }
    
    // ===== METODI DI UTILITÀ =====
    
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
     * Verifica se il DTO contiene informazioni hardware
     */
    private boolean hasProfiloHardware(NuovaRichiestaDTO dto) {
        return dto.getCpu() != null || dto.getRam() != null || 
               dto.getSpazioArchiviazione() != null || dto.getSchedaVideo() != null;
    }
    
    /**
     * Crea ProfiloHardware dal DTO
     */
    private ProfiloHardware creaProfiloHardware(NuovaRichiestaDTO dto, int idUtente) {
        ProfiloHardware profilo = new ProfiloHardware(idUtente, dto.getCpu(), dto.getRam(), dto.getSpazioArchiviazione());
        profilo.setSchedaVideo(dto.getSchedaVideo());
        
        if (dto.getTipoSistema() != null) {
            profilo.setTipoSistema(TipoSistema.valueOf(dto.getTipoSistema().toUpperCase()));
        }
        
        return profilo;
    }
    
    /**
     * Seleziona esperti (automatico = tutti, manuale = dalla lista)
     */
    private List<Integer> selezionaEsperti(NuovaRichiestaDTO dto) throws SQLException {
        List<Integer> espertiIds = new ArrayList<>();
        
        if ("AUTOMATICA".equals(dto.getModalitaSelezione())) {
            // Selezione automatica: tutti gli esperti attivi
            List<Esperto> tuttiEsperti = espertoDAO.getTuttiEspertiAttivi();
            for (Esperto esperto : tuttiEsperti) {
                espertiIds.add(esperto.getIdEsperto());
            }
        } else if ("MANUALE".equals(dto.getModalitaSelezione())) {
            // Selezione manuale: dalla lista del DTO
            for (EspertoSelezionatoDTO espertoDTO : dto.getEspertiSelezionati()) {
                espertiIds.add(espertoDTO.getId());
            }
        }
        
        return espertiIds;
    }
    
    /**
     * Genera messaggio per notifica agli esperti
     */
    private String generaMessaggioNotifica(Richiesta richiesta, Utente utente) {
        StringBuilder messaggio = new StringBuilder();
        messaggio.append("È stata creata una nuova richiesta di consulenza.\n\n");
        messaggio.append("Dettagli:\n");
        messaggio.append("- Utente: ").append(utente.getMail()).append("\n");
        messaggio.append("- Livello esperienza: ").append(utente.getLivelloEsperienza()).append("\n");
        messaggio.append("- Scopo uso: ").append(utente.getScopoUso()).append("\n");
        messaggio.append("- Data creazione: ").append(richiesta.getDataOrarioCreazione()).append("\n");
        
        if (richiesta.getNoteAggiuntive() != null && !richiesta.getNoteAggiuntive().isEmpty()) {
            messaggio.append("- Note: ").append(richiesta.getNoteAggiuntive()).append("\n");
        }
        
        messaggio.append("\nAccedi all'applicazione per valutare le distribuzioni proposte.");
        return messaggio.toString();
    }
    
    /**
     * Crea JSON con dettagli completi di una richiesta
     */
    private JSONObject creaDettagliRichiesta(Richiesta richiesta) throws SQLException, JSONException {
        JSONObject richiestaJson = new JSONObject();
        
        richiestaJson.put("id", richiesta.getIdRichiesta());
        richiestaJson.put("dataCreazione", richiesta.getDataOrarioCreazione().toString());
        richiestaJson.put("stato", richiesta.getStatoRichiesta().toString());
        richiestaJson.put("modalitaSelezione", richiesta.getModalitaSelezione().toString());
        richiestaJson.put("noteAggiuntive", richiesta.getNoteAggiuntive());
        richiestaJson.put("maxEsperti", richiesta.getMaxEsperti());
        
        if (richiesta.getScadenza() != null) {
            richiestaJson.put("scadenza", richiesta.getScadenza().toString());
        }
        
        // Distribuzioni candidate
        List<RichiestaDistribuzione> distribuzioni = richiestaDistribuzioneDAO.getByRichiesta(richiesta.getIdRichiesta());
        JSONArray distribuzioniArray = new JSONArray();
        for (RichiestaDistribuzione rd : distribuzioni) {
            Distribuzione distro = distroDAO.findById(rd.getIdDistribuzione());
            if (distro != null) {
                JSONObject distroJson = new JSONObject();
                distroJson.put("id", distro.getIdDistribuzione());
                distroJson.put("nome", distro.getNome());
                distroJson.put("versione", distro.getVersione());
                distroJson.put("ambienteDesktop", distro.getAmbienteDesktop());
                distribuzioniArray.put(distroJson);
            }
        }
        richiestaJson.put("distribuzioniCandidate", distribuzioniArray);
        
        // Esperti assegnati
        List<RichiestaEsperto> richiesteEsperti = richiestaEspertoDAO.getByRichiesta(richiesta.getIdRichiesta());
        JSONArray espertiArray = new JSONArray();
        for (RichiestaEsperto re : richiesteEsperti) {
            Esperto esperto = espertoDAO.findById(re.getIdEsperto());
            if (esperto != null) {
                Utente utenteEsperto = utenteDAO.findById(esperto.getIdUtente());
                JSONObject espertoJson = new JSONObject();
                espertoJson.put("id", esperto.getIdEsperto());
                espertoJson.put("nome", utenteEsperto != null ? utenteEsperto.getMail() : "Esperto #" + esperto.getIdEsperto());
                espertoJson.put("specializzazione", esperto.getSpecializzazione());
                espertoJson.put("statoNotifica", re.getStatoNotifica().toString());
                espertiArray.put(espertoJson);
            }
        }
        richiestaJson.put("espertiAssegnati", espertiArray);
        
        return richiestaJson;
    }
    
    /**
     * Genera risultati completi delle valutazioni
     */
    private RispostaValutazioneDTO generaRisultatiValutazione(int idRichiesta, Richiesta richiesta) 
            throws SQLException {
        
        RispostaValutazioneDTO risposta = new RispostaValutazioneDTO(
            idRichiesta,
            richiesta.getStatoRichiesta().toString()
        );
        
        // Carica valutazioni per ogni distribuzione
        List<RichiestaDistribuzione> distribuzioni = richiestaDistribuzioneDAO.getByRichiesta(idRichiesta);
        
        for (RichiestaDistribuzione rd : distribuzioni) {
            Distribuzione distro = distroDAO.findById(rd.getIdDistribuzione());
            if (distro == null) continue;
            
            DistribuzioneSelezionataDTO distroDTO = new DistribuzioneSelezionataDTO(
                distro.getIdDistribuzione(),
                distro.getNome(),
                distro.getVersione(),
                distro.getAmbienteDesktop()
            );
            
            // Carica valutazioni per questa distribuzione
            List<ValutazioneEsperto> valutazioni = valutazioneDAO.getByRichiestaEDistribuzione(
                idRichiesta, rd.getIdDistribuzione()
            );
            
            double punteggioMedio = 0.0;
            if (!valutazioni.isEmpty()) {
                punteggioMedio = valutazioni.stream().mapToDouble(ValutazioneEsperto::getPunteggio).average().orElse(0.0);
            }
            
            RispostaValutazioneDTO.ValutazioneDistribuzioneDTO valutazioneDTO = 
                new RispostaValutazioneDTO.ValutazioneDistribuzioneDTO(
                    distroDTO, punteggioMedio, valutazioni.size()
                );
            
            // Aggiungi commenti degli esperti
            for (ValutazioneEsperto val : valutazioni) {
                Esperto esperto = espertoDAO.findById(val.getIdEsperto());
                if (esperto == null) continue;
                
                Utente utenteEsperto = utenteDAO.findById(esperto.getIdUtente());
                String nomeEsperto = utenteEsperto != null ? utenteEsperto.getMail() : "Esperto #" + esperto.getIdEsperto();
                
                EspertoSelezionatoDTO espertoDTO = new EspertoSelezionatoDTO(
                    esperto.getIdEsperto(),
                    nomeEsperto,
                    esperto.getSpecializzazione(),
                    esperto.getAnniEsperienza(),
                    esperto.getFeedbackMedio()
                );
                
                RispostaValutazioneDTO.CommentoEspertoDTO commentoDTO = 
                    new RispostaValutazioneDTO.CommentoEspertoDTO(
                        espertoDTO,
                        val.getPunteggio(),
                        val.getSuggerimento(),
                        val.getMotivazione()
                    );
                
                valutazioneDTO.aggiungiCommento(commentoDTO);
            }
            
            risposta.aggiungiValutazione(valutazioneDTO);
        }
        
        return risposta;
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