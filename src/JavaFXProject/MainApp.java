package JavaFXProject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. On charge le fichier FXML
            Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
            
            // 2. On crée la scène
            Scene scene = new Scene(root);
            
            // 3. Configuration de la fenêtre
            primaryStage.setTitle("Monaco Luxury Garage - Manager V1.0");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false); // On fige la taille pour garder le design propre
            
            // 4. Affichage
            primaryStage.show();
            
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("Erreur : Impossible de charger Dashboard.fxml");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}