package dao;

import entity.dto.DistroSummaryDTO;
import java.sql.SQLException;
import java.util.List;

public interface DistroSummaryDAO {
	
	/**
     * Recupera tutte le distribuzioni summary attive ordinate per priorità
     */
    List<DistroSummaryDTO> getAllDistroSummaries() throws SQLException;
    
    /**
     * Recupera summary di una distribuzione specifica
     */
    DistroSummaryDTO getDistroSummaryById(int idDistribuzione) throws SQLException;

}
