package entity.database;

public class RichiestaDistribuzione {
	
	private int idRichiesta;
	private int idDistribuzione;


	public RichiestaDistribuzione(int idRichiesta,int idDistribuzione) {
		this.idRichiesta = idRichiesta;
		this.idDistribuzione = idDistribuzione;
	}
	
	public int getIdRichiesta() {
		return idRichiesta;
	}

	public void setIdRichiesta(int idRichiesta) {
		this.idRichiesta = idRichiesta;
	}

	public int getIdDistribuzione() {
		return idDistribuzione;
	}

	public void setIdDistribuzione(int idDistribuzione) {
		this.idDistribuzione = idDistribuzione;
	}
	
	

}
