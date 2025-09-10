package dao;

import java.sql.SQLException;
import java.util.List;
import entity.database.ValutazioneEsperto;

public interface ValutazioneEspertoDAO {
	
	int salva(ValutazioneEsperto valutazione) throws SQLException;
    List<ValutazioneEsperto> getByRichiesta(int idRichiesta) throws SQLException;
    List<ValutazioneEsperto> getByRichiestaEDistribuzione(int idRichiesta, int idDistribuzione) throws SQLException;
    List<ValutazioneEsperto> getByRichiestaEEsperto(int idRichiesta, int idEsperto) throws SQLException;
    
    
    boolean haValutatoEsperto(int idEsperto, int idRichiesta) throws SQLException;
    double getPunteggioMedio(int idRichiesta, int idDistribuzione) throws SQLException;
    int contaValutazioni(int idRichiesta) throws SQLException;

}
