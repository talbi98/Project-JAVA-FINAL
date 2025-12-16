package DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import Metier.Employe;
import Metier.Mecanicien;
import Metier.Vendeur;

public class EmployeDAO extends DAO<Employe, Integer> {
	private ResultSet rs;

	public Employe create(Employe e) {
		String sql = "INSERT INTO employe (type_employe, nom, prenom, login, password, commission_pct, specialite) VALUES (?, ?, ?, ?, ?, ?, ?)";

		open();
		try {

			PreparedStatement ps = connect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setString(2, e.getNom());
			ps.setString(3, e.getPrenom());
			ps.setString(4, "user" + (int) (Math.random() * 1000));
			ps.setString(5, "1234"); //

			if (e instanceof Vendeur) {
				ps.setString(1, "VENDEUR");
				ps.setDouble(6, ((Vendeur) e).getCommissionPct());
				ps.setNull(7, Types.VARCHAR);
			} else if (e instanceof Mecanicien) {
				ps.setString(1, "MECANICIEN");
				ps.setNull(6, Types.DOUBLE);
				ps.setString(7, ((Mecanicien) e).getSpecialite());
			}

			ps.executeUpdate();

			rs = ps.getGeneratedKeys();
			
			if (rs.next()) {
				
				e.setId(rs.getInt(1));
				
			}

		} catch (SQLException ex) {
			ex.printStackTrace();

		}

		return e;
	}

	public List<Employe> findAll() 
	{
		List<Employe> liste = new ArrayList<>();
		String sql = "SELECT * FROM employe";

		try  {
			Statement stmt = connect.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			while (rs.next()) {
				String type = rs.getString("type_employe");
				Employe e = null;

				if ("VENDEUR".equals(type)) {
					e = new Vendeur(rs.getInt("id"),
							rs.getString("nom"),
							rs.getString("prenom"), 
							rs.getString("login"),
							rs.getString("password"), 
							rs.getDouble("commission_pct"));
					
				} else if ("MECANICIEN".equals(type)) {
					e = new Mecanicien(rs.getInt("id"), 
							rs.getString("nom"), 
							rs.getString("prenom"),
							rs.getString("login"), 
							rs.getString("password"), 
							rs.getString("specialite"));
				}
				
				if (e != null)
					liste.add(e);
			}
			
			
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		
		return liste;
		
		
	}
	
	

	public static Employe findById(int id) {
		
		String sql = "SELECT * FROM employe WHERE id = ?";
		Employe e = null;

		try {
			PreparedStatement ps = connect.prepareStatement(sql) ;
			ps.setInt(1, id);

			try (ResultSet rs = ps.executeQuery()) {
				
				if (rs.next()) {
					String type = rs.getString("type_employe");

					if ("VENDEUR".equals(type)) {
						e = new Vendeur(rs.getInt("id"),
								rs.getString("nom"),
								rs.getString("prenom"),
								rs.getString("login"), 
								rs.getString("password"), 
								rs.getDouble("commission_pct"));
						
					} else if ("MECANICIEN".equals(type)) {
						e = new Mecanicien(rs.getInt("id"),
								rs.getString("nom"),
								rs.getString("prenom"),
								rs.getString("login"), 
								rs.getString("password"), 
								rs.getString("specialite"));
						
					}
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return e;
	}

	
	
	public Employe update(Employe obj) {
		String sql = "UPDATE employe SET nom=?, prenom=?, login=?, password=?, type_employe=?, commission_pct=?, specialite=? WHERE id=?";

		try {

			PreparedStatement ps = connect.prepareStatement(sql);
			ps.setString(1, obj.getNom());
			ps.setString(2, obj.getPrenom());
			ps.setString(3, "user" + obj.getId());
			ps.setString(4, "1234");

			if (obj instanceof Vendeur) {
				ps.setString(5, "VENDEUR");
				ps.setDouble(6, ((Vendeur) obj).getCommissionPct());
				ps.setNull(7, Types.VARCHAR);
			} else if (obj instanceof Mecanicien) {
				ps.setString(5, "MECANICIEN");
				ps.setNull(6, Types.DOUBLE);
				ps.setString(7, ((Mecanicien) obj).getSpecialite());
			}

			ps.setInt(8, obj.getId());

			ps.executeUpdate();

		} catch (SQLException ex) {
			ex.printStackTrace();

		}
		return obj;
	}
	
	
	

	public void delete(Employe obj) {
		String sql = "DELETE FROM employe WHERE id = ?";

		try  {
			PreparedStatement ps = connect.prepareStatement(sql);
			
			ps.setInt(1, obj.getId());
			ps.executeUpdate();
			
			System.out.println("Employé supprimé avec succès.");
			
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

}