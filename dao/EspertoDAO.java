package dao;

import java.sql.SQLException;
import java.util.List;
import entity.database.Esperto;

public interface EspertoDAO {
	
	
	    
	    // Operazioni CRUD bas
	int salva(Esperto esperto) throws SQLException;
	Esperto findById(int idEsperto) throws SQLException;
	void aggiorna(Esperto esperto) throws SQLException;
	void eliminaById(int idEsperto) throws SQLException;
	List<Esperto> getTuttiEspertiAttivi() throws SQLException;
	boolean esisteEsperto(int idEsperto) throws SQLException;
	Esperto findByIdUtente(int idUtente) throws SQLException;

}
