package JavaFXProject;

import Service.GarageService;
import Metier.Vehicule;
import Metier.VoitureThermique;
import Metier.VehiculeElectrique;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import Metier.VehiculeElectrique;

public class FormulaireController {

    @FXML private ComboBox<String> comboType;
    @FXML private TextField txtMarque;
    @FXML private TextField txtModele;
    @FXML private TextField txtImmat;
    @FXML private TextField txtPrix;
    @FXML private Button btnValider;
    @FXML private Button btnAnnuler;

    private GarageService service = new GarageService();

    @FXML
    public void initialize() {
        // Remplir la liste déroulante
        comboType.getItems().addAll("Thermique", "Electrique");
        comboType.getSelectionModel().selectFirst();

        // Action du bouton Valider
        btnValider.setOnAction(e -> ajouterVehicule());

        // Action du bouton Annuler (Ferme la fenêtre)
        btnAnnuler.setOnAction(e -> fermerFenetre());
    }

    private void ajouterVehicule() {
        try {
            System.out.println("--- Ajout en cours ---");
            
            // 1. Récupération des champs du formulaire
            String type = comboType.getValue();
            String marque = txtMarque.getText();
            String modele = txtModele.getText();
            String immat = txtImmat.getText(); // On récupère l'immatriculation ici
            
            if(txtPrix.getText().isEmpty()) {
                System.err.println("Le prix est vide !");
                return;
            }
            double prix = Double.parseDouble(txtPrix.getText());

            Vehicule nouveauVehicule;

            // 2. Création de l'objet selon tes constructeurs EXACTS
            if ("Electrique".equals(type)) {
                // Constructeur : (id, marque, modele, prix, batterieKwh, autonomie)
                // J'ai mis 100 kWh et 500 km par défaut car ton formulaire n'a pas encore ces champs
                nouveauVehicule = new VehiculeElectrique(0, marque, modele, prix, 100, 500); 
            } else {
                // Constructeur : (id, marque, modele, prix, emissionCo2, cylindree)
                // J'ai mis 150g CO2 et 2000cc par défaut
                nouveauVehicule = new VoitureThermique(0, marque, modele, prix, 150, 2000);
            }

            // 3. IMPORTANT : On ajoute l'immatriculation via le Setter
            // Car elle n'est pas dans le constructeur
            nouveauVehicule.setImmatriculation(immat);
            
            // Le statut est déjà mis à "DISPO" automatiquement dans ton abstract class Vehicule
            // donc pas besoin de le forcer, mais on peut vérifier :
            System.out.println("Statut auto : " + nouveauVehicule.getStatut()); 

            // 4. Envoi au Service -> DAO -> Base de données
            service.ajouterVehicule(nouveauVehicule);
            
            System.out.println("Véhicule ajouté : " + marque + " " + modele);
            fermerFenetre();

        } catch (NumberFormatException ex) {
            System.err.println("Erreur : Le prix doit être un chiffre valide.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    

    private void fermerFenetre() {
        // Petite astuce pour fermer la fenêtre actuelle
        Stage stage = (Stage) btnAnnuler.getScene().getWindow();
        stage.close();
    }
}