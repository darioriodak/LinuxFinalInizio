package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import entity.database.ValutazioneEsperto;

public class ValutazioneEspertoDAOImpl implements ValutazioneEspertoDAO{
	
private String ip, port, dbName, userName, pwd;
    
    public ValutazioneEspertoDAOImpl(String ip, String port, String dbName, String userName, String pwd) {
        this.ip = ip;
        this.port = port;
        this.dbName = dbName;
        this.userName = userName;
        this.pwd = pwd;
    }
    
    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + dbName
             + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
             userName, pwd);
    }
    
    @Override
    public int salva(ValutazioneEsperto valutazione) throws SQLException {
        String sql = "INSERT INTO ValutazioneEsperto (id_richiesta, id_distribuzione, id_esperto, " +
                    "punteggio, suggerimento, motivazione) VALUES (?, ?, ?, ?, ?, ?)";
        int idGenerato = -1;
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, valutazione.getIdRichiesta());
            pstmt.setInt(2, valutazione.getIdDistribuzione());
            pstmt.setInt(3, valutazione.getIdEsperto());
            pstmt.setDouble(4, valutazione.getPunteggio());
            pstmt.setString(5, valutazione.getSuggerimento());
            pstmt.setString(6, valutazione.getMotivazione());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creazione valutazione fallita.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    idGenerato = generatedKeys.getInt(1);
                    valutazione.setIdValutazione(idGenerato);
                }
            }
        }
        return idGenerato;
    }
    
    @Override
    public List<ValutazioneEsperto> getByRichiesta(int idRichiesta) throws SQLException {
        String sql = "SELECT id_valutazione, id_richiesta, id_distribuzione, id_esperto, " +
                    "punteggio, suggerimento, motivazione, data_valutazione " +
                    "FROM ValutazioneEsperto WHERE id_richiesta = ?";
        List<ValutazioneEsperto> result = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idRichiesta);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapResultSetToValutazioneEsperto(rs));
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ValutazioneEsperto> getByRichiestaEDistribuzione(int idRichiesta, int idDistribuzione) throws SQLException {
        String sql = "SELECT id_valutazione, id_richiesta, id_distribuzione, id_esperto, " +
                    "punteggio, suggerimento, motivazione, data_valutazione " +
                    "FROM ValutazioneEsperto WHERE id_richiesta = ? AND id_distribuzione = ?";
        List<ValutazioneEsperto> result = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idRichiesta);
            pstmt.setInt(2, idDistribuzione);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapResultSetToValutazioneEsperto(rs));
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ValutazioneEsperto> getByRichiestaEEsperto(int idRichiesta, int idEsperto) throws SQLException {
        String sql = "SELECT id_valutazione, id_richiesta, id_distribuzione, id_esperto, " +
                    "punteggio, suggerimento, motivazione, data_valutazione " +
                    "FROM ValutazioneEsperto WHERE id_richiesta = ? AND id_esperto = ?";
        List<ValutazioneEsperto> result = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idRichiesta);
            pstmt.setInt(2, idEsperto);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapResultSetToValutazioneEsperto(rs));
                }
            }
        }
        return result;
    }
    
    @Override
    public boolean haValutatoEsperto(int idEsperto, int idRichiesta) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ValutazioneEsperto WHERE id_esperto = ? AND id_richiesta = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idEsperto);
            pstmt.setInt(2, idRichiesta);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    @Override
    public double getPunteggioMedio(int idRichiesta, int idDistribuzione) throws SQLException {
        String sql = "SELECT AVG(punteggio) FROM ValutazioneEsperto WHERE id_richiesta = ? AND id_distribuzione = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idRichiesta);
            pstmt.setInt(2, idDistribuzione);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0.0;
    }
    
    @Override
    public int contaValutazioni(int idRichiesta) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT id_esperto) FROM ValutazioneEsperto WHERE id_richiesta = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idRichiesta);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    private ValutazioneEsperto mapResultSetToValutazioneEsperto(ResultSet rs) throws SQLException {
        ValutazioneEsperto valutazione = new ValutazioneEsperto(
            rs.getInt("id_richiesta"),
            rs.getInt("id_distribuzione"),
            rs.getInt("id_esperto"),
            rs.getDouble("punteggio")
        );
        
        valutazione.setIdValutazione(rs.getInt("id_valutazione"));
        valutazione.setSuggerimento(rs.getString("suggerimento"));
        valutazione.setMotivazione(rs.getString("motivazione"));
        valutazione.setDataValutazione(rs.getTimestamp("data_valutazione"));
        
        return valutazione;
    }

}
