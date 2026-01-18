package JavaFXProject;

public class Session {

	private static String roleActuel = "";
	private static String nomUtilisateur = "";

	public static void setSession(String nom, String role) {
		nomUtilisateur = nom;
		roleActuel = role;
	}

	public static String getNom() {
		return nomUtilisateur;
	}

	public static boolean isAdmin() {
		return "ADMIN".equals(roleActuel);
	}

	public static void logout() {
		nomUtilisateur = "";
		roleActuel = "";
	}
}