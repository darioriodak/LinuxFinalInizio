package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import entity.database.Esperto;

public class EspertoDAOImpl implements EspertoDAO{
	
private String ip, port, dbName, userName, pwd;
    
    public EspertoDAOImpl(String ip, String port, String dbName, String userName, String pwd) {
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
    
    
    public int salva(Esperto esperto) throws SQLException {
        String sql = "INSERT INTO Esperto (id_utente, specializzazione, anni_esperienza, " +
                    "feedback_medio, numero_valutazioni, attivo) VALUES (?, ?, ?, ?, ?, ?)";
        int idGenerato = -1;
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, esperto.getIdUtente());
            pstmt.setString(2, esperto.getSpecializzazione());
            pstmt.setInt(3, esperto.getAnniEsperienza());
            pstmt.setDouble(4, esperto.getFeedbackMedio());
            pstmt.setInt(5, esperto.getNumeroValutazioni());
            pstmt.setBoolean(6, esperto.isAttivo());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creazione esperto fallita, nessuna riga modificata.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    idGenerato = generatedKeys.getInt(1);
                    esperto.setIdEsperto(idGenerato);
                } else {
                    throw new SQLException("Creazione esperto fallita, nessun ID generato.");
                }
            }
        }
        return idGenerato;
    }
    
    
    public Esperto findById(int idEsperto) throws SQLException {
        String sql = "SELECT id_esperto, id_utente, specializzazione, anni_esperienza, " +
                    "feedback_medio, numero_valutazioni, attivo, data_registrazione " +
                    "FROM Esperto WHERE id_esperto = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idEsperto);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEsperto(rs);
                }
            }
        }
        return null;
    }
    
    
    public void aggiorna(Esperto esperto) throws SQLException {
        String sql = "UPDATE Esperto SET specializzazione = ?, anni_esperienza = ?, " +
                    "feedback_medio = ?, numero_valutazioni = ?, attivo = ? " +
                    "WHERE id_esperto = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, esperto.getSpecializzazione());
            pstmt.setInt(2, esperto.getAnniEsperienza());
            pstmt.setDouble(3, esperto.getFeedbackMedio());
            pstmt.setInt(4, esperto.getNumeroValutazioni());
            pstmt.setBoolean(5, esperto.isAttivo());
            pstmt.setInt(6, esperto.getIdEsperto());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Aggiornamento esperto fallito, nessuna riga modificata.");
            }
        }
    }
    
    
    public void eliminaById(int idEsperto) throws SQLException {
        String sql = "DELETE FROM Esperto WHERE id_esperto = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idEsperto);
            pstmt.executeUpdate();
        }
    }
    
    
    public List<Esperto> getTuttiEspertiAttivi() throws SQLException {
        String sql = "SELECT id_esperto, id_utente, specializzazione, anni_esperienza, " +
                    "feedback_medio, numero_valutazioni, attivo, data_registrazione " +
                    "FROM Esperto WHERE attivo = true ORDER BY feedback_medio DESC";
        List<Esperto> esperti = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                esperti.add(mapResultSetToEsperto(rs));
            }
        }
        return esperti;
    }
    
    public boolean esisteEsperto(int idEsperto) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Esperto WHERE id_esperto = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idEsperto);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    private Esperto mapResultSetToEsperto(ResultSet rs) throws SQLException {
        Esperto esperto = new Esperto(
            rs.getInt("id_utente"),
            rs.getString("specializzazione"),
            rs.getInt("anni_esperienza")
        );
        
        esperto.setIdEsperto(rs.getInt("id_esperto"));
        esperto.setFeedbackMedio(rs.getDouble("feedback_medio"));
        esperto.setNumeroValutazioni(rs.getInt("numero_valutazioni"));
        esperto.setAttivo(rs.getBoolean("attivo"));
        esperto.setDataRegistrazione(rs.getTimestamp("data_registrazione"));
        
        return esperto;
    }

}
