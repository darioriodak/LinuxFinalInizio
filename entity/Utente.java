package entity;

import enumerazioni.LivelloEsperienza;

public class Utente {
	
	private int idUtente;
    private String mail;
    private String password;
    private LivelloEsperienza livelloEsperienza;
    private String scopoUso;
    private java.sql.Timestamp dataRegistrazione;


    public Utente(String mail, String password, LivelloEsperienza livelloEsperienza, String scopoUso) {
        this.mail = mail;
        this.password = password;
        this.livelloEsperienza = livelloEsperienza;
        this.scopoUso = scopoUso;
    }

    
    public int getIdUtente() { return idUtente; }
    public void setIdUtente(int idUtente) { this.idUtente = idUtente; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LivelloEsperienza getLivelloEsperienza() { return livelloEsperienza; }
    public void setLivelloEsperienza(LivelloEsperienza livelloEsperienza) { this.livelloEsperienza = livelloEsperienza; }

    public String getScopoUso() { return scopoUso; }
    public void setScopoUso(String scopoUso) { this.scopoUso = scopoUso; }

    public java.sql.Timestamp getDataRegistrazione() { return dataRegistrazione; }
    public void setDataRegistrazione(java.sql.Timestamp dataRegistrazione) { this.dataRegistrazione = dataRegistrazione; }	

}
