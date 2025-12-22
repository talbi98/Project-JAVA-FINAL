package Service;

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
import Metier.Mecanicien;
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
    private VenteDAO VenteDAO = new VenteDAO();
    
    
    

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
    
    
    public void embaucherEmploye(Employe e) {
        employeDAO.create(e);
        System.out.println("RH : Nouvel employé recruté -> " + e.getNom());
    }
    
    public void inscrireClient(Client c) {
        clientDAO.create(c);
        System.out.println("CRM : Nouveau client VIP enregistré -> " + c.getNom());
    }
    

    public boolean vendreVehicule(int idVehicule, int idClient) {
        Vehicule v = vehiculeDAO.findById(idVehicule);
        Client c = clientDAO.findById(idClient);
        
        if (v != null && c != null && "DISPO".equals(v.getStatut())) {
            
            v.setStatut("VENDU"); 
            
            vehiculeDAO.update(v);
            
            System.out.println("SUCCES : La " + v.getMarque() + " a été vendue à " + c.getNom());
            return true;
        } else {
            System.err.println("ECHEC : Véhicule introuvable, déjà vendu ou Client inconnu.");
            return false;
        }
    }

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
    
    
    
    
    
    
    
    
    public static String editerFacture(IFacturable element) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("============= MONACO GARAGE =============\n");
        sb.append("FACTURE REF : ").append(element.getReference()).append("\n");
        
        if (element.getClientFacture() != null) {
            sb.append("CLIENT      : ").append(element.getClientFacture().getNom()).append("\n");
        } else {
            sb.append("CLIENT      : Client de passage\n");
        }
        
        sb.append("-----------------------------------------\n");
        sb.append("OBJET       : ").append(element.getDescriptionFacture()).append("\n");
        sb.append("MONTANT HT  : ").append(element.getMontantTotal() * 0.8).append(" €\n");
        sb.append("TVA (20%)   : ").append(element.getMontantTotal() * 0.2).append(" €\n");
        sb.append("-----------------------------------------\n");
        sb.append("TOTAL TTC   : ").append(element.getMontantTotal()).append(" €\n");
        sb.append("=========================================\n");
        
        return sb.toString();
    }
    
    
    public Intervention planifierIntervention(int idVehicule, int idMecanicien, String description) throws GarageException {
        Vehicule v = vehiculeDAO.findById(idVehicule);
        Employe e = employeDAO.findById(idMecanicien);
        
        if (v == null) throw new GarageException("Véhicule introuvable");
        if (!(e instanceof Mecanicien)) throw new GarageException("Cet employé n'est pas un mécanicien !");

        Intervention i = new Intervention(v, (Mecanicien) e, description, 500.0);
        
        interventionDAO.create(i); 
        
        System.out.println("Succès : Intervention enregistrée en BDD avec l'ID " + i.getId());
        return i;
    }
    
    public void terminerIntervention(int idIntervention) {

    	Intervention i = interventionDAO.findById(idIntervention);
        
        if (i != null && "EN_COURS".equals(i.getStatut())) {
            
            i.setStatut("TERMINE");
            i.setDateFin(new java.sql.Date(System.currentTimeMillis())); 
            
            Vehicule v = i.getVehicule();
            v.setStatut("DISPO"); 
            
            interventionDAO.update(i);
            vehiculeDAO.update(v);
            
            System.out.println("✅ Intervention terminée : " + v.getMarque() + " est disponible.");
        } else {
            System.err.println("❌ Erreur : Intervention introuvable ou déjà terminée.");
        }
    }
    
    
    
    
    public boolean realiserVente(int idVehicule, int idClient, int idVendeur) throws Exception {

    	Vehicule v = vehiculeDAO.findById(idVehicule);
        Client c = clientDAO.findById(idClient);
        Employe e = employeDAO.findById(idVendeur);
        
        
        if (v == null) throw new Exception("Véhicule introuvable (ID " + idVehicule + ")");
        if (c == null) throw new Exception("Client introuvable (ID " + idClient + ")");
        if (e == null) throw new Exception("Vendeur introuvable (ID " + idVendeur + ")");
        

        if (!"DISPO".equals(v.getStatut())) {
            throw new Exception("Impossible de vendre : Le véhicule n'est pas DISPO (Statut actuel : " + v.getStatut() + ")");
        }
        
        if (!(e instanceof Vendeur)) {
            throw new Exception("Erreur : L'employé " + e.getNom() + " n'est pas un vendeur !");
        }

        Vente nouvelleVente = new Vente(v, c, (Vendeur) e);
        
        VenteDAO.create(nouvelleVente);
        

        v.setStatut("VENDU");

        vehiculeDAO.update(v);
        
        System.out.println("$$$ TRANSACTION REUSSIE : " + v.getMarque() + " vendue à " + c.getNom());
        return true;
    }
    /*
    public double calculerChiffreAffaires() {
        // On récupère toutes les ventes réelles enregistrées en BDD
        List<Vente> toutesLesVentes = VenteDAO.findAll(); 
        
        // On fait la somme des montants finaux de la table 'vente'
        return toutesLesVentes.stream()
                .mapToDouble(Vente::getMontantFinal)
                .sum(); // .sum() pour le CA, .average() pour le prix moyen
    }
    */
    
    
    public double calculerPrixMoyenVentes() {
        List<Vehicule> tout = vehiculeDAO.findAll();
        
        return tout.stream()
                .filter(v -> "VENDU".equals(v.getStatut())) 
                .mapToDouble(Vehicule::getPrixVente)        
                .average()                                   
                .orElse(0.0);                                
    }

   
    
    public void afficherRepartitionStock() {
        List<Vehicule> stock = vehiculeDAO.findAll();
        
        long nbElec = stock.stream()
                .filter(v -> v instanceof Metier.VehiculeElectrique && "DISPO".equals(v.getStatut()))
                .count();
                
        long nbTherm = stock.stream()
                .filter(v -> v instanceof Metier.VoitureThermique && "DISPO".equals(v.getStatut()))
                .count();
                
        System.out.println(" STATS STOCK :");
        System.out.println("- Électriques : " + nbElec);
        System.out.println("- Thermiques  : " + nbTherm);
    }
    
    
    public List<Vehicule> getTop3VoituresLuxe() {
        return vehiculeDAO.findAll().stream()
                .filter(v -> "DISPO".equals(v.getStatut()))
                .sorted(Comparator.comparingDouble(Vehicule::getPrixVente).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }

       
    public List<Vehicule> rechercherParBudget(double budgetMax) {
        return vehiculeDAO.findAll().stream()
                .filter(v -> "DISPO".equals(v.getStatut()))
                .filter(v -> v.getPrixVente() <= budgetMax)
                .sorted(Comparator.comparingDouble(Vehicule::getPrixVente))
                .collect(Collectors.toList());
    }
    
    
    public Map<String, List<Vehicule>> grouperVehiculesParMarque() {
        return vehiculeDAO.findAll().stream()
                .filter(v -> "DISPO".equals(v.getStatut()))
                .collect(Collectors.groupingBy(Vehicule::getMarque));
    }
    
    
    
    public List<Employe> listerEmployes() {
        return employeDAO.findAll(); 
    }
    public List<Vente> listerVentes() {
        return VenteDAO.findAll();
    }
    
    
}