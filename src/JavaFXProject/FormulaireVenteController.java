package JavaFXProject;

import Service.GarageService;
import Metier.Client;
import Metier.Employe; // Important
import Metier.Vehicule;
import Metier.Vendeur; // Important
import Metier.Vente;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class FormulaireVenteController {

    @FXML private ComboBox<Client> comboClient;
    @FXML private ComboBox<Vehicule> comboVehicule;
    @FXML private Label lblPrix;
    @FXML private Button btnValider;
    @FXML private Button btnAnnuler;

    private GarageService service = new GarageService();

    @FXML
    public void initialize() {
        chargerListes();

        // Met à jour le prix quand on sélectionne une voiture
        comboVehicule.setOnAction(e -> mettreAJourPrix());

        btnValider.setOnAction(e -> validerVente());
        btnAnnuler.setOnAction(e -> fermerFenetre());
    }

    private void chargerListes() {
        // 1. Charger les Clients
        try {
            List<Client> clients = service.listerClients();
            comboClient.getItems().addAll(clients);
        } catch (Exception e) {
            System.err.println("Erreur chargement clients : " + e.getMessage());
        }

        // 2. Charger les Voitures DISPO uniquement
        try {
            List<Vehicule> tousLesVehicules = service.listerToutLeGarage();
            
            List<Vehicule> vehiculesDispo = tousLesVehicules.stream()
                    .filter(v -> "DISPO".equals(v.getStatut()))
                    .collect(Collectors.toList());

            comboVehicule.getItems().addAll(vehiculesDispo);
        } catch (Exception e) {
            System.err.println("Erreur chargement véhicules : " + e.getMessage());
        }
    }

    private void mettreAJourPrix() {
        Vehicule v = comboVehicule.getValue();
        if (v != null) {
            lblPrix.setText(String.format("Montant : %,.0f €", v.getPrixVente()));
        }
    }

    private void validerVente() {
        try {
            Client client = comboClient.getValue();
            Vehicule vehicule = comboVehicule.getValue();

            if (client == null || vehicule == null) {
                System.out.println("Erreur : Sélectionnez un client et une voiture !");
                return;
            }

            // === L'ASTUCE POUR LE VENDEUR ===
            // Comme le constructeur Vente oblige à avoir un Vendeur,
            // on va chercher le premier vendeur disponible dans la base de données.
            Vendeur vendeur = trouverUnVendeurAuto();

            if (vendeur == null) {
                System.err.println("ERREUR CRITIQUE : Aucun vendeur trouvé dans la base de données !");
                System.err.println("Veuillez ajouter un employé de type 'Vendeur' avant de faire une vente.");
                return;
            }

            // === CRÉATION DE LA VENTE ===
            // On utilise ton 2ème constructeur qui gère la date automatiquement
            Vente nouvelleVente = new Vente(vehicule, client, vendeur);

            // Enregistrement
            service.enregistrerVente(nouvelleVente);

            System.out.println("Vente validée pour le véhicule : " + vehicule.getMarque());
            fermerFenetre();

        } catch (Exception ex) {
            System.err.println("Erreur lors de la validation de la vente :");
            ex.printStackTrace();
        }
    }
    
    // Méthode utilitaire pour trouver un vendeur automatiquement
    private Vendeur trouverUnVendeurAuto() {
        List<Employe> employes = service.listerEmployes();
        
        for (Employe e : employes) {
            if (e instanceof Vendeur) {
                return (Vendeur) e; // On retourne le premier trouvé
            }
        }
        return null; // Aie, pas de vendeur...
    }

    private void fermerFenetre() {
        Stage stage = (Stage) btnAnnuler.getScene().getWindow();
        stage.close();
    }
}