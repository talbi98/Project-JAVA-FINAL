package JavaFXProject;

import Service.GarageService;
import Metier.Intervention;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class AtelierController {

    private GarageService service = new GarageService();

    @FXML private TableView<Intervention> tableAtelier;
    @FXML private TableColumn<Intervention, String> colVehicule, colDesc, colStatut, colMeca;
    @FXML private TableColumn<Intervention, java.util.Date> colDateDeb, colDateFin;

    @FXML private Button btnNouvelleIntervention, btnTerminer, btnFactures;
    @FXML private Label lblUserInitial, lblUserName, lblUserRole;

    @FXML
    public void initialize() {
        chargerDonnees();
        
        if (!Session.isAdmin()) {
            if (btnFactures != null) { btnFactures.setVisible(false); btnFactures.setManaged(false); }
        }
        configurerProfilUtilisateur();
    }

    private void chargerDonnees() {
        tableAtelier.setItems(FXCollections.observableArrayList(service.listerInterventions()));
        
        colVehicule.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getVehicule().getMarque() + " " + c.getValue().getVehicule().getModele()));
        colDesc.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));
        colMeca.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMecanicien().getNom()));
        colDateDeb.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getDateDebut()));
        colDateFin.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getDateFin()));
        colStatut.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatut()));
        
        // DESIGN STATUT
        colStatut.setCellFactory(col -> new TableCell<Intervention, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else {
                    setText(item);
                    if("TERMINE".equals(item)) setStyle("-fx-text-fill: #4caf50; -fx-font-weight: bold;");
                    else setStyle("-fx-text-fill: #ff9800; -fx-font-weight: bold;");
                }
            }
        });
    }

    @FXML private void handleNouvelleIntervention() { openPopup("FormulaireIntervention.fxml"); }

    @FXML private void handleTerminer() {
        Intervention i = tableAtelier.getSelectionModel().getSelectedItem();
        if (i != null && !"TERMINE".equals(i.getStatut())) {
            service.terminerIntervention(i);
            chargerDonnees();
        }
    }

    private void openPopup(String fxml) {
        try {
            Stage stage = new Stage();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource(fxml))));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            chargerDonnees();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // --- NAVIGATION CORRIGÉE ---
    @FXML private void handleBtnDashboard(ActionEvent event) { switchScene(event, "Dashboard.fxml"); }
    @FXML private void handleBtnStock(ActionEvent event) { switchScene(event, "Stock.fxml"); }
    @FXML private void handleBtnCommerce(ActionEvent event) { switchScene(event, "Commerce.fxml"); }
    @FXML private void handleBtnFactures(ActionEvent event) { switchScene(event, "Facture.fxml"); }
    
    @FXML private void handleLogout(ActionEvent event) {
        Session.logout();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
            Scene scene = ((Node) event.getSource()).getScene();
            Stage stage = (Stage) scene.getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void switchScene(ActionEvent event, String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);
        } catch (IOException e) { e.printStackTrace(); }
    }
    
    private void configurerProfilUtilisateur() {
        if (lblUserName != null) lblUserName.setText(Session.getNom());
        if (Session.isAdmin()) {
            if (lblUserRole != null) lblUserRole.setText("Directeur");
            if (lblUserInitial != null) { lblUserInitial.setText("A"); lblUserInitial.setStyle("-fx-background-color: #ff9800; -fx-background-radius: 20; -fx-padding: 8 14; -fx-font-weight: bold;"); }
        } else {
            if (lblUserRole != null) lblUserRole.setText("Employé");
            if (lblUserInitial != null) { lblUserInitial.setText("U"); lblUserInitial.setStyle("-fx-background-color: #2196f3; -fx-background-radius: 20; -fx-padding: 8 14; -fx-font-weight: bold; -fx-text-fill: white;"); }
        }
    }
}