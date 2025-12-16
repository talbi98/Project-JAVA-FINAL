package JavaFXProject;

import Service.GarageService;
import Metier.Vehicule;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.util.List;

public class DashboardController {

    // === 1. CONNEXION AU BACKEND ===
    private GarageService service = new GarageService();

    // === 2. ÉLÉMENTS FXML ===
    @FXML private Label lblPrixMoyen;
    @FXML private Label lblTotalStock;
    @FXML private Label lblAtelier;

    @FXML private TableView<Vehicule> tableTop3;
    @FXML private TableColumn<Vehicule, String> colMarque;
    @FXML private TableColumn<Vehicule, String> colModele;
    @FXML private TableColumn<Vehicule, Double> colPrix;
    @FXML private TableColumn<Vehicule, String> colStatut;

    // === 3. INITIALISATION (Lancement auto) ===
    @FXML
    public void initialize() {
        try {
            chargerIndicateurs();
            chargerTableauTop3();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur chargement dashboard : " + e.getMessage());
        }
    }

    private void chargerIndicateurs() {
        // A. Prix Moyen (via méthode stream du Service)
        double moyenne = service.calculerPrixMoyenVentes();
        lblPrixMoyen.setText(String.format("%,.0f €", moyenne)); // Format 100 000 €

        // B. Total Stock (On filtre les voitures DISPO)
        List<Vehicule> tout = service.listerToutLeGarage();
        long nbDispo = tout.stream().filter(v -> "DISPO".equals(v.getStatut())).count();
        lblTotalStock.setText(String.valueOf(nbDispo));

        // C. En Atelier (On compte celles qui ne sont ni DISPO ni VENDU)
        // Astuce : Si tu n'as pas de méthode spécifique, on filtre la liste complète
        long nbAtelier = tout.stream()
                .filter(v -> !"DISPO".equals(v.getStatut()) && !"VENDU".equals(v.getStatut()))
                .count();
        lblAtelier.setText(String.valueOf(nbAtelier));
    }

    private void chargerTableauTop3() {
        // 1. Récupération des données Backend
        List<Vehicule> top3 = service.getTop3VoituresLuxe();
        ObservableList<Vehicule> data = FXCollections.observableArrayList(top3);

        // 2. Liaison Colonnes <-> Objet Métier
        colMarque.setCellValueFactory(new PropertyValueFactory<>("marque"));
        colModele.setCellValueFactory(new PropertyValueFactory<>("modele"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixVente"));
        
        // Colonne Statut avec rendu personnalisé (Badges de couleur)
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colStatut.setCellFactory(column -> new TableCell<Vehicule, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    Label badge = new Label(item);
                    badge.setStyle("-fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 5;");
                    
                    if ("VENDU".equals(item)) {
                        badge.setStyle(badge.getStyle() + "-fx-background-color: rgba(244, 67, 54, 0.2); -fx-text-fill: #ff6b6b;");
                    } else if ("DISPO".equals(item)) {
                        badge.setStyle(badge.getStyle() + "-fx-background-color: rgba(76, 175, 80, 0.2); -fx-text-fill: #69f0ae;");
                    } else {
                        badge.setStyle(badge.getStyle() + "-fx-background-color: rgba(255, 152, 0, 0.2); -fx-text-fill: #ff9800;");
                    }
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        // 3. Affichage
        tableTop3.setItems(data);
    }
}