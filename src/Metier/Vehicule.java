package Metier;

public abstract class Vehicule {
    protected int id;
    protected String marque;
    protected String modele;
    protected String immatriculation;
    protected double prixVente;
    protected String statut; 

    public Vehicule(int id, String marque, String modele, double prixVente) {
        this.id = id;
        this.marque = marque;
        this.modele = modele;
        this.prixVente = prixVente;
        this.statut = "DISPO";
    }

    
    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMarque() {
		return marque;
	}

	public void setMarque(String marque) {
		this.marque = marque;
	}

	public String getModele() {
		return modele;
	}

	public void setModele(String modele) {
		this.modele = modele;
	}

	public String getImmatriculation() {
		return immatriculation;
	}

	public void setImmatriculation(String immatriculation) {
		this.immatriculation = immatriculation;
	}

	public double getPrixVente() {
		return prixVente;
	}

	public void setPrixVente(double prixVente) {
		this.prixVente = prixVente;
	}

	public String getStatut() {
		return statut;
	}

	public void setStatut(String statut) {
		this.statut = statut;
	}
	
	
    public abstract double calculerTaxeLuxe();


	@Override
    public String toString() {
        return marque + " " + modele + " (" + prixVente + "€)";
    }
}
