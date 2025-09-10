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

import entity.database.Richiesta;
import enumerazioni.StatoRichiesta;
import enumerazioni.ModalitaSelezione;

public class RichiestaDAOImpl implements RichiestaDAO {
	
private String ip, port, dbName, userName, pwd;
    
    public RichiestaDAOImpl(String ip, String port, String dbName, String userName, String pwd) {
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
    public int salva(Richiesta richiesta) throws SQLException {
        String sql = "INSERT INTO Richiesta (id_utente, id_hardware, stato_richiesta, " +
                    "modalita_selezione, max_esperti, scadenza, note_aggiuntive) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        int idGenerato = -1;
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, richiesta.getIdUtente());
            
            // id_hardware può essere null
            if (richiesta.getIdHardware() != null) {
                pstmt.setInt(2, richiesta.getIdHardware());
            } else {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            }
            
            pstmt.setString(3, richiesta.getStatoRichiesta().name());
            pstmt.setString(4, richiesta.getModalitaSelezione().name());
            pstmt.setInt(5, richiesta.getMaxEsperti());
            
            // scadenza può essere null
            if (richiesta.getScadenza() != null) {
                pstmt.setTimestamp(6, richiesta.getScadenza());
            } else {
                pstmt.setNull(6, java.sql.Types.TIMESTAMP);
            }
            
            pstmt.setString(7, richiesta.getNoteAggiuntive());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creazione richiesta fallita, nessuna riga modificata.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    idGenerato = generatedKeys.getInt(1);
                    richiesta.setIdRichiesta(idGenerato);
                } else {
                    throw new SQLException("Creazione richiesta fallita, nessun ID generato.");
                }
            }
        }
        return idGenerato;
    }
    
    @Override
    public Richiesta findById(int idRichiesta) throws SQLException {
        String sql = "SELECT id_richiesta, id_utente, id_hardware, data_orario_creazione, " +
                    "stato_richiesta, modalita_selezione, max_esperti, scadenza, note_aggiuntive " +
                    "FROM Richiesta WHERE id_richiesta = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idRichiesta);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRichiesta(rs);
                }
            }
        }
        return null;
    }
    
    @Override
    public void aggiorna(Richiesta richiesta) throws SQLException {
        String sql = "UPDATE Richiesta SET id_hardware = ?, stato_richiesta = ?, " +
                    "modalita_selezione = ?, max_esperti = ?, scadenza = ?, note_aggiuntive = ? " +
                    "WHERE id_richiesta = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (richiesta.getIdHardware() != null) {
                pstmt.setInt(1, richiesta.getIdHardware());
            } else {
                pstmt.setNull(1, java.sql.Types.INTEGER);
            }
            
            pstmt.setString(2, richiesta.getStatoRichiesta().name());
            pstmt.setString(3, richiesta.getModalitaSelezione().name());
            pstmt.setInt(4, richiesta.getMaxEsperti());
            
            if (richiesta.getScadenza() != null) {
                pstmt.setTimestamp(5, richiesta.getScadenza());
            } else {
                pstmt.setNull(5, java.sql.Types.TIMESTAMP);
            }
            
            pstmt.setString(6, richiesta.getNoteAggiuntive());
            pstmt.setInt(7, richiesta.getIdRichiesta());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Aggiornamento richiesta fallito, nessuna riga modificata.");
            }
        }
    }
    
    @Override
    public void eliminaById(int idRichiesta) throws SQLException {
        String sql = "DELETE FROM Richiesta WHERE id_richiesta = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idRichiesta);
            pstmt.executeUpdate();
        }
    }
    
    @Override
    public List<Richiesta> findByUtente(int idUtente) throws SQLException {
        String sql = "SELECT id_richiesta, id_utente, id_hardware, data_orario_creazione, " +
                    "stato_richiesta, modalita_selezione, max_esperti, scadenza, note_aggiuntive " +
                    "FROM Richiesta WHERE id_utente = ? ORDER BY data_orario_creazione DESC";
        List<Richiesta> richieste = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idUtente);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    richieste.add(mapResultSetToRichiesta(rs));
                }
            }
        }
        return richieste;
    }
    
    private Richiesta mapResultSetToRichiesta(ResultSet rs) throws SQLException {
        Richiesta richiesta = new Richiesta(
            rs.getInt("id_utente"),
            ModalitaSelezione.valueOf(rs.getString("modalita_selezione").toUpperCase())
        );
        
        richiesta.setIdRichiesta(rs.getInt("id_richiesta"));
        
        // id_hardware può essere null
        int idHardware = rs.getInt("id_hardware");
        if (!rs.wasNull()) {
            richiesta.setIdHardware(idHardware);
        }
        
        richiesta.setDataOrarioCreazione(rs.getTimestamp("data_orario_creazione"));
        richiesta.setStatoRichiesta(StatoRichiesta.valueOf(rs.getString("stato_richiesta").toUpperCase()));
        richiesta.setMaxEsperti(rs.getInt("max_esperti"));
        richiesta.setScadenza(rs.getTimestamp("scadenza"));
        richiesta.setNoteAggiuntive(rs.getString("note_aggiuntive"));
        
        return richiesta;
    }

}
