package JavaFXProject;

import Service.GarageService;
import Metier.Vehicule;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class StockController {
   
    private GarageService service = new GarageService();
    private ObservableList<Vehicule> masterData = FXCollections.observableArrayList();
    private FilteredList<Vehicule> filteredData;

    @FXML private TextField txtRecherche;
    @FXML private ComboBox<String> comboMarque, comboStatut;
    @FXML private TableView<Vehicule> tableStock;
    @FXML private TableColumn<Vehicule, Integer> colId;
    @FXML private TableColumn<Vehicule, String> colMarque, colModele, colStatut;
    @FXML private TableColumn<Vehicule, Double> colPrix;
    
    @FXML private Button btnAjouterVehicule, btnFactures;
    @FXML private Label lblUserInitial, lblUserName, lblUserRole;

    @FXML
    public void initialize() {
        try {
            chargerDonneesDepuisBDD();
            configurerColonnes();
            configurerFiltres();
            
            if (!Session.isAdmin()) {
                if (btnAjouterVehicule != null) btnAjouterVehicule.setVisible(false);
                if (btnFactures != null) { btnFactures.setVisible(false); btnFactures.setManaged(false); }
            }
            configurerProfilUtilisateur();
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    private void chargerDonneesDepuisBDD() {
        masterData.setAll(service.listerToutLeGarage());
        filteredData = new FilteredList<>(masterData, p -> true);
        tableStock.setItems(filteredData);
    }

    private void configurerColonnes() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMarque.setCellValueFactory(new PropertyValueFactory<>("marque"));
        colModele.setCellValueFactory(new PropertyValueFactory<>("modele"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixVente"));
        
        // DESIGN PRIX
        colPrix.setCellFactory(column -> new TableCell<Vehicule, Double>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else { 
                    setText(String.format("%,.0f €", item)); 
                    setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-alignment: CENTER-RIGHT;"); 
                }
            }
        });

        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        
        // DESIGN BADGES COULEURS
        colStatut.setCellFactory(column -> new TableCell<Vehicule, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); setText(null); }
                else {
                    Label badge = new Label(item);
                    badge.setStyle("-fx-font-weight: bold; -fx-padding: 3 10; -fx-background-radius: 5; -fx-text-fill: white;");
                    if ("VENDU".equals(item)) badge.setStyle(badge.getStyle() + "-fx-background-color: #f44336;");
                    else if ("DISPO".equals(item)) badge.setStyle(badge.getStyle() + "-fx-background-color: #4caf50;");
                    else badge.setStyle(badge.getStyle() + "-fx-background-color: #ff9800;");
                    setGraphic(badge); setText(null);
                }
            }
        });
    }
    
    private void configurerFiltres() {
        List<String> marques = masterData.stream().map(Vehicule::getMarque).distinct().sorted().collect(Collectors.toList());
        marques.add(0, "Toutes");
        comboMarque.setItems(FXCollections.observableArrayList(marques));
        comboStatut.setItems(FXCollections.observableArrayList("Tous", "DISPO", "VENDU", "ATELIER"));
        
        txtRecherche.textProperty().addListener((o, old, n) -> appliquerFiltre());
        comboMarque.valueProperty().addListener((o, old, n) -> appliquerFiltre());
        comboStatut.valueProperty().addListener((o, old, n) -> appliquerFiltre());
    }
    
    private void appliquerFiltre() {
        filteredData.setPredicate(v -> {
            String search = (txtRecherche.getText() != null) ? txtRecherche.getText().toLowerCase() : "";
            boolean matchText = search.isEmpty() || v.getModele().toLowerCase().contains(search) || v.getMarque().toLowerCase().contains(search);
            return matchText; 
        });
    }

    @FXML private void handleAjouterVehicule() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("FormulaireVehicule.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            chargerDonneesDepuisBDD();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // --- NAVIGATION CORRIGÉE ---
    @FXML private void handleBtnDashboard(ActionEvent event) { switchScene(event, "Dashboard.fxml"); }
    @FXML private void handleBtnCommerce(ActionEvent event) { switchScene(event, "Commerce.fxml"); }
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