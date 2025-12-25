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
import javafx.stage.Modality; // Important pour la fenêtre bloquante
import javafx.stage.Stage;    // Important pour la fenêtre

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class CommerceController {

    private GarageService service = new GarageService();

    // --- Table Ventes ---
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
    @FXML private TextField txtChercherClient; // J'ai ajouté le champ de recherche

    @FXML
    public void initialize() {
        try {
            setupColonnes();
            chargerDonnees();
            System.out.println("Page Commerce chargée.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupColonnes() {
        // --- VENTES ---
        colVenteDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getdateVente()));
        colVenteRef.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReference()));
        
        colVenteVehicule.setCellValueFactory(cellData -> {
            if (cellData.getValue().getVehicule() != null) {
                return new SimpleStringProperty(cellData.getValue().getVehicule().getMarque() + " " + cellData.getValue().getVehicule().getModele());
            }
            return new SimpleStringProperty("N/A");
        });

        colVenteClient.setCellValueFactory(cellData -> {
            if (cellData.getValue().getClient() != null) {
                return new SimpleStringProperty(cellData.getValue().getClient().getNom() + " " + cellData.getValue().getClient().getPrenom());
            }
            return new SimpleStringProperty("Inconnu");
        });

        colVenteMontant.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getMontantFinal()));
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

        // --- CLIENTS ---
        colClientNom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom() + " " + cellData.getValue().getPrenom()));
        colClientEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        colClientVip.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVipLevel()));
    }

    private void chargerDonnees() {
        // Charge les deux tableaux
        List<Vente> ventes = service.listerVentes();
        tableVentes.setItems(FXCollections.observableArrayList(ventes));

        List<Client> clients = service.listerClients();
        tableClients.setItems(FXCollections.observableArrayList(clients));
    }

    // --- ACTIONS BOUTONS ---

    @FXML
    private void handleNouvelleVente(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FormulaireVente.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("Nouvelle Vente");
            popupStage.setScene(new Scene(root));
            popupStage.initModality(Modality.APPLICATION_MODAL);

            popupStage.showAndWait();

            // Une fois fini, on recharge les données pour voir la nouvelle vente
            chargerDonnees();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleNouveauClient(ActionEvent event) {
        // C'EST ICI QUE J'AI CORRIGÉ LE CODE :
        try {
            // 1. Charger la vue du formulaire
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FormulaireClient.fxml"));
            Parent root = loader.load();

            // 2. Créer la fenêtre
            Stage popupStage = new Stage();
            popupStage.setTitle("Nouveau Client");
            popupStage.setScene(new Scene(root));
            
            // 3. Bloquer la fenêtre derrière
            popupStage.initModality(Modality.APPLICATION_MODAL);
            
            // 4. Afficher et attendre
            popupStage.showAndWait();

            // 5. Rafraîchir le tableau après fermeture
            chargerDonnees();

        } catch (IOException e) {
            System.err.println("Impossible d'ouvrir FormulaireClient.fxml");
            e.printStackTrace();
        }
    }

    // --- NAVIGATION ---

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
    
    @FXML
    private void handleBtnAtelier(ActionEvent event) {
        switchScene(event, "Atelier.fxml");
    }
}