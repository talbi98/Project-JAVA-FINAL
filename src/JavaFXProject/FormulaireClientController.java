package JavaFXProject;

import Service.GarageService;
import Metier.Client; 
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FormulaireClientController {

    // Ces champs doivent correspondre aux fx:id de ton FXML
    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private TextField txtTel;
    @FXML private TextField txtEmail;
    
    @FXML private Button btnValider;
    @FXML private Button btnAnnuler;

    private GarageService service = new GarageService();

    @FXML
    public void initialize() {
        // Liaison des boutons aux méthodes
        btnValider.setOnAction(e -> ajouterClient());
        btnAnnuler.setOnAction(e -> fermerFenetre());
    }

    private void ajouterClient() {
        try {
            System.out.println("--- Ajout Client en cours ---");

            // 1. Récupérer les infos des champs texte
            String nom = txtNom.getText();
            String prenom = txtPrenom.getText();
            String tel = txtTel.getText();
            String email = txtEmail.getText();

            // Petite validation de sécurité
            if (nom.isEmpty() || prenom.isEmpty()) {
                System.err.println("Erreur : Le Nom et le Prénom sont obligatoires !");
                return;
            }

            // 2. Créer l'objet Client
            // ON UTILISE TON 2ème CONSTRUCTEUR
            // Il va mettre automatiquement vipLevel à "STANDARD"
            Client nouveauClient = new Client(nom, prenom, email, tel);
            
            // Si tu voulais changer le VIP, tu pourrais faire :
            // nouveauClient.setVipLevel("GOLD");

            System.out.println("Client créé en Java : " + nouveauClient.toString());

            // 3. Envoyer à la BDD via le Service
            service.ajouterClient(nouveauClient); 
            
            System.out.println("Succès ! Fermeture de la fenêtre.");
            fermerFenetre();

        } catch (Exception ex) {
            System.err.println("Erreur lors de l'ajout du client :");
            ex.printStackTrace();
        }
    }

    private void fermerFenetre() {
        // Récupère la fenêtre (Stage) actuelle via le bouton et la ferme
        Stage stage = (Stage) btnAnnuler.getScene().getWindow();
        stage.close();
    }
}