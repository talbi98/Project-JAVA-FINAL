package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SingleConnection {

	private static Connection cn;

	private SingleConnection() {
		try {

			// mac
			cn = DriverManager.getConnection(
					"jdbc:mysql://localhost:8889/" + "garage monaco v2" + "?serverTimezone=UTC", "root", "root");

			// ISMA:
			// cn = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+"garage
			// monaco v2"+"?serverTimezone=UTC", "root", "");

			System.out.println("Connexion à la base de données réussie !");

		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Erreur de connexion : Vérifiez que MAMP est lancé et que le port est bien 8889.");
		}
	}

	public static Connection getInstance() {
		if (cn == null) {
			new SingleConnection();
		}
		return cn;
	}

	public static void close(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		cn = null;

	}

}
