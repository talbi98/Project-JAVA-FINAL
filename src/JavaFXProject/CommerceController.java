
package JavaFXProject ;

import java.util.List;

import DAO.ClientDAO;
import Metier.Client;



import Service.GarageService;
import Metier.Client;
import Metier.Vente;
import javafx.beans.property.SimpleObjectProperty;

	


import Service.GarageService;
import Metier.Client;
import Metier.Vente;
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

public class CommerceController {

    private GarageService service = new GarageService();

    @FXML private TableView<Vente> tableVentes;
    @FXML private TableColumn<Vente, String> colVenteRef, colVenteVehicule, colVenteClient, colVenteDate;
    @FXML private TableColumn<Vente, Double> colVenteMontant;
    @FXML private TableView<Client> tableClients;
    @FXML private TableColumn<Client, String> colClientNom, colClientEmail, colClientVip;
    
    @FXML private Button btnNouvelleVente, btnNouveauClient, btnFactures;
    @FXML private Label lblUserInitial, lblUserName, lblUserRole;

    @FXML
    public void initialize() {
        chargerDonnees();
        
        if (!Session.isAdmin()) {
            if (btnNouvelleVente != null) btnNouvelleVente.setVisible(false);
            if (btnNouveauClient != null) btnNouveauClient.setVisible(false);
            if (btnFactures != null) { btnFactures.setVisible(false); btnFactures.setManaged(false); }
        }
        configurerProfilUtilisateur();
    }

    private void chargerDonnees() {
        // VENTES
        tableVentes.setItems(FXCollections.observableArrayList(service.listerVentes()));
        colVenteRef.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReference()));
        colVenteDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getdateVente().toString()));
        colVenteVehicule.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getVehicule() != null ? c.getValue().getVehicule().getModele() : "?"));
        colVenteClient.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getClient() != null ? c.getValue().getClient().getNom() : "?"));
        colVenteMontant.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getMontantFinal()));
        
        colVenteMontant.setCellFactory(col -> new TableCell<Vente, Double>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else { setText(String.format("%,.0f €", item)); setStyle("-fx-text-fill: #4caf50; -fx-font-weight: bold;"); }
            }
        });

        // CLIENTS
        tableClients.setItems(FXCollections.observableArrayList(service.listerClients()));
        colClientNom.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNom() + " " + c.getValue().getPrenom()));
        colClientEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        colClientVip.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getVipLevel()));
    }

    @FXML private void handleNouvelleVente() { openPopup("FormulaireVente.fxml"); }
    @FXML private void handleNouveauClient() { openPopup("FormulaireClient.fxml"); }

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
    @FXML private void handleBtnAtelier(ActionEvent event) { switchScene(event, "Atelier.fxml"); }
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
