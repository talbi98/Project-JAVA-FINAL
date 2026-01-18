package JavaFXProject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {

	@FXML
	private TextField txtUser;
	@FXML
	private PasswordField txtPass;
	@FXML
	private Label lblError;

	@FXML
	private void handleLogin(ActionEvent event) {
		String user = txtUser.getText();
		String pass = txtPass.getText();

		if (user.isEmpty() || pass.isEmpty()) {
			lblError.setText("Veuillez remplir tous les champs.");
			return;
		}

		if ("admin".equals(user) && "admin".equals(pass)) {
			connexionReussie(event, "Administrateur", "ADMIN");
		} else if ("user".equals(user) && "user".equals(pass)) {
			connexionReussie(event, "Invité", "USER");
		} else {
			lblError.setText("Identifiant ou mot de passe incorrect.");
		}
	}

	private void connexionReussie(ActionEvent event, String nom, String role) {
		try {
			Session.setSession(nom, role);

			Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
			Scene scene = new Scene(root);

			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(scene);
			stage.centerOnScreen();
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
			lblError.setText("Erreur au chargement du Dashboard.");
		}
	}
}