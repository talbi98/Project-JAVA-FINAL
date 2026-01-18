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

	@FXML
	private ComboBox<String> comboType;
	@FXML
	private TextField txtMarque;
	@FXML
	private TextField txtModele;
	@FXML
	private TextField txtImmat;
	@FXML
	private TextField txtPrix;
	@FXML
	private Button btnValider;
	@FXML
	private Button btnAnnuler;

	private GarageService service = new GarageService();

	@FXML
	public void initialize() {

		comboType.getItems().addAll("Thermique", "Electrique");
		comboType.getSelectionModel().selectFirst();

		btnValider.setOnAction(e -> ajouterVehicule());

		btnAnnuler.setOnAction(e -> fermerFenetre());
	}

	private void ajouterVehicule() {
		try {
			System.out.println("--- Ajout en cours ---");

			String type = comboType.getValue();
			String marque = txtMarque.getText();
			String modele = txtModele.getText();
			String immat = txtImmat.getText();

			if (txtPrix.getText().isEmpty()) {
				System.err.println("Le prix est vide !");
				return;
			}
			double prix = Double.parseDouble(txtPrix.getText());

			Vehicule nouveauVehicule;

			if ("Electrique".equals(type)) {

				nouveauVehicule = new VehiculeElectrique(0, marque, modele, prix, 100, 500);
			} else {

				nouveauVehicule = new VoitureThermique(0, marque, modele, prix, 150, 2000);
			}

			nouveauVehicule.setImmatriculation(immat);

			System.out.println("Statut auto : " + nouveauVehicule.getStatut());

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

		Stage stage = (Stage) btnAnnuler.getScene().getWindow();
		stage.close();
	}
}