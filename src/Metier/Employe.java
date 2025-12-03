package Metier;

public abstract class Employe {
    protected int id;
    protected String nom;
    protected String prenom;
    protected String login;
    protected String password;
    protected String typeEmploye; // "vendeur" ou alors "mecanicien"

    public Employe(int id, String nom, String prenom, String login, String password, String typeEmploye) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.login = login;
        this.password = password;
        this.typeEmploye = typeEmploye;
    }
    
    public int getId() {
    	return id; 
    	
    }
    public void setId(int id) {
    	this.id = id;
    }
    public String getNom() { 
    	return nom;
    	
    }
    public String getPrenom() { 
    	return prenom; 
    	
    }
    
    @Override
    public String toString() {
        return nom + " " + prenom + " (" + typeEmploye + ")";
    }
}
