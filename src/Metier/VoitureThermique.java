package Metier;

public class VoitureThermique extends Vehicule {
    private int emissionCo2;
    private int cylindree;

    public VoitureThermique(int id, String marque, String modele, double prix, int emissionCo2, int cylindree) {
        super(id, marque, modele, prix); 
        this.emissionCo2 = emissionCo2;
        this.cylindree = cylindree;
    }
    
    

    public int getEmissionCo2() {
		return emissionCo2;
	}



	public void setEmissionCo2(int emissionCo2) {
		this.emissionCo2 = emissionCo2;
	}


	public int getCylindree() {
		return cylindree;
	}




	public void setCylindree(int cylindree) {
		this.cylindree = cylindree;
	}


	
    public double calculerTaxeLuxe() {
        if (emissionCo2 > 200) return 20000.0;
        return 500.0;
    }
    

}