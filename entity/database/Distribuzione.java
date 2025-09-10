package entity.database;
import enumerazioni.CategoriaDistribuzione;

public class Distribuzione {
	
	private int idDistribuzione;
    private String nome;
    private String versione;
    private String ambienteDesktop;
    private CategoriaDistribuzione categoria;
    private String descrizione;
    private boolean attiva;

  

    public Distribuzione(String nome, String versione, String ambienteDesktop) {
        this.nome = nome;
        this.versione = versione;
        this.ambienteDesktop = ambienteDesktop;
        this.attiva = true;
    }

    // Getter e Setter
    public int getIdDistribuzione() { return idDistribuzione; }
    public void setIdDistribuzione(int idDistribuzione) { this.idDistribuzione = idDistribuzione; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getVersione() { return versione; }
    public void setVersione(String versione) { this.versione = versione; }

    public String getAmbienteDesktop() { return ambienteDesktop; }
    public void setAmbienteDesktop(String ambienteDesktop) { this.ambienteDesktop = ambienteDesktop; }

    public CategoriaDistribuzione getCategoria() { return categoria; }
    public void setCategoria(CategoriaDistribuzione categoria) { this.categoria = categoria; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public boolean isAttiva() { return attiva; }
    public void setAttiva(boolean attiva) { this.attiva = attiva; }
    
    public String toString() {
    	return "nome " + this.nome + " versione " + this.versione + " ambiente desktop " + this.ambienteDesktop + " categoria " + this.categoria + " descrizione " + this.descrizione;
    }

}
