package Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import DAO.ClientDAO;
import DAO.EmployeDAO;
import DAO.InterventionDAO;
import DAO.VehiculeDAO;
import DAO.VenteDAO;
import Metier.Client;
import Metier.Employe;
import Metier.IFacturable;
import Metier.Intervention;
import Metier.Vehicule;
import Metier.VehiculeElectrique;
import Metier.Vendeur;
import Metier.Vente;
import Metier.VoitureThermique;

public class GarageService {

	private InterventionDAO interventionDAO = new InterventionDAO();
	private EmployeDAO employeDAO = new EmployeDAO();
	private VehiculeDAO vehiculeDAO = new VehiculeDAO();
	private ClientDAO clientDAO = new ClientDAO();
	private VenteDAO venteDAO = new VenteDAO();

	public void ajouterVehicule(Vehicule v) {
		vehiculeDAO.create(v);
	}

	public List<Vehicule> listerToutLeGarage() {
		return vehiculeDAO.findAll();
	}

	public void ajouterClient(Client c) {
		clientDAO.create(c);
	}

	public List<Client> listerClients() {
		return clientDAO.findAll();
	}

	public List<Employe> listerEmployes() {
		return employeDAO.findAll();
	}

	public List<Vente> listerVentes() {
		return venteDAO.findAll();
	}

	public void enregistrerVente(Vente vente) {
		venteDAO.create(vente);

		Vehicule v = vente.getVehicule();
		if (v != null) {
			v.setStatut("VENDU");
			vehiculeDAO.update(v);
		}

		Client c = vente.getClient();
		if (c != null) {
			c.setVipLevel("VIP");
			clientDAO.update(c);
		}
	}

	public double calculerPrixMoyenVentes() {
		return vehiculeDAO.findAll().stream().filter(v -> "VENDU".equals(v.getStatut()))
				.mapToDouble(Vehicule::getPrixVente).average().orElse(0.0);
	}

	public List<Vehicule> getTop3VoituresLuxe() {
		return vehiculeDAO.findAll().stream().filter(v -> "DISPO".equals(v.getStatut()))
				.sorted(Comparator.comparingDouble(Vehicule::getPrixVente).reversed()).limit(3)
				.collect(Collectors.toList());
	}

	public List<Intervention> listerInterventions() {
		return interventionDAO.findAll();
	}

	public void creerIntervention(Intervention i) {
		interventionDAO.create(i);
		Vehicule v = i.getVehicule();
		if (v != null) {
			v.setStatut("ATELIER");
			vehiculeDAO.update(v);
		}
	}

	public void terminerIntervention(Intervention i) {
		i.setStatut("TERMINE");
		i.setDateFin(new java.sql.Date(System.currentTimeMillis()));
		interventionDAO.update(i);

		Vehicule v = i.getVehicule();
		if (v != null) {
			v.setStatut("DISPO");
			vehiculeDAO.update(v);
		}
	}

	public List<IFacturable> listerHistoriqueFactures() {
		List<IFacturable> historique = new ArrayList<>();
		historique.addAll(venteDAO.findAll());

		List<Intervention> interventions = interventionDAO.findAll();
		for (Intervention i : interventions) {
			if ("TERMINE".equals(i.getStatut())) {
				historique.add(i);
			}
		}
		return historique;
	}

	public static String editerFacture(IFacturable element) {
		StringBuilder sb = new StringBuilder();
		sb.append("============= MONACO GARAGE =============\n");
		sb.append("FACTURE REF : ").append(element.getReference()).append("\n");
		sb.append("DATE        : ").append(new java.util.Date()).append("\n");
		sb.append("-----------------------------------------\n");

		if (element.getClientFacture() != null) {
			sb.append("CLIENT      : ").append(element.getClientFacture().getNom()).append("\n");
		} else {
			sb.append("CLIENT      : Garage / Interne\n");
		}

		sb.append("DESCRIPTION : ").append(element.getDescriptionFacture()).append("\n");
		sb.append("-----------------------------------------\n");
		// Calculs
		double total = element.getMontantTotal();
		sb.append("MONTANT HT  : ").append(String.format("%.2f", total * 0.8)).append(" euros\n");
		sb.append("TVA (20%)   : ").append(String.format("%.2f", total * 0.2)).append(" euros\n");
		sb.append("TOTAL TTC   : ").append(String.format("%.2f", total)).append(" euros\n");
		sb.append("=========================================\n");
		return sb.toString();
	}

	public String imprimerFactureTxt(IFacturable element) throws IOException {
		String nomFichier = "Facture_" + element.getReference() + ".txt";

		try (PrintWriter writer = new PrintWriter(new FileWriter(nomFichier))) {
			writer.print(editerFacture(element));
		}
		return nomFichier;
	}
}