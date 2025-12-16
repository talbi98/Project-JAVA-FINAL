package JavaFXProject; // Ou le nom exact du package

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application {

    private BorderPane root;
    // Liste pour stocker tous les boutons (pour gérer l'état "actif")
    private List<Button> menuButtons = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {

        root = new BorderPane();
        root.setStyle("-fx-background-color: #0a0a0c;");

        // 1. Création du menu latéral
        VBox sidebar = createSidebar();
        root.setLeft(sidebar);

        // 2. Afficher la page Dashboard par défaut au lancement
        // On simule un clic sur le premier bouton
        if (!menuButtons.isEmpty()) {
            menuButtons.get(0).fire();
        }

        Scene scene = new Scene(root, 1100, 750);
        primaryStage.setTitle("Garage Manager - Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPrefWidth(250);
        sidebar.setPadding(new Insets(30, 20, 30, 20));
        sidebar.setStyle("-fx-background-color: #0f0f11; -fx-border-color: #222; -fx-border-width: 0 1 0 0;");

        // --- LOGO ---
        HBox logoBox = new HBox(10);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        
        StackPane iconPane = new StackPane();
        Rectangle bgSquare = new Rectangle(35, 35);
        bgSquare.setFill(Color.web("#f59e0b"));
        bgSquare.setArcWidth(10);
        bgSquare.setArcHeight(10);
        Label letterG = new Label("G");
        letterG.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");
        iconPane.getChildren().addAll(bgSquare, letterG);

        Label brandName = new Label("GARAGE");
        brandName.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        Label brandSub = new Label("MGR");
        brandSub.setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold; -fx-font-size: 14px;");
        
        logoBox.getChildren().addAll(iconPane, brandName, brandSub);

        // --- TITRE MENU ---
        Label menuTitle = new Label("MENU PRINCIPAL");
        menuTitle.setStyle("-fx-text-fill: #666; -fx-font-size: 10px; -fx-padding: 20 0 5 0;");

        // --- CREATION DES BOUTONS ---
        // On définit ici quel texte a le bouton ET quelle page il affiche
        
        Button btnDashboard = createMenuButton("Dashboard");
        btnDashboard.setOnAction(e -> {
            highlightButton(btnDashboard);
            root.setCenter(createPageContent("Tableau de Bord", "Vue d'ensemble de l'activité."));
        });

        Button btnPlanning = createMenuButton("Planning du garage");
        btnPlanning.setOnAction(e -> {
            highlightButton(btnPlanning);
            root.setCenter(createPageContent("Planning", "Gestion des rendez-vous mécaniques."));
        });

        Button btnClients = createMenuButton("Clients");
        btnClients.setOnAction(e -> {
            highlightButton(btnClients);
            root.setCenter(createPageContent("Clients", "Liste et gestion des clients."));
        });

        Button btnInvoices = createMenuButton("Factures");
        btnInvoices.setOnAction(e -> {
            highlightButton(btnInvoices);
            root.setCenter(createPageContent("Facturation", "Historique des factures et paiements."));
        });

        sidebar.getChildren().addAll(logoBox, menuTitle, btnDashboard, btnPlanning, btnClients, btnInvoices);
        return sidebar;
    }

    // Crée un bouton standard et l'ajoute à la liste
    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(12, 15, 12, 15));
        
        // Style de base (inactif)
        setButtonStyle(btn, false);

        // Effet de survol (Hover)
        btn.setOnMouseEntered(e -> {
            // Si le bouton n'est pas celui actif actuellement, on change sa couleur
            if (!isButtonActive(btn)) {
                btn.setStyle("-fx-background-color: #1a1a1d; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;");
            }
        });
        
        btn.setOnMouseExited(e -> {
            // Si le bouton n'est pas actif, on remet le style transparent
            if (!isButtonActive(btn)) {
                setButtonStyle(btn, false);
            }
        });

        menuButtons.add(btn); // On l'ajoute à la liste pour le retrouver plus tard
        return btn;
    }

    // Change visuellement le bouton actif
    private void highlightButton(Button targetBtn) {
        // 1. On éteint tous les boutons
        for (Button btn : menuButtons) {
            setButtonStyle(btn, false);
        }
        // 2. On allume le bouton cliqué
        setButtonStyle(targetBtn, true);
    }

    // Applique le style CSS (Actif ou Inactif)
    private void setButtonStyle(Button btn, boolean isActive) {
        String commonStyle = "-fx-font-size: 14px; -fx-cursor: hand; ";
        
        if (isActive) {
            btn.setStyle(commonStyle +
                         "-fx-text-fill: white; " +
                         "-fx-background-color: #2b2b2e; " +
                         "-fx-border-color: #f59e0b; " +
                         "-fx-border-width: 0 0 0 3;"); // Bordure orange à gauche
        } else {
            btn.setStyle(commonStyle +
                         "-fx-text-fill: #888; " +
                         "-fx-background-color: transparent;");
        }
    }

    // Petite méthode utilitaire pour vérifier si un bouton a la bordure orange (donc actif)
    private boolean isButtonActive(Button btn) {
        return btn.getStyle().contains("-fx-border-color: #f59e0b");
    }

    // --- METHODE TEMPORAIRE POUR GENERER LE CONTENU DES PAGES ---
    // Plus tard, tu remplaceras ça par tes classes "DashboardView", "ClientsView", etc.
    private Node createPageContent(String titleText, String subTitleText) {
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #0a0a0c;"); // Fond noir
        
        Label title = new Label(titleText);
        title.setStyle("-fx-text-fill: white; -fx-font-size: 32px; -fx-font-weight: bold;");
        
        Label subTitle = new Label(subTitleText);
        subTitle.setStyle("-fx-text-fill: #666; -fx-font-size: 16px;");
        
        box.getChildren().addAll(title, subTitle);
        return box;
    }

    public static void main(String[] args) {
        launch(args);
    }
}