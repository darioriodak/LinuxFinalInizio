package dao;

import java.sql.SQLException;
import java.util.List;
import entity.database.RichiestaDistribuzione;

public interface RichiestaDistribuzioneDAO {
	
	int salva(RichiestaDistribuzione rd) throws SQLException;
	List<RichiestaDistribuzione> getByRichiesta(int idRichiesta) throws SQLException;
	void eliminaByRichiesta(int idRichiesta) throws SQLException;
    boolean esisteAssociazione(int idRichiesta, int idDistribuzione) throws SQLException;

}
