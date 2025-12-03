package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SingleConnection {

	private static Connection cn ;
	
	private SingleConnection() {
		try {
			cn = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+"garage monaco v2"+"?serverTimezone=UTC", "root", "");
			
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static Connection getInstance() {
		if(cn == null) {
			new SingleConnection();
		}
		return cn ;
	}
	
	public static void close(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cn = null ;

	}
	
	
}
