package JavaFXProject;

import Service.GarageService;
import Metier.Intervention;
import Metier.Vehicule;
import Metier.Employe;
import Metier.Mecanicien;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class FormulaireInterventionController {

	@FXML
	private ComboBox<Vehicule> comboVehicule;
	@FXML
	private ComboBox<Mecanicien> comboMecanicien;
	@FXML
	private TextArea txtDescription;
	@FXML
	private TextField txtPrix;

	@FXML
	private Button btnValider;
	@FXML
	private Button btnAnnuler;

	private GarageService service = new GarageService();

	@FXML
	public void initialize() {
		chargerListes();
		btnValider.setOnAction(e -> validerIntervention());
		btnAnnuler.setOnAction(e -> fermerFenetre());
	}

	private void chargerListes() {

		List<Vehicule> vehicules = service.listerToutLeGarage();
		comboVehicule.getItems().addAll(vehicules);

		List<Employe> employes = service.listerEmployes();

		for (Employe e : employes) {
			if (e instanceof Mecanicien) {
				comboMecanicien.getItems().add((Mecanicien) e);
			}
		}
	}

	private void validerIntervention() {
		try {
			Vehicule v = comboVehicule.getValue();
			Mecanicien m = comboMecanicien.getValue();
			String desc = txtDescription.getText();
			String prixStr = txtPrix.getText();

			if (v == null || m == null || desc.isEmpty() || prixStr.isEmpty()) {
				System.out.println("Tout remplir svp !");
				return;
			}

			double prix = Double.parseDouble(prixStr);

			Intervention i = new Intervention(v, m, desc, prix);

			service.creerIntervention(i);

			System.out.println("Intervention créée !");
			fermerFenetre();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void fermerFenetre() {
		Stage stage = (Stage) btnAnnuler.getScene().getWindow();
		stage.close();
	}
}