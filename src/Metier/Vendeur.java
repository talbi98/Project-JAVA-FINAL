package Metier;

public class Vendeur extends Employe {
	private double commissionPct;

	public Vendeur(int id, String nom, String prenom, String login, String password, double commissionPct) {
		super(id, nom, prenom, login, password, "VENDEUR");
		this.commissionPct = commissionPct;
	}

	public double getCommissionPct() {
		return commissionPct;

	}

	public void setCommissionPct(double commissionPct) {

		this.commissionPct = commissionPct;

	}
}