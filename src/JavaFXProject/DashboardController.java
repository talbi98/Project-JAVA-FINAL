package JavaFXProject;



import Service.GarageService;
import Metier.Vehicule;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class DashboardController {

    // --- 1. Injection du Service Backend ---
    private GarageService service = new GarageService();

    // --- 2. Injection des éléments graphiques du FXML ---
    @FXML private Label lblPrixMoyen;
    @FXML private Label lblTotalStock;
    @FXML private Label lblAtelier;

    @FXML private TableView<Vehicule> tableTop3;
    @FXML private TableColumn<Vehicule, String> colMarque;
    @FXML private TableColumn<Vehicule, String> colModele;
    @FXML private TableColumn<Vehicule, Double> colPrix;
    @FXML private TableColumn<Vehicule, String> colStatut;

    // --- 3. Méthode appelée automatiquement au chargement ---
    @FXML
    public void initialize() {
        try {
            chargerStatistiques();
            chargerTop3();
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des données : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void chargerStatistiques() {
        // A. Prix Moyen des Ventes
        double moyenne = service.calculerPrixMoyenVentes();
        lblPrixMoyen.setText(String.format("%.0f €", moyenne));

        // B. Total Stock (On utilise listerToutLeGarage et on compte)
        List<Vehicule> tout = service.listerToutLeGarage();
        long nbDispo = tout.stream().filter(v -> "DISPO".equals(v.getStatut())).count();
        lblTotalStock.setText(String.valueOf(nbDispo));
        
        // C. Atelier (Texte simple pour l'instant)
        lblAtelier.setText("Ouvert");
    }

    private void chargerTop3() {
        // Récupérer la liste depuis le backend
        List<Vehicule> top3 = service.getTop3VoituresLuxe();

        // Convertir en liste Observable pour JavaFX
        ObservableList<Vehicule> data = FXCollections.observableArrayList(top3);

        // Lier les colonnes aux attributs de la classe Vehicule
        colMarque.setCellValueFactory(new PropertyValueFactory<>("marque"));
        colModele.setCellValueFactory(new PropertyValueFactory<>("modele"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixVente"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Remplir le tableau
        tableTop3.setItems(data);
    }
}