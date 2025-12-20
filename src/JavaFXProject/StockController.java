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

import java.io.IOException;
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
    // La colonne colAnnee a été retirée car la propriété n'existe pas dans le métier 
    @FXML private TableColumn<Vehicule, Double> colPrix;
    @FXML private TableColumn<Vehicule, String> colStatut;

    @FXML
    public void initialize() {
        try {
            chargerDonneesInitiales();
            setupTableauColonnes();
            setupFiltres();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur chargement Stock : " + e.getMessage());
        }
    }

    private void chargerDonneesInitiales() {
        List<Vehicule> liste = service.listerToutLeGarage();
        masterData.clear();
        masterData.addAll(liste);
        
        filteredData = new FilteredList<>(masterData, p -> true);
        tableStock.setItems(filteredData);
    }

    private void setupTableauColonnes() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMarque.setCellValueFactory(new PropertyValueFactory<>("marque"));
        colModele.setCellValueFactory(new PropertyValueFactory<>("modele"));
        // Ligne supprimée : colAnnee.setCellValueFactory(new PropertyValueFactory<>("annee"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixVente"));
        
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colStatut.setCellFactory(column -> new TableCell<Vehicule, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    String style = "-fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 4; -fx-font-size: 11px; ";
                    
                    if ("VENDU".equals(item)) {
                        style += "-fx-background-color: rgba(244, 67, 54, 0.15); -fx-text-fill: #ff6b6b;";
                    } else if ("DISPO".equals(item)) {
                        style += "-fx-background-color: rgba(76, 175, 80, 0.15); -fx-text-fill: #69f0ae;";
                    } else {
                        style += "-fx-background-color: rgba(255, 152, 0, 0.15); -fx-text-fill: #ff9800;";
                    }
                    badge.setStyle(style);
                    setGraphic(badge);
                    setText(null);
                }
            }
        });
    }

    private void setupFiltres() {
        List<String> marques = masterData.stream()
                .map(Vehicule::getMarque)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        marques.add(0, "Toutes");
        
        if (comboMarque != null) {
            comboMarque.setItems(FXCollections.observableArrayList(marques));
            comboMarque.getSelectionModel().selectFirst();
            comboMarque.valueProperty().addListener((obs, old, newValue) -> updateFiltre());
        }

        if (comboStatut != null) {
            comboStatut.setItems(FXCollections.observableArrayList("Tous", "DISPO", "VENDU", "ATELIER", "RESERVE"));
            comboStatut.getSelectionModel().selectFirst();
            comboStatut.valueProperty().addListener((obs, old, newValue) -> updateFiltre());
        }

        if (txtRecherche != null) {
            txtRecherche.textProperty().addListener((obs, old, newValue) -> updateFiltre());
        }
    }

    private void updateFiltre() {
        String recherche = (txtRecherche != null) ? txtRecherche.getText().toLowerCase() : "";
        String marqueSelectionnee = (comboMarque != null) ? comboMarque.getValue() : "Toutes";
        String statutSelectionne = (comboStatut != null) ? comboStatut.getValue() : "Tous";

        filteredData.setPredicate(vehicule -> {
            boolean matchRecherche = recherche.isEmpty() || 
                                     vehicule.getMarque().toLowerCase().contains(recherche) || 
                                     vehicule.getModele().toLowerCase().contains(recherche);

            boolean matchMarque = "Toutes".equals(marqueSelectionnee) || 
                                  vehicule.getMarque().equalsIgnoreCase(marqueSelectionnee);

            boolean matchStatut = "Tous".equals(statutSelectionne) || 
                                  vehicule.getStatut().equalsIgnoreCase(statutSelectionne);

            return matchRecherche && matchMarque && matchStatut;
        });
    }

    @FXML
    private void handleBtnDashboard(ActionEvent event) {
        try {
            Parent dashboardView = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
            Scene currentScene = ((Node) event.getSource()).getScene();
            currentScene.setRoot(dashboardView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}