package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import entity.database.Utente;
import enumerazioni.LivelloEsperienza;

public class UtenteDAOImpl implements UtenteDAO{
	
private String ip, port, dbName, userName, pwd;
    
    public UtenteDAOImpl(String ip, String port, String dbName, String userName, String pwd) {
        this.ip = ip;
        this.port = port;
        this.dbName = dbName;
        this.userName = userName;
        this.pwd = pwd;
    }
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + dbName
               + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
               userName, pwd);
    }
    
    
    public int salva(Utente utente) throws SQLException {
        String sql = "INSERT INTO Utente(mail, password, livello_esperienza, scopo_uso) VALUES(?, ?, ?, ?)";
        int idGenerato = -1;
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, utente.getMail());
            pstmt.setString(2, utente.getPassword()); 
            pstmt.setString(3, utente.getLivelloEsperienza().toString()); // ENUM to String
            pstmt.setString(4, utente.getScopoUso());
            
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creazione utente fallita, nessuna riga modificata.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    idGenerato = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creazione utente fallita, nessun ID generato.");
                }
            }
        }
        return idGenerato;
    }
    
    
    public Utente findByEmail(String email) throws SQLException {
        String sql = "SELECT id_utente, mail, password, livello_esperienza, scopo_uso, data_registrazione " +
                    "FROM Utente WHERE mail = ?";
        Utente utente = null;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    utente = new Utente(
                        rs.getString("mail"),
                        rs.getString("password"), 
                        LivelloEsperienza.valueOf(rs.getString("livello_esperienza")), // String to ENUM
                        rs.getString("scopo_uso")
                    );
                    utente.setIdUtente(rs.getInt("id_utente"));
                    utente.setDataRegistrazione(rs.getTimestamp("data_registrazione"));
                }
            }
        }
        return utente;
    }
    
    
    public Utente findById(int idUtente) throws SQLException {
        String sql = "SELECT id_utente, mail, password, livello_esperienza, scopo_uso, data_registrazione " +
                    "FROM Utente WHERE id_utente = ?";
        Utente utente = null;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idUtente);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    utente = new Utente(
                        rs.getString("mail"),
                        rs.getString("password"),
                        LivelloEsperienza.valueOf(rs.getString("livello_esperienza")),
                        rs.getString("scopo_uso")
                    );
                    utente.setIdUtente(rs.getInt("id_utente"));
                    utente.setDataRegistrazione(rs.getTimestamp("data_registrazione"));
                }
            }
        }
        return utente;
    }
	

}
