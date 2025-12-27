package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entity.database.RichiestaDistribuzione;

public class RichiestaDistribuzioneDAOImpl implements RichiestaDistribuzioneDAO {
	
private String ip, port, dbName, userName, pwd;
    
    public RichiestaDistribuzioneDAOImpl(String ip, String port, String dbName, String userName, String pwd) {
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
    public int salva(RichiestaDistribuzione rd) throws SQLException {
        String sql = "INSERT INTO RichiestaDistribuzione (id_richiesta, id_distribuzione) VALUES (?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, rd.getIdRichiesta());
            pstmt.setInt(2, rd.getIdDistribuzione());
            
            return pstmt.executeUpdate();
        }
    }
    
    @Override
    public List<RichiestaDistribuzione> getByRichiesta(int idRichiesta) throws SQLException {
        String sql = "SELECT id_richiesta, id_distribuzione FROM RichiestaDistribuzione WHERE id_richiesta = ?";
        List<RichiestaDistribuzione> result = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idRichiesta);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new RichiestaDistribuzione(
                        rs.getInt("id_richiesta"),
                        rs.getInt("id_distribuzione")
                    ));
                }
            }
        }
        return result;
    }
    
    @Override
    public void eliminaByRichiesta(int idRichiesta) throws SQLException {
        String sql = "DELETE FROM RichiestaDistribuzione WHERE id_richiesta = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idRichiesta);
            pstmt.executeUpdate();
        }
    }
    
    @Override
    public boolean esisteAssociazione(int idRichiesta, int idDistribuzione) throws SQLException {
        String sql = "SELECT COUNT(*) FROM RichiestaDistribuzione WHERE id_richiesta = ? AND id_distribuzione = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idRichiesta);
            pstmt.setInt(2, idDistribuzione);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
	
	

}
