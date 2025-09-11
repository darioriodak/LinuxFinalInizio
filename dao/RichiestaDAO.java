package dao;

import java.sql.SQLException;
import java.util.List;
import entity.database.Richiesta;
import enumerazioni.StatoRichiesta;

public interface RichiestaDAO {
	
	
	    
	    
	int salva(Richiesta richiesta) throws SQLException;
    Richiesta findById(int idRichiesta) throws SQLException;
	void aggiorna(Richiesta richiesta) throws SQLException;
	void eliminaById(int idRichiesta) throws SQLException;
	void aggiornaStato(int idRichiesta, StatoRichiesta nuovoStato) throws SQLException;
	    
	// Query specifiche per business logic
	List<Richiesta> findByUtente(int idUtente) throws SQLException;
	   
	    
	    
	    
	    
	

}
