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

    // --- GESTION VEHICULES ---
    public void ajouterVehicule(Vehicule v) { vehiculeDAO.create(v); }
    public List<Vehicule> listerToutLeGarage() { return vehiculeDAO.findAll(); }
    
    public List<Vehicule> listerVehiculesElectriques() {
        return vehiculeDAO.findAll().stream()
                .filter(v -> v instanceof VehiculeElectrique)
                .collect(Collectors.toList());
    }
    
    public List<Vehicule> listerVehiculesThermique() {
        return vehiculeDAO.findAll().stream()
                .filter(v -> v instanceof VoitureThermique)
                .collect(Collectors.toList());
    }

    public void afficherRepartitionStock() {
        List<Vehicule> stock = vehiculeDAO.findAll();
        long nbElec = stock.stream().filter(v -> v instanceof VehiculeElectrique && "DISPO".equals(v.getStatut())).count();
        long nbTherm = stock.stream().filter(v -> v instanceof VoitureThermique && "DISPO".equals(v.getStatut())).count();
        System.out.println(" STATS STOCK : Elec=" + nbElec + " / Therm=" + nbTherm);
    }

    public List<Vehicule> getTop3VoituresLuxe() {
        return vehiculeDAO.findAll().stream()
                .filter(v -> "DISPO".equals(v.getStatut()))
                .sorted(Comparator.comparingDouble(Vehicule::getPrixVente).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }

    public Map<String, List<Vehicule>> grouperVehiculesParMarque() {
        return vehiculeDAO.findAll().stream()
                .filter(v -> "DISPO".equals(v.getStatut()))
                .collect(Collectors.groupingBy(Vehicule::getMarque));
    }

    // --- GESTION CLIENTS ---
    public void ajouterClient(Client c) { clientDAO.create(c); }
    public List<Client> listerClients() { return clientDAO.findAll(); }

    // --- GESTION EMPLOYES ---
    public void embaucherEmploye(Employe e) { employeDAO.create(e); }
    public List<Employe> listerEmployes() { return employeDAO.findAll(); }

    // --- GESTION VENTES ---
    public List<Vente> listerVentes() { return venteDAO.findAll(); }
    
    public void enregistrerVente(Vente vente) {
        venteDAO.create(vente);
        Vehicule v = vente.getVehicule();
        if(v != null) {
            v.setStatut("VENDU");
            vehiculeDAO.update(v);
        }
    }

    public boolean realiserVente(int idVehicule, int idClient, int idVendeur) throws Exception {
        Vehicule v = vehiculeDAO.findById(idVehicule);
        Client c = clientDAO.findById(idClient);
        Employe e = employeDAO.findById(idVendeur);
        
        if (v == null || c == null || e == null) throw new Exception("Données introuvables.");
        if (!"DISPO".equals(v.getStatut())) throw new Exception("Véhicule non disponible.");
        if (!(e instanceof Vendeur)) throw new Exception("L'employé n'est pas un vendeur.");

        Vente nouvelleVente = new Vente(v, c, (Vendeur) e);
        enregistrerVente(nouvelleVente);
        return true;
    }

    public double calculerPrixMoyenVentes() {
        return vehiculeDAO.findAll().stream()
                .filter(v -> "VENDU".equals(v.getStatut())) 
                .mapToDouble(Vehicule::getPrixVente)        
                .average()                            
                .orElse(0.0);                        
    }

    // --- GESTION ATELIER ---
    public List<Intervention> listerInterventions() { return interventionDAO.findAll(); }

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

    // --- GESTION FACTURES & IMPRESSION (TP 7) ---
    
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

    // Méthode 1 : Prépare le texte de la facture (String)
    public static String editerFacture(IFacturable element) {
        StringBuilder sb = new StringBuilder();
        sb.append("============= MONACO GARAGE =============\n");
        sb.append("FACTURE REF : ").append(element.getReference()).append("\n");
        sb.append("DATE        : ").append(new java.util.Date()).append("\n");
        sb.append("-----------------------------------------\n");
        
        if (element.getClientFacture() != null) {
            sb.append("CLIENT      : ").append(element.getClientFacture().getNom()).append(" ").append(element.getClientFacture().getPrenom()).append("\n");
        } else {
            sb.append("CLIENT      : Garage / Interne\n");
        }
        
        sb.append("DESCRIPTION : ").append(element.getDescriptionFacture()).append("\n");
        sb.append("-----------------------------------------\n");
        sb.append("MONTANT HT  : ").append(String.format("%.2f", element.getMontantTotal() * 0.8)).append(" euros\n");
        sb.append("TVA (20%)   : ").append(String.format("%.2f", element.getMontantTotal() * 0.2)).append(" euros\n");
        sb.append("TOTAL TTC   : ").append(String.format("%.2f", element.getMontantTotal())).append(" euros\n");
        sb.append("=========================================\n");
        return sb.toString();
    }

    // Méthode 2 : Crée le fichier TXT sur le disque (TP 7 - Exercice 1)
    public String imprimerFactureTxt(IFacturable element) throws IOException {
        // Le fichier sera créé à la racine du projet, comme demandé dans le TP 7
        String nomFichier = "Facture_" + element.getReference() + ".txt";
        
        // Récupération du texte formaté
        String contenu = editerFacture(element);
        
        // Écriture dans le fichier avec PrintWriter et FileWriter
        try (PrintWriter writer = new PrintWriter(new FileWriter(nomFichier))) {
            writer.print(contenu);
        }
        
        return nomFichier;
    }
}