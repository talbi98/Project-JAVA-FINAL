package Metier;

public class Mecanicien extends Employe {
    private String specialite; 

    public Mecanicien(int id, String nom, String prenom, String login, String password, String specialite) {
        super(id, nom, prenom, login, password, "MECANICIEN");
        this.specialite = specialite;
    }

    public String getSpecialite() {
    	return specialite; 
    	
    }
    
    
    public void setSpecialite(String specialite) { 
    	this.specialite = specialite; 
    	
    }
}
