package JavaFXProject;

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
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class CommerceController {

    private GarageService service = new GarageService();

    
    @FXML private TableView<Vente> tableVentes;
    @FXML private TableColumn<Vente, String> colVenteRef;
    @FXML private TableColumn<Vente, java.sql.Date> colVenteDate;
    @FXML private TableColumn<Vente, String> colVenteVehicule;
    @FXML private TableColumn<Vente, String> colVenteClient;
    @FXML private TableColumn<Vente, Double> colVenteMontant;

    // --- Table Clients ---
    @FXML private TableView<Client> tableClients;
    @FXML private TableColumn<Client, String> colClientNom;
    @FXML private TableColumn<Client, String> colClientEmail;
    @FXML private TableColumn<Client, String> colClientVip;

    @FXML
    public void initialize() {
        try {
            setupColonnes();
            chargerDonnees();
            System.out.println("Commerce initialisé avec la classe Vente corrigée.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupColonnes() {
      

      
        colVenteDate.setCellValueFactory(cellData -> {
            return new SimpleObjectProperty<>(cellData.getValue().getdateVente());
        });

      
        colVenteRef.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getReference()));

       
        colVenteVehicule.setCellValueFactory(cellData -> {
            if (cellData.getValue().getVehicule() != null) {
                return new SimpleStringProperty(
                    cellData.getValue().getVehicule().getMarque() + " " + 
                    cellData.getValue().getVehicule().getModele()
                );
            }
            return new SimpleStringProperty("N/A");
        });

       
        colVenteClient.setCellValueFactory(cellData -> {
            if (cellData.getValue().getClient() != null) {
                return new SimpleStringProperty(
                    cellData.getValue().getClient().getNom() + " " + 
                    cellData.getValue().getClient().getPrenom()
                );
            }
            return new SimpleStringProperty("Inconnu");
        });

       
        colVenteMontant.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getMontantFinal()));

       
        colVenteMontant.setCellFactory(column -> new TableCell<Vente, Double>() {
            
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f €", item));
                    setStyle("-fx-font-weight: bold;");
                }
            }
        });

       
        colClientNom.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNom() + " " + cellData.getValue().getPrenom()));
        colClientEmail.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getEmail()));
        colClientVip.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getVipLevel()));
    }

    private void chargerDonnees() {
        List<Vente> ventes = service.listerVentes();
        tableVentes.setItems(FXCollections.observableArrayList(ventes));

        List<Client> clients = service.listerClients();
        tableClients.setItems(FXCollections.observableArrayList(clients));
    }

   

    @FXML
    private void handleNouvelleVente(ActionEvent event) {
        System.out.println("Action : Ouverture du formulaire de nouvelle vente");
       
    }

    @FXML
    private void handleNouveauClient(ActionEvent event) {
        System.out.println("Action : Ouverture du formulaire de nouveau client");
       
    }

   

    @FXML 
    private void handleBtnDashboard(ActionEvent event) { 
        switchScene(event, "Dashboard.fxml"); 
    }
    
    @FXML 
    private void handleBtnStock(ActionEvent event) { 
        switchScene(event, "Stock.fxml"); 
    }

    private void switchScene(ActionEvent event, String fxmlFile) {
        try {
            URL resource = getClass().getResource(fxmlFile);
            if (resource == null) {
                System.err.println("Fichier introuvable : " + fxmlFile);
                return;
            }
            Parent root = FXMLLoader.load(resource);
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);
        } catch (IOException e) { 
            e.printStackTrace(); 
        }
    }
}