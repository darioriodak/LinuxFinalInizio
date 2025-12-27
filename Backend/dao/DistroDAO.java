package dao;

import java.sql.SQLException;
import java.util.List;
import entity.database.Distribuzione;
import entity.dto.DistribuzioneSelezionataDTO;
import entity.dto.DistribuzioneCompletaDTO;

public interface DistroDAO {
	
	// Metodi essenziali per il flusso principale
    Distribuzione findById(int id) throws SQLException;
    List<Distribuzione> findAll() throws SQLException;
    List<Distribuzione> findByName(String nome) throws SQLException;
    
    // Metodi specifici per DTO (ottimizzati per frontend)
    List<DistribuzioneSelezionataDTO> getDistribuzioniSelezionabili() throws SQLException;
    List<DistribuzioneCompletaDTO> getDistribuzioniComplete(List<Integer> ids) throws SQLException;
    DistribuzioneCompletaDTO getDistribuzioneCompletaById(int id) throws SQLException;
    
    // Utilità
    boolean esisteDistribuzione(int id) throws SQLException;
    int contaDistribuzioniAttive() throws SQLException;

}
