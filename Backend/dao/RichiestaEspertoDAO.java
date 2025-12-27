package dao;

import java.sql.SQLException;
import java.util.List;
import entity.database.RichiestaEsperto;

public interface RichiestaEspertoDAO {
	
	int salva(RichiestaEsperto richiestaEsperto) throws SQLException;
    List<RichiestaEsperto> getByRichiesta(int idRichiesta) throws SQLException;
    List<RichiestaEsperto> getByEsperto(int idEsperto) throws SQLException;
    void aggiorna(RichiestaEsperto richiestaEsperto) throws SQLException;
    
    // Utilità per servlet
    boolean esisteAssegnazione(int idRichiesta, int idEsperto) throws SQLException;
    void eliminaByRichiesta(int idRichiesta) throws SQLException;
    int contaEspertiByRichiesta(int idRichiesta) throws SQLException;

}
