package dao;

import java.sql.SQLException;
import java.util.List;
import entity.database.Notifica;
import enumerazioni.StatoNotificaLettura;

public interface NotificaDAO {
	
	int salva(Notifica notifica) throws SQLException;
    List<Notifica> getNotificheNonLetteByEsperto(int idEsperto) throws SQLException;
    List<Notifica> getTutteNotificheByEsperto(int idEsperto) throws SQLException;
    void marcaComeLetta(int idNotifica) throws SQLException;
    void aggiornaStato(int idNotifica, StatoNotificaLettura stato) throws SQLException;
    
    // Utilità
    int contaNotificheNonLette(int idEsperto) throws SQLException;

}
