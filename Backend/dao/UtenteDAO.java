package dao;

import java.sql.SQLException;
import entity.database.Utente;

public interface UtenteDAO {
	
	// Metodi essenziali per il flusso principale
    int salva(Utente utente) throws SQLException;
    Utente findByEmail(String email) throws SQLException;  // Cerca per email anche se campo è "mail"
    Utente findById(int idUtente) throws SQLException;

}
