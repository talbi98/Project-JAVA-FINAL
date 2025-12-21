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

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contrôleur pour la vue Stock.fxml.
 * Gère l'affichage du parc automobile, les filtres dynamiques et la navigation.
 */
public class StockController {

    // Connexion au service métier (Lien avec les DAO)
    private GarageService service = new GarageService();
    
    // Listes pour la gestion du tableau et du filtrage en temps réel
    private ObservableList<Vehicule> masterData = FXCollections.observableArrayList();
    private FilteredList<Vehicule> filteredData;

    // --- Injections FXML (fx:id dans le fichier Stock.fxml) ---
    @FXML private TextField txtRecherche;
    @FXML private ComboBox<String> comboMarque;
    @FXML private ComboBox<String> comboStatut;

    @FXML private TableView<Vehicule> tableStock;
    @FXML private TableColumn<Vehicule, Integer> colId;
    @FXML private TableColumn<Vehicule, String> colMarque;
    @FXML private TableColumn<Vehicule, String> colModele;
    @FXML private TableColumn<Vehicule, Double> colPrix;
    @FXML private TableColumn<Vehicule, String> colStatut;

    /**
     * Méthode d'initialisation appelée automatiquement par JavaFX.
     */
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

    /**
     * Récupère les données depuis la base de données via le service.
     */
    private void chargerDonneesDepuisBDD() {
        List<Vehicule> liste = service.listerToutLeGarage();
        masterData.clear();
        masterData.addAll(liste);
        
        // On crée une liste filtrée basée sur la liste principale
        filteredData = new FilteredList<>(masterData, p -> true);
        tableStock.setItems(filteredData);
    }

    /**
     * Définit le style et le contenu des colonnes du tableau.
     */
    private void configurerColonnes() {
        // Mappage direct des propriétés
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMarque.setCellValueFactory(new PropertyValueFactory<>("marque"));
        colModele.setCellValueFactory(new PropertyValueFactory<>("modele"));
        
        // Formatage du prix (Euros + Gras)
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

        // Gestion des badges pour la colonne Statut
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
                        // Statut ATELIER ou autre
                        badge.setStyle(styleBase + "-fx-background-color: rgba(255, 152, 0, 0.2); -fx-text-fill: #ff9800;");
                    }
                    setGraphic(badge);
                    setText(null);
                }
            }
        });
    }

    /**
     * Initialise les ComboBox et les écouteurs pour la recherche.
     */
    private void configurerFiltres() {
        // Liste dynamique des marques présentes en stock
        List<String> marques = masterData.stream()
                .map(Vehicule::getMarque)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        marques.add(0, "Toutes les marques");
        
        comboMarque.setItems(FXCollections.observableArrayList(marques));
        comboMarque.getSelectionModel().selectFirst();

        // Liste des statuts possibles
        comboStatut.setItems(FXCollections.observableArrayList("Tous les statuts", "DISPO", "VENDU", "ATELIER"));
        comboStatut.getSelectionModel().selectFirst();

        // Ajout des listeners pour filtrer en temps réel (Search while you type)
        txtRecherche.textProperty().addListener((obs, old, newValue) -> appliquerFiltre());
        comboMarque.valueProperty().addListener((obs, old, newValue) -> appliquerFiltre());
        comboStatut.valueProperty().addListener((obs, old, newValue) -> appliquerFiltre());
    }

    /**
     * Logique de filtrage multicritère.
     */
    private void appliquerFiltre() {
        String recherche = (txtRecherche.getText() != null) ? txtRecherche.getText().toLowerCase() : "";
        String marqueSelectionnee = comboMarque.getValue();
        String statutSelectionne = comboStatut.getValue();

        filteredData.setPredicate(vehicule -> {
            // Filtre 1 : Texte (recherche dans le modèle ou la marque)
            boolean matchTexte = recherche.isEmpty() || 
                    vehicule.getModele().toLowerCase().contains(recherche) ||
                    vehicule.getMarque().toLowerCase().contains(recherche);

            // Filtre 2 : Marque
            boolean matchMarque = "Toutes les marques".equals(marqueSelectionnee) || 
                    vehicule.getMarque().equalsIgnoreCase(marqueSelectionnee);

            // Filtre 3 : Statut
            boolean matchStatut = "Tous les statuts".equals(statutSelectionne) || 
                    vehicule.getStatut().equalsIgnoreCase(statutSelectionne);

            return matchTexte && matchMarque && matchStatut;
        });
    }

    // --- Actions et Navigation (onAction) ---

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
        System.out.println("LOG: Action d'ajout de véhicule déclenchée.");
        // À implémenter : Ouverture d'une fenêtre modale
    }

    /**
     * Utilitaire pour changer de scène de manière fluide.
     */
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
}