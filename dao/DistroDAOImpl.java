package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entity.database.Distribuzione;
import entity.dto.DistribuzioneSelezionataDTO;
import entity.dto.DistribuzioneCompletaDTO;
import enumerazioni.CategoriaDistribuzione;

public class DistroDAOImpl implements DistroDAO{
	
private String ip, port, dbName, userName, pwd;
    
    public DistroDAOImpl(String ip, String port, String dbName, String userName, String pwd) {
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
    public Distribuzione findById(int id) throws SQLException {
        String query = "SELECT id_distribuzione, nome, versione, ambiente_desktop, categoria, descrizione, attiva " +
                      "FROM Distribuzione WHERE id_distribuzione = ?";
        Distribuzione distribuzione = null;
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    distribuzione = new Distribuzione(
                        rs.getString("nome"),
                        rs.getString("versione"),
                        rs.getString("ambiente_desktop")
                    );
                    distribuzione.setIdDistribuzione(rs.getInt("id_distribuzione"));
                    
                    // Gestione categoria enum (può essere NULL)
                    String categoriaStr = rs.getString("categoria");
                    if (categoriaStr != null && !categoriaStr.trim().isEmpty()) {
                        distribuzione.setCategoria(CategoriaDistribuzione.valueOf(categoriaStr));
                    }
                    
                    distribuzione.setDescrizione(rs.getString("descrizione"));
                    distribuzione.setAttiva(rs.getBoolean("attiva"));
                }
            }
        }
        return distribuzione;
    }
    
    @Override
    public List<Distribuzione> findAll() throws SQLException {
        String query = "SELECT id_distribuzione, nome, versione, ambiente_desktop, categoria, descrizione, attiva " +
                      "FROM Distribuzione WHERE attiva = true ORDER BY nome";
        List<Distribuzione> distribuzioni = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Distribuzione distribuzione = new Distribuzione(
                    rs.getString("nome"),
                    rs.getString("versione"),
                    rs.getString("ambiente_desktop")
                );
                distribuzione.setIdDistribuzione(rs.getInt("id_distribuzione"));
                
                String categoriaStr = rs.getString("categoria");
                if (categoriaStr != null && !categoriaStr.trim().isEmpty()) {
                    distribuzione.setCategoria(CategoriaDistribuzione.valueOf(categoriaStr));
                }
                
                distribuzione.setDescrizione(rs.getString("descrizione"));
                distribuzione.setAttiva(rs.getBoolean("attiva"));
                
                distribuzioni.add(distribuzione);
            }
        }
        return distribuzioni;
    }
    
    @Override
    public List<Distribuzione> findByName(String nome) throws SQLException {
        String query = "SELECT id_distribuzione, nome, versione, ambiente_desktop, categoria, descrizione, attiva " +
                      "FROM Distribuzione WHERE nome LIKE ? AND attiva = true";
        List<Distribuzione> distribuzioni = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, "%" + nome + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Distribuzione distribuzione = new Distribuzione(
                        rs.getString("nome"),
                        rs.getString("versione"),
                        rs.getString("ambiente_desktop")
                    );
                    distribuzione.setIdDistribuzione(rs.getInt("id_distribuzione"));
                    
                    String categoriaStr = rs.getString("categoria");
                    if (categoriaStr != null && !categoriaStr.trim().isEmpty()) {
                        distribuzione.setCategoria(CategoriaDistribuzione.valueOf(categoriaStr));
                    }
                    
                    distribuzione.setDescrizione(rs.getString("descrizione"));
                    distribuzione.setAttiva(rs.getBoolean("attiva"));
                    
                    distribuzioni.add(distribuzione);
                }
            }
        }
        return distribuzioni;
    }
    
    @Override
    public List<DistribuzioneSelezionataDTO> getDistribuzioniSelezionabili() throws SQLException {
        String query = "SELECT id_distribuzione, nome, versione, ambiente_desktop " +
                      "FROM Distribuzione WHERE attiva = true ORDER BY nome";
        List<DistribuzioneSelezionataDTO> distribuzioni = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                DistribuzioneSelezionataDTO dto = new DistribuzioneSelezionataDTO(
                    rs.getInt("id_distribuzione"),
                    rs.getString("nome"),
                    rs.getString("versione"),
                    rs.getString("ambiente_desktop")
                );
                distribuzioni.add(dto);
            }
        }
        return distribuzioni;
    }
    
    @Override
    public DistribuzioneCompletaDTO getDistribuzioneCompletaById(int id) throws SQLException {
        String query = "SELECT id_distribuzione, nome, versione, ambiente_desktop, categoria, descrizione " +
                      "FROM Distribuzione WHERE id_distribuzione = ? AND attiva = true";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    DistribuzioneCompletaDTO dto = new DistribuzioneCompletaDTO(
                        rs.getInt("id_distribuzione"),
                        rs.getString("nome"),
                        rs.getString("versione")
                    );
                    
                    dto.setAmbienteDesktop(rs.getString("ambiente_desktop"));
                    dto.setDescrizione(rs.getString("descrizione"));
                    
                    String categoriaStr = rs.getString("categoria");
                    if (categoriaStr != null && !categoriaStr.trim().isEmpty()) {
                        dto.setCategoria(categoriaStr);
                    }
                    
                    return dto;
                }
            }
        }
        return null;
    }
    
    @Override
    public List<DistribuzioneCompletaDTO> getDistribuzioniComplete(List<Integer> ids) throws SQLException {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Crea placeholder per IN clause (??,??,??)
        String placeholders = String.join(",", java.util.Collections.nCopies(ids.size(), "?"));
        String query = "SELECT id_distribuzione, nome, versione, ambiente_desktop, categoria, descrizione " +
                      "FROM Distribuzione WHERE id_distribuzione IN (" + placeholders + ") AND attiva = true " +
                      "ORDER BY nome";
        
        List<DistribuzioneCompletaDTO> distribuzioni = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            // Imposta i parametri per la IN clause
            for (int i = 0; i < ids.size(); i++) {
                pstmt.setInt(i + 1, ids.get(i));
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DistribuzioneCompletaDTO dto = new DistribuzioneCompletaDTO(
                        rs.getInt("id_distribuzione"),
                        rs.getString("nome"),
                        rs.getString("versione")
                    );
                    
                    dto.setAmbienteDesktop(rs.getString("ambiente_desktop"));
                    dto.setDescrizione(rs.getString("descrizione"));
                    
                    String categoriaStr = rs.getString("categoria");
                    if (categoriaStr != null && !categoriaStr.trim().isEmpty()) {
                        dto.setCategoria(categoriaStr);
                    }
                    
                    distribuzioni.add(dto);
                }
            }
        }
        return distribuzioni;
    }
    
    @Override
    public boolean esisteDistribuzione(int id) throws SQLException {
        String query = "SELECT COUNT(*) FROM Distribuzione WHERE id_distribuzione = ? AND attiva = true";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        }
    }
    
    @Override
    public int contaDistribuzioniAttive() throws SQLException {
        String query = "SELECT COUNT(*) FROM Distribuzione WHERE attiva = true";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
	
	

}
