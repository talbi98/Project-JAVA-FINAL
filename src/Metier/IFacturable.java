package Metier;

public interface IFacturable {

	double getMontantTotal();

	String getDescriptionFacture();

	Client getClientFacture();

	String getReference();
}
