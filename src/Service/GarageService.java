package Service;

import java.util.List;
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
        // Simple relais vers le DAO
        employeDAO.create(e);
        System.out.println("RH : Nouvel employé recruté -> " + e.getNom());
    }
    
    public void inscrireClient(Client c) {
        // On appelle le DAO pour l'insertion SQL
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
        
        // Règle métier : On vérifie les acteurs
        if (v == null) throw new GarageException("Véhicule introuvable");
        if (!(e instanceof Mecanicien)) throw new GarageException("Cet employé n'est pas un mécanicien !");

        // Création de l'objet Java
        Intervention i = new Intervention(v, (Mecanicien) e, description, 500.0);
        
        // APPEL DU DAO (Le lien se fait ici)
        interventionDAO.create(i); 
        
        System.out.println("Succès : Intervention enregistrée en BDD avec l'ID " + i.getId());
        return i;
    }
    
    public void terminerIntervention(int idIntervention) {
        // 1. On récupère l'intervention via le DAO (Maintenant ça marche !)
        Intervention i = interventionDAO.findById(idIntervention);
        
        if (i != null && "EN_COURS".equals(i.getStatut())) {
            
            // 2. Mise à jour des infos
            i.setStatut("TERMINE");
            i.setDateFin(new java.sql.Date(System.currentTimeMillis())); // Date du jour
            
            // 3. Libération du véhicule
            Vehicule v = i.getVehicule();
            v.setStatut("DISPO"); // La voiture sort de l'atelier
            
            // 4. Sauvegarde en BDD
            interventionDAO.update(i);
            vehiculeDAO.update(v);
            
            System.out.println("✅ Intervention terminée : " + v.getMarque() + " est disponible.");
        } else {
            System.err.println("❌ Erreur : Intervention introuvable ou déjà terminée.");
        }
    }
    
    
    
    
    public boolean realiserVente(int idVehicule, int idClient, int idVendeur) throws Exception {
        // 1. On récupère les acteurs
        Vehicule v = vehiculeDAO.findById(idVehicule);
        Client c = clientDAO.findById(idClient);
        Employe e = employeDAO.findById(idVendeur);
        
        // 2. Vérifications de sécurité (Est-ce que tout existe ?)
        if (v == null) throw new Exception("Véhicule introuvable (ID " + idVehicule + ")");
        if (c == null) throw new Exception("Client introuvable (ID " + idClient + ")");
        if (e == null) throw new Exception("Vendeur introuvable (ID " + idVendeur + ")");
        
        // 3. Règles Métier
        if (!"DISPO".equals(v.getStatut())) {
            throw new Exception("Impossible de vendre : Le véhicule n'est pas DISPO (Statut actuel : " + v.getStatut() + ")");
        }
        
        if (!(e instanceof Vendeur)) {
            throw new Exception("Erreur : L'employé " + e.getNom() + " n'est pas un vendeur !");
        }

        // 4. Tout est bon, on acte la vente
        // On crée l'objet Vente (avec la date d'aujourd'hui gérée dans le constructeur ou ici)
        Vente nouvelleVente = new Vente(v, c, (Vendeur) e);
        
        VenteDAO.create(nouvelleVente);
        
        // Mise à jour de la table VEHICULE (Elle passe en VENDU)
        v.setStatut("VENDU");
        // v.setProprietaire(c); // Décommente si tu as ajouté ce champ dans Vehicule
        vehiculeDAO.update(v);
        
        System.out.println("$$$ TRANSACTION REUSSIE : " + v.getMarque() + " vendue à " + c.getNom());
        return true;
    }
    
    
    
    
    
    
    
    
    public double calculerPrixMoyenVentes() {
        List<Vehicule> tout = vehiculeDAO.findAll();
        
        return tout.stream()
                .filter(v -> "VENDU".equals(v.getStatut())) // On garde que les vendues
                .mapToDouble(Vehicule::getPrixVente)         // On prend juste le prix
                .average()                                   // On fait la moyenne
                .orElse(0.0);                                // 0 si rien vendu
    }

    /**
     * Compte combien de véhicules de chaque type on a en stock.
     */
    public void afficherRepartitionStock() {
        List<Vehicule> stock = vehiculeDAO.findAll();
        
        long nbElec = stock.stream()
                .filter(v -> v instanceof Metier.VehiculeElectrique && "DISPO".equals(v.getStatut()))
                .count();
                
        long nbTherm = stock.stream()
                .filter(v -> v instanceof Metier.VoitureThermique && "DISPO".equals(v.getStatut()))
                .count();
                
        System.out.println("📊 STATS STOCK :");
        System.out.println("- Électriques : " + nbElec);
        System.out.println("- Thermiques  : " + nbTherm);
    }
    
    
    
}
    
    
    
