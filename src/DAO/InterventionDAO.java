package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Metier.Employe;
import Metier.Intervention;
import Metier.Mecanicien;
import Metier.Vehicule;

public class InterventionDAO extends DAO<Intervention, Integer> {

	private VehiculeDAO vehiculeDAO = new VehiculeDAO();
	private EmployeDAO employeDAO = new EmployeDAO();

	
	
	public Intervention create(Intervention i) {
		
		String sql = "INSERT INTO intervention (date_debut, description, statut, vehicule_id, mecanicien_id) VALUES (?, ?, ?, ?, ?)";

		try  {
			PreparedStatement ps = connect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			ps.setDate(1, new java.sql.Date(i.getDateDebut().getTime()));
			ps.setString(2, i.getDescription());
			ps.setString(3, i.getStatut());
			ps.setInt(4, i.getVehicule().getId());
			ps.setInt(5, i.getMecanicien().getId());

			ps.executeUpdate();

			ResultSet rs = ps.getGeneratedKeys();
			
			if (rs.next()) {
				i.setId(rs.getInt(1));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return i;
	}

	public List<Intervention> findAll() {
		List<Intervention> liste = new ArrayList<>();
		String sql = "SELECT * FROM intervention";

		try  {
			
			Statement stmt = connect.createStatement(); 
			ResultSet rs = stmt.executeQuery(sql);
			
			
			while (rs.next()) {
				
				Vehicule v = vehiculeDAO.findById(rs.getInt("vehicule_id"));
				Employe e = employeDAO.findById(rs.getInt("mecanicien_id"));

				if (v != null && e instanceof Mecanicien) {
					Intervention i = new Intervention(rs.getInt("id"), rs.getDate("date_debut"), rs.getDate("date_fin"), 
					rs.getString("description"), rs.getString("statut"), v, (Mecanicien) e, 0.0  );
				
					liste.add(i);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return liste;
	}

	public Intervention findById(Integer id) {
		
		String sql = "SELECT * FROM intervention WHERE id = ?";
		
		try  {
			PreparedStatement ps = connect.prepareStatement(sql);
			
			ps.setInt(1, id);
		
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Vehicule v = vehiculeDAO.findById(rs.getInt("vehicule_id"));
					Employe e = employeDAO.findById(rs.getInt("mecanicien_id"));

					if (v != null && e instanceof Mecanicien) {
						return new Intervention(rs.getInt("id"), rs.getDate("date_debut"), rs.getDate("date_fin"),
								rs.getString("description"), rs.getString("statut"), v, (Mecanicien) e, 0.0);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	public Intervention update(Intervention i) {
        String sql = "UPDATE intervention SET date_fin=?, statut=?, description=? WHERE id=?";

        try (PreparedStatement ps = connect.prepareStatement(sql)) {

            if (i.getDateFin() != null) {
                ps.setDate(1, new java.sql.Date(i.getDateFin().getTime()));
            } else {
                ps.setNull(1, Types.DATE);
            }

            ps.setString(2, i.getStatut());
            ps.setString(3, i.getDescription());
            ps.setInt(4, i.getId());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                return i;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
	
	

	public void delete(Integer id) {
	}

	public void delete(Intervention obj) {
	}
}