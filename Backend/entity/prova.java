package entity;

import java.sql.SQLException;

import dao.DistroDAO;
import dao.DistroDAOImpl;
import dao.UtenteDAO;
import dao.UtenteDAOImpl;
import entity.database.Distribuzione;
import entity.database.Utente;
import enumerazioni.LivelloEsperienza;

public class prova {

	public static void main(String[] args){
		
		UtenteDAO d = new UtenteDAOImpl("fedora","3306","distroFinal","dario","Telemarketing56-");
		DistroDAO di = new DistroDAOImpl("fedora","3306","distroFinal","dario","Telemarketing56-");
		Utente u = new Utente("paodario1@gmail.com","1234",LivelloEsperienza.INTERMEDIO,"gaming");
		Distribuzione distro = null;
		try {
			distro = di.findById(2);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(distro != null) {
			System.out.println(distro);
		}
		

	}

}
