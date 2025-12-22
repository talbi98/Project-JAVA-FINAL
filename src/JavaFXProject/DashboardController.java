package JavaFXProject;

import Service.GarageService;
import Metier.Employe;
import Metier.Mecanicien;
import Metier.Vehicule;
import Metier.Vendeur;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class DashboardController {

    
    private GarageService service = new GarageService();

   
    @FXML private Label lblPrixMoyen;
    @FXML private Label lblTotalStock;
    @FXML private Label lblAtelier;

   
    @FXML private TableView<Vehicule> tableTop3;
    @FXML private TableColumn<Vehicule, String> colMarque;
    @FXML private TableColumn<Vehicule, String> colModele;
    @FXML private TableColumn<Vehicule, Double> colPrix;
    @FXML private TableColumn<Vehicule, String> colStatut;

   
    @FXML private TableView<Employe> tableEmployes;
    @FXML private TableColumn<Employe, String> colEmpNom;
    @FXML private TableColumn<Employe, String> colEmpPrenom;
    @FXML private TableColumn<Employe, String> colEmpPoste;

   
    @FXML
    public void initialize() {
        try {
            chargerIndicateurs();
            chargerTableauTop3();
            chargerTableauEmployes(); 
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur chargement dashboard : " + e.getMessage());
        }
    }

    
    
    @FXML
    private void handleBtnStock(ActionEvent event) {
        System.out.println("Clic sur bouton Stock...");
        switchScene(event, "Stock.fxml");
    }

    @FXML
    private void handleBtnCommerce(ActionEvent event) {
        System.out.println("Clic sur bouton Commerce...");
        switchScene(event, "Commerce.fxml");
    }

   
    private void switchScene(ActionEvent event, String fxmlFile) {
        try {
            URL resource = getClass().getResource(fxmlFile);
            if (resource == null) {
                System.err.println("ERREUR : Le fichier " + fxmlFile + " est introuvable dans le dossier JavaFXProject.");
                return;
            }
            
            Parent root = FXMLLoader.load(resource);
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);
            System.out.println("Navigation réussie vers : " + fxmlFile);
            
        } catch (IOException e) {
            System.err.println("Erreur critique lors du chargement de " + fxmlFile);
            e.printStackTrace();
        }
    }

    private void chargerIndicateurs() {
     
        double caTotal = service.calculerPrixMoyenVentes();
        lblPrixMoyen.setText(String.format("%,.0f €", caTotal));

       
        List<Vehicule> tout = service.listerToutLeGarage();
        long nbDispo = tout.stream().filter(v -> "DISPO".equals(v.getStatut())).count();
        lblTotalStock.setText(String.valueOf(nbDispo));

       
        long nbAtelier = tout.stream()
                .filter(v -> "ATELIER".equals(v.getStatut()) || "EN_COURS".equals(v.getStatut()))
                .count();
        lblAtelier.setText(String.valueOf(nbAtelier));
    }

    private void chargerTableauTop3() {
        List<Vehicule> top3 = service.getTop3VoituresLuxe();
        ObservableList<Vehicule> data = FXCollections.observableArrayList(top3);

        colMarque.setCellValueFactory(new PropertyValueFactory<>("marque"));
        colModele.setCellValueFactory(new PropertyValueFactory<>("modele"));
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

        tableTop3.setItems(data);
    }
    
    private void chargerTableauEmployes() {
        List<Employe> lesEmployes = service.listerEmployes(); 
        ObservableList<Employe> data = FXCollections.observableArrayList(lesEmployes);

        colEmpNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colEmpPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));

        colEmpPoste.setCellValueFactory(cellData -> {
            Employe e = cellData.getValue();
            if (e instanceof Vendeur) {
                return new SimpleStringProperty("Vendeur");
            } else if (e instanceof Mecanicien) {
                return new SimpleStringProperty("Mécanicien");
            } else {
                return new SimpleStringProperty("Autre");
            }
        });

        tableEmployes.setItems(data);
    }
}