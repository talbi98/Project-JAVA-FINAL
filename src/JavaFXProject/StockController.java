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
    
    // Données pour le tableau
    private ObservableList<Vehicule> masterData = FXCollections.observableArrayList();
    private FilteredList<Vehicule> filteredData;

    // Filtres (Plus de barre de recherche)
    @FXML private ComboBox<String> comboMarque;
    @FXML private ComboBox<String> comboStatut;

    // Tableau
    @FXML private TableView<Vehicule> tableStock;
    @FXML private TableColumn<Vehicule, Integer> colId;
    @FXML private TableColumn<Vehicule, String> colMarque, colModele, colStatut;
    @FXML private TableColumn<Vehicule, Double> colPrix;
    
    // Boutons et Profil
    @FXML private Button btnAjouterVehicule, btnFactures;
    @FXML private Label lblUserInitial, lblUserName, lblUserRole;

    @FXML
    public void initialize() {
        try {
            chargerDonneesDepuisBDD();
            configurerColonnes();
            configurerFiltres(); // C'est ici qu'on active les ComboBox
            
            // Sécurité Admin/User
            if (!Session.isAdmin()) {
                if (btnAjouterVehicule != null) btnAjouterVehicule.setVisible(false);
                if (btnFactures != null) { btnFactures.setVisible(false); btnFactures.setManaged(false); }
            }
            configurerProfilUtilisateur();
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    private void chargerDonneesDepuisBDD() {
        // 1. On récupère tout
        masterData.setAll(service.listerToutLeGarage());
        
        // 2. On prépare le filtre (au début, il laisse tout passer "p -> true")
        filteredData = new FilteredList<>(masterData, p -> true);
        
        // 3. On lie le tableau aux données filtrées
        tableStock.setItems(filteredData);
    }

    private void configurerColonnes() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMarque.setCellValueFactory(new PropertyValueFactory<>("marque"));
        colModele.setCellValueFactory(new PropertyValueFactory<>("modele"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixVente"));
        
        // Design du Prix
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
        
        // Design des Badges Statut
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
        // 1. Remplir la ComboBox des Marques avec les marques existantes en BDD
        List<String> marques = masterData.stream()
                                         .map(Vehicule::getMarque)
                                         .distinct()
                                         .sorted()
                                         .collect(Collectors.toList());
        marques.add(0, "Toutes"); // On ajoute l'option pour tout voir
        comboMarque.setItems(FXCollections.observableArrayList(marques));
        comboMarque.getSelectionModel().selectFirst(); // Sélectionne "Toutes" par défaut

        // 2. Remplir la ComboBox des Statuts
        comboStatut.setItems(FXCollections.observableArrayList("Tous", "DISPO", "VENDU", "ATELIER"));
        comboStatut.getSelectionModel().selectFirst(); // Sélectionne "Tous" par défaut
        
        // 3. Ajouter les "Ecouteurs" : dès qu'on change une valeur, on filtre
        comboMarque.valueProperty().addListener((obs, oldVal, newVal) -> appliquerFiltre());
        comboStatut.valueProperty().addListener((obs, oldVal, newVal) -> appliquerFiltre());
    }
    
    private void appliquerFiltre() {
        String marqueSelectionnee = comboMarque.getValue();
        String statutSelectionne = comboStatut.getValue();

        filteredData.setPredicate(vehicule -> {
            // Logique pour la Marque
            boolean matchMarque = true;
            if (marqueSelectionnee != null && !"Toutes".equals(marqueSelectionnee)) {
                matchMarque = vehicule.getMarque().equalsIgnoreCase(marqueSelectionnee);
            }

            // Logique pour le Statut
            boolean matchStatut = true;
            if (statutSelectionne != null && !"Tous".equals(statutSelectionne)) {
                matchStatut = vehicule.getStatut().equalsIgnoreCase(statutSelectionne);
            }

            // On garde le véhicule seulement si Marque ET Statut correspondent
            return matchMarque && matchStatut; 
        });
    }

    @FXML private void handleAjouterVehicule() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("FormulaireVehicule.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            // Recharger les données et mettre à jour les filtres (nouvelle marque peut-être ?)
            chargerDonneesDepuisBDD();
            configurerFiltres(); 
        } catch (IOException e) { e.printStackTrace(); }
    }

    // --- NAVIGATION ---
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