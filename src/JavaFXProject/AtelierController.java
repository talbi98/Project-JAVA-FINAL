package JavaFXProject;

import Service.GarageService;
import Metier.Intervention;
import Metier.Vehicule;
import Metier.Mecanicien; // Importe ton métier
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
import java.net.URL;
import java.util.List;

public class AtelierController {

    private GarageService service = new GarageService();

    @FXML private TableView<Intervention> tableReparations;
    @FXML private TableColumn<Intervention, String> colVehicule;
    @FXML private TableColumn<Intervention, String> colMecanicien; // Nouvelle colonne
    @FXML private TableColumn<Intervention, String> colDescription;
    @FXML private TableColumn<Intervention, Double> colPrix;
    @FXML private TableColumn<Intervention, String> colStatut;

    @FXML
    public void initialize() {
        configurerColonnes();
        chargerDonnees();
    }

    private void configurerColonnes() {
        // 1. Véhicule (Marque + Modèle)
        colVehicule.setCellValueFactory(cell -> {
            Vehicule v = cell.getValue().getVehicule();
            if(v != null) return new SimpleStringProperty(v.getMarque() + " " + v.getModele());
            return new SimpleStringProperty("Inconnu");
        });

        // 2. Mécanicien (Nom)
        colMecanicien.setCellValueFactory(cell -> {
            Mecanicien m = cell.getValue().getMecanicien();
            if(m != null) return new SimpleStringProperty(m.getNom() + " " + m.getPrenom());
            return new SimpleStringProperty("Non assigné");
        });

        // 3. Description
        colDescription.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDescription()));
        
        // 4. Prix
        colPrix.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getPrixMainOeuvre()));
        colPrix.setCellFactory(column -> new TableCell<Intervention, Double>() {
             @Override protected void updateItem(Double item, boolean empty) {
                 super.updateItem(item, empty);
                 if (empty || item == null) setText(null);
                 else {
                     setText(String.format("%,.0f €", item));
                     setStyle("-fx-font-weight: bold;");
                 }
             }
        });

        // 5. Statut (Avec couleurs)
        colStatut.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatut()));
        colStatut.setCellFactory(column -> new TableCell<Intervention, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.setStyle("-fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 5;");
                    
                    if ("EN_COURS".equals(item)) {
                        badge.setStyle(badge.getStyle() + "-fx-background-color: rgba(33, 150, 243, 0.2); -fx-text-fill: #2196f3;"); // Bleu
                    } else if ("TERMINE".equals(item)) {
                        badge.setStyle(badge.getStyle() + "-fx-background-color: rgba(76, 175, 80, 0.2); -fx-text-fill: #4CAF50;"); // Vert
                    }
                    setGraphic(badge); setText(null);
                }
            }
        });
    }

    private void chargerDonnees() {
        List<Intervention> liste = service.listerInterventions();
        tableReparations.setItems(FXCollections.observableArrayList(liste));
    }

    @FXML
    private void handleNouvelleIntervention() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FormulaireIntervention.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Nouvelle Intervention");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            chargerDonnees(); // Rafraichir après fermeture
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTerminerIntervention() {
        Intervention selected = tableReparations.getSelectionModel().getSelectedItem();
        if (selected == null) {
            System.out.println("Sélectionnez une ligne !");
            return;
        }
        if ("TERMINE".equals(selected.getStatut())) {
            System.out.println("Cette intervention est déjà terminée !");
            return;
        }

        service.terminerIntervention(selected);
        System.out.println("Intervention terminée.");
        chargerDonnees();
    }

    // --- NAVIGATION ---
    @FXML private void handleBtnDashboard(ActionEvent e) { switchScene(e, "Dashboard.fxml"); }
    @FXML private void handleBtnStock(ActionEvent e) { switchScene(e, "Stock.fxml"); }
    @FXML private void handleBtnCommerce(ActionEvent e) { switchScene(e, "Commerce.fxml"); }

    private void switchScene(ActionEvent event, String fxmlFile) {
        try {
            URL resource = getClass().getResource(fxmlFile);
            if(resource == null) { System.err.println("Introuvable: " + fxmlFile); return; }
            Parent root = FXMLLoader.load(resource);
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);
        } catch (IOException e) { e.printStackTrace(); }
    }
    
    @FXML
    private void handleBtnAtelier(ActionEvent event) {
        switchScene(event, "Atelier.fxml");
    }
}