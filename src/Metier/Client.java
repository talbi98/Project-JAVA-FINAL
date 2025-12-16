package Metier;

public class Client {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String vipLevel; 

    public Client(int id, String nom, String prenom, String email, String telephone, String vipLevel) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.vipLevel = vipLevel;
    }

    public Client(String nom, String prenom, String email, String telephone) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.vipLevel = "STANDARD";
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
    
    
    public void setNom(String nom) {
    	
    	this.nom = nom;
    	
    }
    
    public String getPrenom() { 
    	
    	return prenom; 
    	
    }
    
    
    public void setPrenom(String prenom) {
    	this.prenom = prenom; 
    	
    }
    
    public String getEmail() { 
    	
    	return email; 
    	
    }
    
    public void setEmail(String email) { 
    	this.email = email; 
    	
    }
    
    public String getTelephone() { 
    	return telephone; 
    	
    }
    public void setTelephone(String telephone) {
    	
    	this.telephone = telephone;
    	
    }
    
    
    public String getVipLevel() {
    	
    	return vipLevel; 
    	}
    
    public void setVipLevel(String vipLevel) { 
    	this.vipLevel = vipLevel; 
    	
    }

    @Override
    public String toString() {
        return nom.toUpperCase() + " " + prenom + " (" + vipLevel + ")";
    }
}
