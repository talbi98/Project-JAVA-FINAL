package DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Metier.Client;
import Metier.Employe;
import Metier.Vehicule;
import Metier.Vendeur;
import Metier.Vente;

public class VenteDAO extends DAO<Vente, Integer> {
    
    private ResultSet rs ;

    private VehiculeDAO vehiculeDAO = new VehiculeDAO();
    private ClientDAO clientDAO = new ClientDAO();
    // private EmployeDAO employeDAO = new EmployeDAO(); // A décommenter si tu as fini findById dans EmployeDAO

    
    public Vente create(Vente v) {
        String sql = "INSERT INTO vente (date_vente, montant_final, vehicule_id, client_id, vendeur_id) VALUES (?, ?, ?, ?, ?)";
        
        try  {
        	
        	PreparedStatement ps = connect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setDate(1, v.getdateVente() ); 
            ps.setDouble(2, v.getMontantFinal());
            
            // ICI ON MET LES CLÉS ÉTRANGÈRES
            ps.setInt(3, v.getVehicule().getId());
            ps.setInt(4, v.getClient().getId());
            ps.setInt(5, v.getVendeur().getId());

            ps.executeUpdate();
            
            rs = ps.getGeneratedKeys();
            if (rs.next()) { v.setId(rs.getInt(1)); }

        } catch (SQLException e) {
        	e.printStackTrace(); 
        	
        }
        return v;
    }

    
    public List<Vente> findAll() {
        List<Vente> ventes = new ArrayList<>();
        String sql = "SELECT * FROM vente";

        try (Statement stmt = connect.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int idVehicule = rs.getInt("vehicule_id");
                int idClient = rs.getInt("client_id");
                int idVendeur = rs.getInt("vendeur_id");

                Vehicule v = vehiculeDAO.findById(idVehicule);
                Client c = clientDAO.findById(idClient);
                Employe e = EmployeDAO.findById(idVendeur);

                // 3. Petit cast de sécurité : on s'assure que l'employé est bien un Vendeur
                Vendeur vendeur = null;
                if (e instanceof Vendeur) {
                    vendeur = (Vendeur) e;
                }

                // 4. On reconstruit la Vente
                if (v != null && c != null && vendeur != null) {
                    Vente vente = new Vente(
                        rs.getInt("id"),
                        rs.getDate("date_vente"),
                        rs.getDouble("montant_final"),
                        v,
                        c,
                        vendeur
                    );
                    ventes.add(vente);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ventes;
    }

    
    public Vente findById(Integer id) {
        String sql = "SELECT * FROM vente WHERE id = ?";
        Vente vente = null;

        try {
        	PreparedStatement ps = connect.prepareStatement(sql);
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Vehicule v = vehiculeDAO.findById(rs.getInt("vehicule_id"));
                    Client c = clientDAO.findById(rs.getInt("client_id"));
                    Employe e = EmployeDAO.findById(rs.getInt("vendeur_id"));
                    
                    Vendeur vendeur = (e instanceof Vendeur) ? (Vendeur) e : null;

                    if (v != null && c != null && vendeur != null) {
                        vente = new Vente(
                            rs.getInt("id"),
                            rs.getDate("date_vente"),
                            rs.getDouble("montant_final"),
                            v,
                            c,
                            vendeur
                        );
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vente;
    }


    public Vente update(Vente v) {
        String sql = "UPDATE vente SET date_vente=?, montant_final=?, vehicule_id=?, client_id=?, vendeur_id=? WHERE id=?";

        try {
        	PreparedStatement ps = connect.prepareStatement(sql);
            ps.setDate(1, v.getdateVente());
            ps.setDouble(2, v.getMontantFinal());
            
            // On met à jour les clés étrangères
            ps.setInt(3, v.getVehicule().getId());
            ps.setInt(4, v.getClient().getId());
            ps.setInt(5, v.getVendeur().getId());
            
            ps.setInt(6, v.getId()); // Clause WHERE

           ps.executeUpdate() ;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return v;

    }

    @Override
    public void delete(Vente v) {
        String sql = "DELETE FROM vente WHERE id = ?";
        try  {
        	PreparedStatement ps = connect.prepareStatement(sql);
            ps.setInt(1, v.getId());
            ps.executeUpdate();
            System.out.println("Vente supprimée.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void delete(Integer id) {
        String sql = "DELETE FROM vente WHERE id = ?";
        try  {
        	PreparedStatement ps = connect.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}