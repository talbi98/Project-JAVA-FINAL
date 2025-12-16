package DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Metier.Client;

public class ClientDAO extends DAO<Client, Integer> {
	private ResultSet rs ;

    
    public Client create(Client c) {
        String sql = "INSERT INTO client (nom, prenom, email, telephone, vip_level) VALUES (?, ?, ?, ?, ?)";
        open();
        try  {
        	
        	PreparedStatement ps = connect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, c.getNom());
            ps.setString(2, c.getPrenom());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getTelephone());
            ps.setString(5, c.getVipLevel());

            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                c.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }

    
    public List<Client> findAll() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM client";
        open();


        try (Statement stmt = connect.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Client c = new Client(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("telephone"),
                    rs.getString("vip_level")
                );
                clients.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }

    
    public Client findById(int id) {
        String sql = "SELECT * FROM client WHERE id = ?";
        try {
        	PreparedStatement ps = connect.prepareStatement(sql);
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Client(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("telephone"),
                        rs.getString("vip_level")
                    );
                }
            }
        } catch (SQLException e) {
        	
            e.printStackTrace();
        }
        return null;
    }
    
    
    
    public void delete(Client toto) {
        open();

         String sql = "DELETE FROM client WHERE id = ?";
         try  {
        	 PreparedStatement stmt = connect.prepareStatement(sql);
             stmt.setInt(1, toto.getId());
             stmt.executeUpdate();
         } catch(SQLException e) { e.printStackTrace(); }
    }


    public Client update(Client c) {
        String sql = "UPDATE client SET nom=?, prenom=?, email=?, telephone=?, vip_level=? WHERE id=?";

        open();

        
        try  {
        	PreparedStatement ps = connect.prepareStatement(sql);
            ps.setString(1, c.getNom());
            ps.setString(2, c.getPrenom());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getTelephone());
            ps.setString(5, c.getVipLevel());

            ps.setInt(6, c.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c ;
    }
    
   //zaza//
//zizi//

	
}