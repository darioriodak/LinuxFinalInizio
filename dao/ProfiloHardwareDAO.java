package dao;

import java.sql.SQLException;
import entity.database.ProfiloHardware;

public interface ProfiloHardwareDAO {
	
	int salva(ProfiloHardware profilo) throws SQLException;
    ProfiloHardware findById(int idHardware) throws SQLException;
    ProfiloHardware findByUtente(int idUtente) throws SQLException;
    void aggiorna(ProfiloHardware profilo) throws SQLException;
    void eliminaById(int idHardware) throws SQLException;

	
}
