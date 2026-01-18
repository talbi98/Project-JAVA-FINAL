package Metier;

public class VehiculeElectrique extends Vehicule {
	private int batterieKwh;
	private int autonomie;

	public VehiculeElectrique(int id, String marque, String modele, double prix, int batterieKwh, int autonomie) {
		super(id, marque, modele, prix);
		this.batterieKwh = batterieKwh;
		this.autonomie = autonomie;

	}

	public int getBatterieKwh() {
		return batterieKwh;
	}

	public void setBatterieKwh(int batterieKwh) {
		this.batterieKwh = batterieKwh;
	}

	public int getAutonomie() {
		return autonomie;
	}

	public void setAutonomie(int autonomie) {
		this.autonomie = autonomie;
	}

	@Override
	public double calculerTaxeLuxe() {

		return 0.0;
	}
}