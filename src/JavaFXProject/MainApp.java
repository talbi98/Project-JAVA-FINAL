package JavaFXProject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

   
    public void start(Stage primaryStage) {
        try {
           
            Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
            Scene scene = new Scene(root);
            
            primaryStage.setTitle("Monaco Luxury Garage - Manager V2.0");
            primaryStage.setScene(scene);
            
            
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);
            
            primaryStage.show();
            
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println(" Erreur critique : Impossible de charger l'interface.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}