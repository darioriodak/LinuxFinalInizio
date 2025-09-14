package dao;

import entity.dto.DistroSummaryDTO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione DAO per DistroSummary
 */
public class DistroSummaryDAOImpl implements DistroSummaryDAO {
    
    private String ip;
    private String port;
    private String dbName;
    private String userName;
    private String password;
    
    public DistroSummaryDAOImpl(String ip, String port, String dbName, String userName, String password) {
        this.ip = ip;
        this.port = port;
        this.dbName = dbName;
        this.userName = userName;
        this.password = password;
    }
    
    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://" + ip + ":" + port + "/" + dbName;
        return DriverManager.getConnection(url, userName, password);
    }
    
    @Override
    public List<DistroSummaryDTO> getAllDistroSummaries() throws SQLException {
        List<DistroSummaryDTO> summaries = new ArrayList<>();
        
        String query = "SELECT id, idDistribuzione, nomeDisplay, descrizioneBreve, " +
                      "descrizioneDettaglio, icona, coloreHex, livelloDifficolta, " +
                      "punteggioPopolarita FROM DistroSummary " +
                      "WHERE attiva = TRUE ORDER BY ordinePriorita ASC";
        
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                DistroSummaryDTO summary = mapResultSetToDistroSummary(rs);
                summaries.add(summary);
            }
        }
        
        return summaries;
    }
    
    @Override
    public DistroSummaryDTO getDistroSummaryById(int idDistribuzione) throws SQLException {
        String query = "SELECT id, idDistribuzione, nomeDisplay, descrizioneBreve, " +
                      "descrizioneDettaglio, icona, coloreHex, livelloDifficolta, " +
                      "punteggioPopolarita FROM DistroSummary " +
                      "WHERE idDistribuzione = ? AND attiva = TRUE";
        
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            
            stmt.setInt(1, idDistribuzione);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDistroSummary(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Mappa ResultSet a DistroSummaryDTO
     */
    private DistroSummaryDTO mapResultSetToDistroSummary(ResultSet rs) throws SQLException {
        DistroSummaryDTO summary = new DistroSummaryDTO();
        
        summary.setId(rs.getInt("id"));
        summary.setIdDistribuzione(rs.getInt("idDistribuzione"));
        summary.setNomeDisplay(rs.getString("nomeDisplay"));
        summary.setDescrizioneBreve(rs.getString("descrizioneBreve"));
        summary.setDescrizioneDettaglio(rs.getString("descrizioneDettaglio"));
        summary.setIcona(rs.getString("icona"));
        summary.setColoreHex(rs.getString("coloreHex"));
        summary.setLivelloDifficolta(rs.getString("livelloDifficolta"));
        summary.setPunteggioPopolarita(rs.getInt("punteggioPopolarita"));
        
        return summary;
    }
}