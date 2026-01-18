package JavaFXProject;

import Service.GarageService;
import Metier.Client;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox; // Import important
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FormulaireClientController {

	@FXML
	private TextField txtNom;
	@FXML
	private TextField txtPrenom;
	@FXML
	private TextField txtTel;
	@FXML
	private TextField txtEmail;

	@FXML
	private ComboBox<String> comboStatut;

	@FXML
	private Button btnValider;
	@FXML
	private Button btnAnnuler;

	private GarageService service = new GarageService();

	@FXML
	public void initialize() {

		comboStatut.setItems(FXCollections.observableArrayList("STANDARD", "PLATINUM", "VIP"));

		comboStatut.getSelectionModel().select("STANDARD");

		btnValider.setOnAction(e -> ajouterClient());
		btnAnnuler.setOnAction(e -> fermerFenetre());
	}

	private void ajouterClient() {
		try {
			System.out.println("--- Ajout Client en cours ---");

			String nom = txtNom.getText();
			String prenom = txtPrenom.getText();
			String tel = txtTel.getText();
			String email = txtEmail.getText();

			String statutChoisi = comboStatut.getValue();

			if (nom.isEmpty() || prenom.isEmpty()) {
				System.err.println("Erreur : Le Nom et le Prénom sont obligatoires !");
				return;
			}

			Client nouveauClient = new Client(nom, prenom, email, tel);

			if (statutChoisi != null) {
				nouveauClient.setVipLevel(statutChoisi);
			}

			System.out
					.println("Client créé : " + nouveauClient.getNom() + " - Statut : " + nouveauClient.getVipLevel());

			service.ajouterClient(nouveauClient);

			System.out.println("Succès ! Fermeture de la fenêtre.");
			fermerFenetre();

		} catch (Exception ex) {
			System.err.println("Erreur lors de l'ajout du client :");
			ex.printStackTrace();
		}
	}

	private void fermerFenetre() {
		Stage stage = (Stage) btnAnnuler.getScene().getWindow();
		stage.close();
	}
}