package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import entity.database.Notifica;
import enumerazioni.StatoNotificaLettura;
import enumerazioni.TipoNotifica;
import enumerazioni.PrioritaNotifica;

public class NotificaDAOImpl implements NotificaDAO {
	
private String ip, port, dbName, userName, pwd;
    
    public NotificaDAOImpl(String ip, String port, String dbName, String userName, String pwd) {
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
    public int salva(Notifica notifica) throws SQLException {
        String sql = "INSERT INTO Notifica (id_esperto, id_richiesta, tipo_notifica, titolo, " +
                    "messaggio, stato, priorita) VALUES (?, ?, ?, ?, ?, ?, ?)";
        int idGenerato = -1;
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, notifica.getIdEsperto());
            pstmt.setInt(2, notifica.getIdRichiesta());
            pstmt.setString(3, notifica.getTipoNotifica().name());
            pstmt.setString(4, notifica.getTitolo());
            pstmt.setString(5, notifica.getMessaggio());
            pstmt.setString(6, notifica.getStato().name());
            pstmt.setString(7, notifica.getPriorita().name());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creazione notifica fallita.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    idGenerato = generatedKeys.getInt(1);
                    notifica.setIdNotifica(idGenerato);
                }
            }
        }
        return idGenerato;
    }
    
    
    public List<Notifica> getNotificheNonLetteByEsperto(int idEsperto) throws SQLException {
        String sql = "SELECT id_notifica, id_esperto, id_richiesta, tipo_notifica, titolo, " +
                    "messaggio, data_creazione, data_lettura, stato, priorita " +
                    "FROM Notifica WHERE id_esperto = ? AND stato = 'NON_LETTA' " +
                    "ORDER BY data_creazione DESC";
        List<Notifica> result = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idEsperto);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapResultSetToNotifica(rs));
                }
            }
        }
        return result;
    }
    
    @Override
    public List<Notifica> getTutteNotificheByEsperto(int idEsperto) throws SQLException {
        String sql = "SELECT id_notifica, id_esperto, id_richiesta, tipo_notifica, titolo, " +
                    "messaggio, data_creazione, data_lettura, stato, priorita " +
                    "FROM Notifica WHERE id_esperto = ? " +
                    "ORDER BY data_creazione DESC LIMIT 50";
        List<Notifica> result = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idEsperto);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapResultSetToNotifica(rs));
                }
            }
        }
        return result;
    }
    
    @Override
    public void marcaComeLetta(int idNotifica) throws SQLException {
        String sql = "UPDATE Notifica SET stato = 'LETTA', data_lettura = NOW() WHERE id_notifica = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idNotifica);
            pstmt.executeUpdate();
        }
    }
    
    @Override
    public void aggiornaStato(int idNotifica, StatoNotificaLettura stato) throws SQLException {
        String sql = "UPDATE Notifica SET stato = ? WHERE id_notifica = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, stato.name());
            pstmt.setInt(2, idNotifica);
            pstmt.executeUpdate();
        }
    }
    
    @Override
    public int contaNotificheNonLette(int idEsperto) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Notifica WHERE id_esperto = ? AND stato = 'NON_LETTA'";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idEsperto);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    private Notifica mapResultSetToNotifica(ResultSet rs) throws SQLException {
        Notifica notifica = new Notifica(
            rs.getInt("id_esperto"),
            rs.getInt("id_richiesta"),
            TipoNotifica.valueOf(rs.getString("tipo_notifica")),
            rs.getString("titolo")
        );
        
        notifica.setIdNotifica(rs.getInt("id_notifica"));
        notifica.setMessaggio(rs.getString("messaggio"));
        notifica.setDataCreazione(rs.getTimestamp("data_creazione"));
        notifica.setDataLettura(rs.getTimestamp("data_lettura"));
        notifica.setStato(StatoNotificaLettura.valueOf(rs.getString("stato")));
        notifica.setPriorita(PrioritaNotifica.valueOf(rs.getString("priorita")));
        
        return notifica;
    }
	
	

}
