package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import entity.database.ProfiloHardware;
import enumerazioni.TipoSistema;

public class ProfiloHardwareDAOImpl implements ProfiloHardwareDAO {
	
private String ip, port, dbName, userName, pwd;
    
    public ProfiloHardwareDAOImpl(String ip, String port, String dbName, String userName, String pwd) {
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
    public int salva(ProfiloHardware profilo) throws SQLException {
        String sql = "INSERT INTO ProfiloHardware (id_utente, cpu, ram, spazio_archiviazione, " +
                    "scheda_video, tipo_sistema) VALUES (?, ?, ?, ?, ?, ?)";
        int idGenerato = -1;
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, profilo.getIdUtente());
            pstmt.setString(2, profilo.getCpu());
            pstmt.setString(3, profilo.getRam());
            pstmt.setString(4, profilo.getSpazioArchiviazione());
            pstmt.setString(5, profilo.getSchedaVideo());
            
            if (profilo.getTipoSistema() != null) {
                pstmt.setString(6, profilo.getTipoSistema().name());
            } else {
                pstmt.setString(6, "DESKTOP"); // Default
            }
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creazione profilo hardware fallita.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    idGenerato = generatedKeys.getInt(1);
                    profilo.setIdHardware(idGenerato);
                }
            }
        }
        return idGenerato;
    }
    
    @Override
    public ProfiloHardware findById(int idHardware) throws SQLException {
        String sql = "SELECT id_hardware, id_utente, cpu, ram, spazio_archiviazione, " +
                    "scheda_video, tipo_sistema, data_creazione " +
                    "FROM ProfiloHardware WHERE id_hardware = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idHardware);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProfiloHardware(rs);
                }
            }
        }
        return null;
    }
    
    @Override
    public ProfiloHardware findByUtente(int idUtente) throws SQLException {
        String sql = "SELECT id_hardware, id_utente, cpu, ram, spazio_archiviazione, " +
                    "scheda_video, tipo_sistema, data_creazione " +
                    "FROM ProfiloHardware WHERE id_utente = ? ORDER BY data_creazione DESC LIMIT 1";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idUtente);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProfiloHardware(rs);
                }
            }
        }
        return null;
    }
    
    @Override
    public void aggiorna(ProfiloHardware profilo) throws SQLException {
        String sql = "UPDATE ProfiloHardware SET cpu = ?, ram = ?, spazio_archiviazione = ?, " +
                    "scheda_video = ?, tipo_sistema = ? WHERE id_hardware = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, profilo.getCpu());
            pstmt.setString(2, profilo.getRam());
            pstmt.setString(3, profilo.getSpazioArchiviazione());
            pstmt.setString(4, profilo.getSchedaVideo());
            
            if (profilo.getTipoSistema() != null) {
                pstmt.setString(5, profilo.getTipoSistema().name());
            } else {
                pstmt.setString(5, "DESKTOP");
            }
            
            pstmt.setInt(6, profilo.getIdHardware());
            pstmt.executeUpdate();
        }
    }
    
    @Override
    public void eliminaById(int idHardware) throws SQLException {
        String sql = "DELETE FROM ProfiloHardware WHERE id_hardware = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idHardware);
            pstmt.executeUpdate();
        }
    }
    
    private ProfiloHardware mapResultSetToProfiloHardware(ResultSet rs) throws SQLException {
        ProfiloHardware profilo = new ProfiloHardware(
            rs.getInt("id_utente"),
            rs.getString("cpu"),
            rs.getString("ram"),
            rs.getString("spazio_archiviazione")
        );
        
        profilo.setIdHardware(rs.getInt("id_hardware"));
        profilo.setSchedaVideo(rs.getString("scheda_video"));
        
        String tipoSistema = rs.getString("tipo_sistema");
        if (tipoSistema != null) {
            profilo.setTipoSistema(TipoSistema.valueOf(tipoSistema));
        }
        
        profilo.setDataCreazione(rs.getTimestamp("data_creazione"));
        
        return profilo;
    }

}
