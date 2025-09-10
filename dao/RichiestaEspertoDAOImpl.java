package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entity.database.RichiestaEsperto;
import enumerazioni.StatoNotifica;

public class RichiestaEspertoDAOImpl implements RichiestaEspertoDAO {
	
private String ip, port, dbName, userName, pwd;
    
    public RichiestaEspertoDAOImpl(String ip, String port, String dbName, String userName, String pwd) {
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
    
    
    public int salva(RichiestaEsperto richiestaEsperto) throws SQLException {
        String sql = "INSERT INTO RichiestaEsperto (id_richiesta, id_esperto, stato_notifica, data_scadenza) " +
                    "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, richiestaEsperto.getIdRichiesta());
            pstmt.setInt(2, richiestaEsperto.getIdEsperto());
            pstmt.setString(3, richiestaEsperto.getStatoNotifica().name()); // MAIUSCOLO
            
            if (richiestaEsperto.getDataScadenza() != null) {
                pstmt.setTimestamp(4, richiestaEsperto.getDataScadenza());
            } else {
                pstmt.setNull(4, java.sql.Types.TIMESTAMP);
            }
            
            return pstmt.executeUpdate();
        }
    }
    
    
    public List<RichiestaEsperto> getByRichiesta(int idRichiesta) throws SQLException {
        String sql = "SELECT id_richiesta, id_esperto, data_assegnazione, stato_notifica, " +
                    "data_lettura, data_scadenza FROM RichiestaEsperto WHERE id_richiesta = ?";
        List<RichiestaEsperto> result = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idRichiesta);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapResultSetToRichiestaEsperto(rs));
                }
            }
        }
        return result;
    }
    
    
    public List<RichiestaEsperto> getByEsperto(int idEsperto) throws SQLException {
        String sql = "SELECT id_richiesta, id_esperto, data_assegnazione, stato_notifica, " +
                    "data_lettura, data_scadenza FROM RichiestaEsperto WHERE id_esperto = ? " +
                    "ORDER BY data_assegnazione DESC";
        List<RichiestaEsperto> result = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idEsperto);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapResultSetToRichiestaEsperto(rs));
                }
            }
        }
        return result;
    }
    
    
    public void aggiorna(RichiestaEsperto richiestaEsperto) throws SQLException {
        String sql = "UPDATE RichiestaEsperto SET stato_notifica = ?, data_lettura = ?, " +
                    "data_scadenza = ? WHERE id_richiesta = ? AND id_esperto = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, richiestaEsperto.getStatoNotifica().name());
            pstmt.setTimestamp(2, richiestaEsperto.getDataLettura());
            pstmt.setTimestamp(3, richiestaEsperto.getDataScadenza());
            pstmt.setInt(4, richiestaEsperto.getIdRichiesta());
            pstmt.setInt(5, richiestaEsperto.getIdEsperto());
            
            pstmt.executeUpdate();
        }
    }
    
    
    public boolean esisteAssegnazione(int idRichiesta, int idEsperto) throws SQLException {
        String sql = "SELECT COUNT(*) FROM RichiestaEsperto WHERE id_richiesta = ? AND id_esperto = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idRichiesta);
            pstmt.setInt(2, idEsperto);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    
    public void eliminaByRichiesta(int idRichiesta) throws SQLException {
        String sql = "DELETE FROM RichiestaEsperto WHERE id_richiesta = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idRichiesta);
            pstmt.executeUpdate();
        }
    }
    
    
    public int contaEspertiByRichiesta(int idRichiesta) throws SQLException {
        String sql = "SELECT COUNT(*) FROM RichiestaEsperto WHERE id_richiesta = ?";
        
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
    
    private RichiestaEsperto mapResultSetToRichiestaEsperto(ResultSet rs) throws SQLException {
        RichiestaEsperto re = new RichiestaEsperto(
            rs.getInt("id_richiesta"),
            rs.getInt("id_esperto")
        );
        
        re.setDataAssegnazione(rs.getTimestamp("data_assegnazione"));
        re.setStatoNotifica(StatoNotifica.valueOf(rs.getString("stato_notifica")));
        re.setDataLettura(rs.getTimestamp("data_lettura"));
        re.setDataScadenza(rs.getTimestamp("data_scadenza"));
        
        return re;
    }

}
