package Metier;

import java.sql.Date;

public class Vente {
    private int id;
    private Date dateVente;
    private double montantFinal;
    
   
    private Vehicule vehicule;
    private Client client;
    private Vendeur vendeur;

    public Vente(int id, Date dateVente, double montantFinal, Vehicule vehicule, Client client, Vendeur vendeur) {
        this.id = id;
        this.dateVente = dateVente;
        this.montantFinal = montantFinal;
        this.vehicule = vehicule;
        this.client = client;
        this.vendeur = vendeur;
    }
    
    public Vente(Vehicule vehicule, Client client, Vendeur vendeur) {
        this.dateVente = new Date(System.currentTimeMillis()); 
        this.montantFinal = vehicule.getPrixVente(); 
        this.vehicule = vehicule;
        this.client = client;
        this.vendeur = vendeur;
    }

    
    public Date getdateVente() {
    	return dateVente ;
    }

    
    public int getId() { 
    	return id; 
    	
    }
    
    public void setId(int id) {
    	this.id = id; 
    	
    }
    
    public Vehicule getVehicule() { 
    	return vehicule; 
    	}
    
    public Client getClient() {
    	return client; 
    	}
    
    public Vendeur getVendeur() { 
    	return vendeur; 
    	}
    
    public double getMontantFinal() {
    	return montantFinal; 
    }
}