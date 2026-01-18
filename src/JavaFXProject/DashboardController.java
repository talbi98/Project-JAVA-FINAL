package JavaFXProject;

import Service.GarageService;
import Metier.Employe;
import Metier.Mecanicien;
import Metier.Vehicule;
import Metier.Vendeur;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class DashboardController {

	private GarageService service = new GarageService();

	@FXML
	private Label lblPrixMoyen;
	@FXML
	private Label lblTotalStock;
	@FXML
	private Label lblAtelier;

	@FXML
	private TableView<Vehicule> tableTop3;
	@FXML
	private TableColumn<Vehicule, String> colMarque;
	@FXML
	private TableColumn<Vehicule, String> colModele;
	@FXML
	private TableColumn<Vehicule, Double> colPrix;
	@FXML
	private TableColumn<Vehicule, String> colStatut;

	@FXML
	private TableView<Employe> tableEmployes;
	@FXML
	private TableColumn<Employe, String> colEmpNom;
	@FXML
	private TableColumn<Employe, String> colEmpPrenom;
	@FXML
	private TableColumn<Employe, String> colEmpPoste;

	@FXML
	private Label lblUserInitial;
	@FXML
	private Label lblUserName;
	@FXML
	private Label lblUserRole;
	@FXML
	private Button btnFactures;

	@FXML
	public void initialize() {
		try {
			chargerIndicateurs();
			chargerTableauTop3();
			chargerTableauEmployes();

			if (!Session.isAdmin()) {
				if (btnFactures != null) {
					btnFactures.setVisible(false);
					btnFactures.setManaged(false);
				}
			}
			configurerProfilUtilisateur();

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Erreur chargement dashboard : " + e.getMessage());
		}
	}

	@FXML
	private void handleBtnDashboard(ActionEvent event) {
		switchScene(event, "Dashboard.fxml");
	}

	@FXML
	private void handleBtnStock(ActionEvent event) {
		switchScene(event, "Stock.fxml");
	}

	@FXML
	private void handleBtnCommerce(ActionEvent event) {
		switchScene(event, "Commerce.fxml");
	}

	@FXML
	private void handleBtnAtelier(ActionEvent event) {
		switchScene(event, "Atelier.fxml");
	}

	@FXML
	private void handleBtnFactures(ActionEvent event) {
		switchScene(event, "Facture.fxml");
	}

	@FXML
	private void handleLogout(ActionEvent event) {
		Session.logout();
		try {
			Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
			Scene scene = new Scene(root);
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(scene);
			stage.centerOnScreen();
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void switchScene(ActionEvent event, String fxmlFile) {
		try {

			Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
			Scene scene = ((Node) event.getSource()).getScene();
			scene.setRoot(root);
		} catch (IOException e) {
			System.err.println("Erreur critique lors du chargement de " + fxmlFile);
			e.printStackTrace();
		}
	}

	private void chargerIndicateurs() {
		double caTotal = service.calculerPrixMoyenVentes();
		lblPrixMoyen.setText(String.format("%,.0f €", caTotal));

		List<Vehicule> tout = service.listerToutLeGarage();
		long nbDispo = tout.stream().filter(v -> "DISPO".equals(v.getStatut())).count();
		lblTotalStock.setText(String.valueOf(nbDispo));

		long nbAtelier = tout.stream().filter(v -> "ATELIER".equals(v.getStatut()) || "EN_COURS".equals(v.getStatut()))
				.count();
		lblAtelier.setText(String.valueOf(nbAtelier));
	}

	private void chargerTableauTop3() {
		List<Vehicule> top3 = service.getTop3VoituresLuxe();
		ObservableList<Vehicule> data = FXCollections.observableArrayList(top3);

		colMarque.setCellValueFactory(new PropertyValueFactory<>("marque"));
		colModele.setCellValueFactory(new PropertyValueFactory<>("modele"));
		colPrix.setCellValueFactory(new PropertyValueFactory<>("prixVente"));

		colPrix.setCellFactory(col -> new TableCell<Vehicule, Double>() {
			@Override
			protected void updateItem(Double item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null)
					setText(null);
				else {
					setText(String.format("%,.0f €", item));
					setStyle("-fx-text-fill: white; -fx-alignment: CENTER-RIGHT;");
				}
			}
		});

		colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

		colStatut.setCellFactory(column -> new TableCell<Vehicule, String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setGraphic(null);
					setText(null);
				} else {
					Label badge = new Label(item);
					badge.setStyle(
							"-fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 5; -fx-text-fill: white;");

					if ("VENDU".equals(item))
						badge.setStyle(badge.getStyle() + "-fx-background-color: #f44336;"); // Rouge
					else if ("DISPO".equals(item))
						badge.setStyle(badge.getStyle() + "-fx-background-color: #4caf50;"); // Vert
					else
						badge.setStyle(badge.getStyle() + "-fx-background-color: #ff9800;"); // Orange

					setGraphic(badge);
					setText(null);
				}
			}
		});

		tableTop3.setItems(data);
	}

	private void chargerTableauEmployes() {
		List<Employe> lesEmployes = service.listerEmployes();
		ObservableList<Employe> data = FXCollections.observableArrayList(lesEmployes);

		colEmpNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
		colEmpPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));

		colEmpPoste.setCellValueFactory(cellData -> {
			Employe e = cellData.getValue();
			if (e instanceof Vendeur)
				return new SimpleStringProperty("Vendeur");
			else if (e instanceof Mecanicien)
				return new SimpleStringProperty("Mécanicien");
			else
				return new SimpleStringProperty("Autre");
		});

		tableEmployes.setItems(data);
	}

	private void configurerProfilUtilisateur() {
		if (lblUserName != null)
			lblUserName.setText(Session.getNom());
		if (Session.isAdmin()) {
			if (lblUserRole != null)
				lblUserRole.setText("Directeur / Admin");
			if (lblUserInitial != null) {
				lblUserInitial.setText("A");
				lblUserInitial.setStyle(
						"-fx-background-color: #ff9800; -fx-background-radius: 20; -fx-text-fill: black; -fx-padding: 8 14; -fx-font-weight: bold;");
			}
		} else {
			if (lblUserRole != null)
				lblUserRole.setText("Employé");
			if (lblUserInitial != null) {
				lblUserInitial.setText("U");
				lblUserInitial.setStyle(
						"-fx-background-color: #2196f3; -fx-background-radius: 20; -fx-text-fill: white; -fx-padding: 8 14; -fx-font-weight: bold;");
			}
		}
	}
}