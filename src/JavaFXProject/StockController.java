package JavaFXProject;

import Service.GarageService;
import Metier.Vehicule;
import javafx.beans.property.SimpleStringProperty;
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
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;


public class StockController {

   
    private GarageService service = new GarageService();
    
   
    private ObservableList<Vehicule> masterData = FXCollections.observableArrayList();
    private FilteredList<Vehicule> filteredData;

   
    @FXML private TextField txtRecherche;
    @FXML private ComboBox<String> comboMarque;
    @FXML private ComboBox<String> comboStatut;

    @FXML private TableView<Vehicule> tableStock;
    @FXML private TableColumn<Vehicule, Integer> colId;
    @FXML private TableColumn<Vehicule, String> colMarque;
    @FXML private TableColumn<Vehicule, String> colModele;
    @FXML private TableColumn<Vehicule, Double> colPrix;
    @FXML private TableColumn<Vehicule, String> colStatut;

   
    @FXML
    public void initialize() {
        try {
            chargerDonneesDepuisBDD();
            configurerColonnes();
            configurerFiltres();
            System.out.println("LOG: Page Stock initialisée avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERREUR: Échec de l'initialisation du StockController.");
        }
    }
    
    
 

    
    private void chargerDonneesDepuisBDD() {
        List<Vehicule> liste = service.listerToutLeGarage();
        masterData.clear();
        masterData.addAll(liste);
        
       
        filteredData = new FilteredList<>(masterData, p -> true);
        tableStock.setItems(filteredData);
    }

  
    private void configurerColonnes() {
        
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMarque.setCellValueFactory(new PropertyValueFactory<>("marque"));
        colModele.setCellValueFactory(new PropertyValueFactory<>("modele"));
        
       
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixVente"));
        colPrix.setCellFactory(column -> new TableCell<Vehicule, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f €", item));
                    setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
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
                    Label badge = new Label(item.toUpperCase());
                    String styleBase = "-fx-font-weight: bold; -fx-padding: 3 10; -fx-background-radius: 5; -fx-font-size: 10px;";
                    
                    if ("VENDU".equals(item)) {
                        badge.setStyle(styleBase + "-fx-background-color: rgba(244, 67, 54, 0.2); -fx-text-fill: #ff6b6b;");
                    } else if ("DISPO".equals(item)) {
                        badge.setStyle(styleBase + "-fx-background-color: rgba(76, 175, 80, 0.2); -fx-text-fill: #69f0ae;");
                    } else {
                      
                        badge.setStyle(styleBase + "-fx-background-color: rgba(255, 152, 0, 0.2); -fx-text-fill: #ff9800;");
                    }
                    setGraphic(badge);
                    setText(null);
                }
            }
        });
    }

   
    private void configurerFiltres() {
      
        List<String> marques = masterData.stream()
                .map(Vehicule::getMarque)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        marques.add(0, "Toutes les marques");
        
        comboMarque.setItems(FXCollections.observableArrayList(marques));
        comboMarque.getSelectionModel().selectFirst();

        
        comboStatut.setItems(FXCollections.observableArrayList("Tous les statuts", "DISPO", "VENDU", "ATELIER"));
        comboStatut.getSelectionModel().selectFirst();

       
        txtRecherche.textProperty().addListener((obs, old, newValue) -> appliquerFiltre());
        comboMarque.valueProperty().addListener((obs, old, newValue) -> appliquerFiltre());
        comboStatut.valueProperty().addListener((obs, old, newValue) -> appliquerFiltre());
    }

   
    private void appliquerFiltre() {
        String recherche = (txtRecherche.getText() != null) ? txtRecherche.getText().toLowerCase() : "";
        String marqueSelectionnee = comboMarque.getValue();
        String statutSelectionne = comboStatut.getValue();

        filteredData.setPredicate(vehicule -> {
           
            boolean matchTexte = recherche.isEmpty() || 
                    vehicule.getModele().toLowerCase().contains(recherche) ||
                    vehicule.getMarque().toLowerCase().contains(recherche);

           
            boolean matchMarque = "Toutes les marques".equals(marqueSelectionnee) || 
                    vehicule.getMarque().equalsIgnoreCase(marqueSelectionnee);

           
            boolean matchStatut = "Tous les statuts".equals(statutSelectionne) || 
                    vehicule.getStatut().equalsIgnoreCase(statutSelectionne);

            return matchTexte && matchMarque && matchStatut;
        });
    }

   

    @FXML
    private void handleBtnDashboard(ActionEvent event) {
        switchScene(event, "Dashboard.fxml");
    }

    @FXML
    private void handleBtnCommerce(ActionEvent event) {
        switchScene(event, "Commerce.fxml");
    }

    @FXML
    private void handleAjouterVehicule() {
    	ouvrirFormulaireAjout();       
    }

    
    @FXML
    private void ouvrirFormulaireAjout() {
        try {
            // 1. Charger le FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FormulaireVehicule.fxml"));
            Parent root = loader.load();

            // 2. Créer la fenêtre (Stage)
            Stage popupStage = new Stage();
            popupStage.setTitle("Ajouter un véhicule");
            popupStage.setScene(new Scene(root));
            
            // 3. Bloquer la fenêtre principale tant que celle-ci est ouverte (Mode Modal)
            popupStage.initModality(Modality.APPLICATION_MODAL);
            
            // 4. Afficher
            popupStage.showAndWait(); // Attend que la fenêtre se ferme

            // 5. Une fois fermée, on rafraichit le tableau pour voir le nouveau véhicule
            chargerDonneesDepuisBDD();
            
            configurerFiltres();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
 
    private void switchScene(ActionEvent event, String fxmlFile) {
        try {
            URL resource = getClass().getResource(fxmlFile);
            if (resource == null) {
                System.err.println("ERREUR: " + fxmlFile + " introuvable.");
                return;
            }
            Parent root = FXMLLoader.load(resource);
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            System.err.println("ERREUR: Échec du chargement de la vue " + fxmlFile);
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleBtnAtelier(ActionEvent event) {
        switchScene(event, "Atelier.fxml");
    }
    
    
}