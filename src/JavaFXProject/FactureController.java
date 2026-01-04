package JavaFXProject;

import Service.GarageService;
import Metier.IFacturable;
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
import javafx.stage.Stage;
import java.io.IOException;

public class FactureController {

    private GarageService service = new GarageService();
    @FXML private TableView<IFacturable> tableFactures;
    @FXML private TableColumn<IFacturable, String> colRef, colDescription, colClient, colDate;
    @FXML private TableColumn<IFacturable, Double> colMontant;
    @FXML private Label lblUserInitial, lblUserName, lblUserRole;

    @FXML
    public void initialize() {
        tableFactures.setItems(FXCollections.observableArrayList(service.listerHistoriqueFactures()));
        colRef.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReference()));
        colDescription.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescriptionFacture()));
        colClient.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getClientFacture() != null ? c.getValue().getClientFacture().getNom() : "Interne"));
        colDate.setCellValueFactory(c -> new SimpleStringProperty("2025-01-04")); // Date fictive ou à implémenter
        colMontant.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getMontantTotal()));
        
        colMontant.setCellFactory(col -> new TableCell<IFacturable, Double>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else { setText(String.format("%,.0f €", item)); setStyle("-fx-text-fill: #4caf50; -fx-font-weight: bold; -fx-alignment: CENTER-RIGHT;"); }
            }
        });
        
        configurerProfilUtilisateur();
    }

    @FXML private void handleImprimer() {
        IFacturable item = tableFactures.getSelectionModel().getSelectedItem();
        if(item != null) System.out.println(GarageService.editerFacture(item));
    }

    // --- NAVIGATION CORRIGÉE ---
    @FXML private void handleBtnDashboard(ActionEvent event) { switchScene(event, "Dashboard.fxml"); }
    @FXML private void handleBtnStock(ActionEvent event) { switchScene(event, "Stock.fxml"); }
    @FXML private void handleBtnCommerce(ActionEvent event) { switchScene(event, "Commerce.fxml"); }
    @FXML private void handleBtnAtelier(ActionEvent event) { switchScene(event, "Atelier.fxml"); }
    
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