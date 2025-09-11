package servlet;

import jakarta.servlet.ServletException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dao.*;
import entity.database.*;
import entity.dto.*;


@WebServlet("/DatiSupportoServlet")
public class DatiSupportoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private DistroDAO distroDAO;
    private EspertoDAO espertoDAO;
    private UtenteDAO utenteDAO;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DatiSupportoServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        // Recupera parametri di configurazione database
        String ip = getInitParameter("ip");
        String port = getInitParameter("port"); 
        String dbName = getInitParameter("dbName");
        String userName = getInitParameter("userName");
        String password = getInitParameter("password");
        
        // Inizializza DAO
        this.distroDAO = new DistroDAOImpl(ip, port, dbName, userName, password);
        this.espertoDAO = new EspertoDAOImpl(ip, port, dbName, userName, password);
        this.utenteDAO = new UtenteDAOImpl(ip, port, dbName, userName, password);
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null) {
        	response.setStatus(400);
        	response.getWriter().append("Endpoint mancante");
        	return;
            /*try {
            	response.setStatus(400);
            	response.getWriter().append("Endpoint mancante");
				//inviaErrore(response, 400, "Endpoint mancante");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return;*/
        }
        
        try {
            switch (pathInfo) {
                case "/distribuzioni":
                    gestisciGetDistribuzioni(request, response);
                    break;
                case "/esperti":
                    gestisciGetEsperti(request, response);
                    break;
                default:
                    // Pattern per singoli elementi: /distribuzione/123, /esperto/456
                    if (pathInfo.startsWith("/distribuzione/")) {
                        gestisciGetDistribuzioneSingola(request, response, pathInfo);
                    } else if (pathInfo.startsWith("/esperto/")) {
                        gestisciGetEspertoSingolo(request, response, pathInfo);
                    } else {
                    	response.setStatus(404);
                    	response.getWriter().append("Endpoint non trovato: " + pathInfo);
                    	return;
                        //inviaErrore(response, 404, "Endpoint non trovato: " + pathInfo);
                    }
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(500);
        	response.getWriter().append("Errore database: " + e.getMessage());
        	return;
            //inviaErrore(response, 500, "Errore database: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
        	response.getWriter().append("Errore interno del server");
        	return;
            //inviaErrore(response, 500, "Errore interno del server");
        }
		
		
	}
	
	private void gestisciGetDistribuzioni(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException, JSONException {
        
        List<DistribuzioneSelezionataDTO> distribuzioni = distroDAO.getDistribuzioniSelezionabili();
        
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("success", true);
        jsonResponse.put("count", distribuzioni.size());
        
        JSONArray distribuzioniArray = new JSONArray();
        for (DistribuzioneSelezionataDTO distro : distribuzioni) {
            JSONObject distroJson = new JSONObject();
            distroJson.put("id", distro.getId());
            distroJson.put("nome", distro.getNome());
            distroJson.put("versione", distro.getVersione());
            distroJson.put("ambienteDesktop", distro.getAmbienteDesktop());
            distribuzioniArray.put(distroJson);
        }
        jsonResponse.put("distribuzioni", distribuzioniArray);
        
        PrintWriter out = response.getWriter();
        out.print(jsonResponse.toString());
        out.flush();
        response.setStatus(200);
    }
    
    /**
     * GET /api/esperti
     * Restituisce lista di tutti gli esperti attivi per selezione manuale
     * @throws JSONException 
     */
    private void gestisciGetEsperti(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException, JSONException {
        
        List<Esperto> esperti = espertoDAO.getTuttiEspertiAttivi();
        
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("success", true);
        jsonResponse.put("count", esperti.size());
        
        JSONArray espertiArray = new JSONArray();
        for (Esperto esperto : esperti) {
            // Recupera nome utente associato
            Utente utente = utenteDAO.findById(esperto.getIdUtente());
            String nomeEsperto = utente != null ? utente.getMail() : "Esperto #" + esperto.getIdEsperto();
            
            JSONObject espertoJson = new JSONObject();
            espertoJson.put("id", esperto.getIdEsperto());
            espertoJson.put("nome", nomeEsperto);
            espertoJson.put("specializzazione", esperto.getSpecializzazione());
            espertoJson.put("anniEsperienza", esperto.getAnniEsperienza());
            espertoJson.put("feedbackMedio", esperto.getFeedbackMedio());
            espertoJson.put("numeroValutazioni", esperto.getNumeroValutazioni());
            espertiArray.put(espertoJson);
        }
        jsonResponse.put("esperti", espertiArray);
        
        PrintWriter out = response.getWriter();
        out.print(jsonResponse.toString());
        out.flush();
        response.setStatus(200);
    }
    
    /**
     * GET /api/distribuzione/{id}
     * Restituisce dettagli completi di una distribuzione specifica
     * @throws JSONException 
     */
    private void gestisciGetDistribuzioneSingola(HttpServletRequest request, HttpServletResponse response, 
            String pathInfo) throws SQLException, IOException, JSONException {
        
        try {
            // Estrae ID dal path: /distribuzione/123 -> 123
            String idStr = pathInfo.substring("/distribuzione/".length());
            int idDistribuzione = Integer.parseInt(idStr);
            
            DistribuzioneCompletaDTO distribuzione = distroDAO.getDistribuzioneCompletaById(idDistribuzione);
            
            if (distribuzione == null) {
            	response.setStatus(404);
            	response.getWriter().append("Distribuzione non trovata");
                //inviaErrore(response, 404, "Distribuzione non trovata");
                return;
            }
            
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("success", true);
            
            JSONObject distroJson = new JSONObject();
            distroJson.put("id", distribuzione.getId());
            distroJson.put("nome", distribuzione.getNome());
            distroJson.put("versione", distribuzione.getVersione());
            distroJson.put("ambienteDesktop", distribuzione.getAmbienteDesktop());
            distroJson.put("categoria", distribuzione.getCategoria());
            distroJson.put("descrizione", distribuzione.getDescrizione());
            distroJson.put("requisitiHardware", distribuzione.getRequisitiHardware());
            distroJson.put("pregi", distribuzione.getPregi());
            distroJson.put("difetti", distribuzione.getDifetti());
            
            jsonResponse.put("distribuzione", distroJson);
            
            PrintWriter out = response.getWriter();
            out.print(jsonResponse.toString());
            out.flush();
            response.setStatus(200);
            
        } catch (NumberFormatException e) {
        	response.setStatus(400);
        	response.getWriter().append("ID distribuzione non valido");
        	
           // inviaErrore(response, 400, "ID distribuzione non valido");
        }
    }
    
    /**
     * GET /api/esperto/{id}
     * Restituisce dettagli completi di un esperto specifico
     * @throws JSONException 
     */
    private void gestisciGetEspertoSingolo(HttpServletRequest request, HttpServletResponse response, 
            String pathInfo) throws SQLException, IOException, JSONException {
        
        try {
            // Estrae ID dal path: /esperto/456 -> 456
            String idStr = pathInfo.substring("/esperto/".length());
            int idEsperto = Integer.parseInt(idStr);
            
            Esperto esperto = espertoDAO.findById(idEsperto);
            
            if (esperto == null) {
            	response.setStatus(404);
            	response.getWriter().append("Esperto non trovato");
                //inviaErrore(response, 404, "Esperto non trovato");
                return;
            }
            
            // Recupera nome utente associato
            Utente utente = utenteDAO.findById(esperto.getIdUtente());
            String nomeEsperto = utente != null ? utente.getMail() : "Esperto #" + esperto.getIdEsperto();
            
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("success", true);
            
            JSONObject espertoJson = new JSONObject();
            espertoJson.put("id", esperto.getIdEsperto());
            espertoJson.put("nome", nomeEsperto);
            espertoJson.put("specializzazione", esperto.getSpecializzazione());
            espertoJson.put("anniEsperienza", esperto.getAnniEsperienza());
            espertoJson.put("feedbackMedio", esperto.getFeedbackMedio());
            espertoJson.put("numeroValutazioni", esperto.getNumeroValutazioni());
            espertoJson.put("attivo", esperto.isAttivo());
            espertoJson.put("dataRegistrazione", esperto.getDataRegistrazione().toString());
            
            // Informazioni utente base (senza dati sensibili)
            if (utente != null) {
                JSONObject utenteJson = new JSONObject();
                utenteJson.put("livelloEsperienza", utente.getLivelloEsperienza().toString());
                utenteJson.put("scopoUso", utente.getScopoUso());
                espertoJson.put("profiloUtente", utenteJson);
            }
            
            jsonResponse.put("esperto", espertoJson);
            
            PrintWriter out = response.getWriter();
            out.print(jsonResponse.toString());
            out.flush();
            response.setStatus(200);
            
        } catch (NumberFormatException e) {
        	response.setStatus(400);
        	response.getWriter().append("ID esperto non valido");

        	
            //inviaErrore(response, 400, "ID esperto non valido");
        }
    }
    
    /**
     * Utility per inviare errori in formato JSON standardizzato
     * @throws JSONException 
     */
   /* private void inviaErrore(HttpServletResponse response, int statusCode, String messaggio) 
            throws IOException, JSONException {
        
        response.setStatus(statusCode);
        
        JSONObject errorJson = new JSONObject();
        errorJson.put("success", false);
        errorJson.put("error", messaggio);
        errorJson.put("statusCode", statusCode);
        
        PrintWriter out = response.getWriter();
        out.print(errorJson.toString());
        out.flush();
    }
    
    vecchia gestione errori
    response.setStatus(551);
	response.getWriter().append("non esistono distribuzioni con il parametro specificato");
	return;
    */
    

	

}
