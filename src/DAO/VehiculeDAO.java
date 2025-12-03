package DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import Metier.Vehicule;
import Metier.VehiculeElectrique;
import Metier.VoitureThermique;

public class VehiculeDAO extends DAO<Vehicule, Integer> {
	
	private ResultSet rs ;
 

    
    public Vehicule create(Vehicule v) {
        String sql = "INSERT INTO vehicule (type_vehicule, marque, modele, immatriculation, prix_vente, cylindree, emission_co2, batterie_kwh, autonomie_km, statut) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        open();

        try {
        	
        	PreparedStatement stmt = connect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS) ;
        	
        	if (v instanceof VoitureThermique) {
                stmt.setString(1, "THERMIQUE");
            } else if (v instanceof VehiculeElectrique) {
                stmt.setString(1, "ELECTRIQUE");
            }

            stmt.setString(2, v.getMarque());
            stmt.setString(3, v.getModele());
            stmt.setString(4, v.getImmatriculation());
            stmt.setDouble(5, v.getPrixVente());

            if (v instanceof VoitureThermique) {
                VoitureThermique vt = (VoitureThermique) v;
                stmt.setInt(6, vt.getCylindree());
                stmt.setInt(7, vt.getEmissionCo2());
                stmt.setNull(8, Types.INTEGER);
                stmt.setNull(9, Types.INTEGER);
            } else if (v instanceof VehiculeElectrique) {
                VehiculeElectrique ve = (VehiculeElectrique) v;
                stmt.setNull(6, Types.INTEGER);
                stmt.setNull(7, Types.INTEGER);
                stmt.setInt(8, ve.getBatterieKwh());
                stmt.setInt(9, ve.getAutonomie()); 
            }

            stmt.setString(10, "DISPO");

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                v.setId(rs.getInt(1)); 
            }
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return v;
    }
    
    
    

    public List<Vehicule> findAll()  {
    	
        List<Vehicule> liste = new ArrayList<>();
        String sql = "SELECT * FROM vehicule";
        
        open();

        try {
        	
        	PreparedStatement stmt = connect.prepareStatement(sql) ;
        	
        	 rs = stmt.executeQuery();
        	 
        	 
        	 while (rs.next()) {
                 String type = rs.getString("type_vehicule");
                 Vehicule v = null;

                 if ("THERMIQUE".equals(type)) {
                     v = new VoitureThermique(
                         rs.getInt("id"),
                         rs.getString("marque"),
                         rs.getString("modele"),
                         rs.getDouble("prix_vente"),
                         rs.getInt("emission_co2"),
                         rs.getInt("cylindree")
                     );
                 } else if ("ELECTRIQUE".equals(type)) {
                     v = new VehiculeElectrique(
                         rs.getInt("id"),
                         rs.getString("marque"),
                         rs.getString("modele"),
                         rs.getDouble("prix_vente"),
                         rs.getInt("batterie_kwh"),
                         rs.getInt("autonomie_km")
                     );
                     
                 }
                 
                 if (v != null) {
                     
                     liste.add(v);
                 }
             }
         
       
        	
        	
        }catch(SQLException e){
        	e.printStackTrace();
        }


        return liste;
    }
    
    
    
    
    

    
    public Vehicule findById(int id) {
        String sql = "SELECT * FROM vehicule WHERE id = ?";
        Vehicule v = null;
        open();

        try {
        	PreparedStatement ps = connect.prepareStatement(sql);

            ps.setInt(1, id);
            
            rs = ps.executeQuery();
               
            
            if (rs.next()) {
            	
            	
                    String type = rs.getString("type_vehicule");

                    if ("THERMIQUE".equals(type)) {
                        v = new VoitureThermique(
                            rs.getInt("id"),
                            rs.getString("marque"),
                            rs.getString("modele"),
                            rs.getDouble("prix_vente"),
                            rs.getInt("emission_co2"),
                            rs.getInt("cylindree")
                        );
                    } else if ("ELECTRIQUE".equals(type)) {
                        v = new VehiculeElectrique(
                            rs.getInt("id"),
                            rs.getString("marque"),
                            rs.getString("modele"),
                            rs.getDouble("prix_vente"),
                            rs.getInt("batterie_kwh"),
                            rs.getInt("autonomie_km")
                        );
                    }

                    if (v != null) {
                        v.setImmatriculation(rs.getString("immatriculation"));
                    }
                }
            
        } catch (SQLException e) {
            System.err.println("Erreur dans findById : " + e.getMessage());
            e.printStackTrace();
        }

        return v; 
    }
    
    
    

    
    public Vehicule update(Vehicule v) {
        open();

        String sql = "UPDATE vehicule SET marque=?, modele=?, immatriculation=?, prix_vente=?, statut=?, " +
                     "cylindree=?, emission_co2=?, batterie_kwh=?, autonomie_km=? " +
                     "WHERE id=?";

        try {
             PreparedStatement ps = connect.prepareStatement(sql);

            ps.setString(1, v.getMarque());
            ps.setString(2, v.getModele());
            ps.setString(3, v.getImmatriculation());
            ps.setDouble(4, v.getPrixVente());
            ps.setString(5, "DISPO"); 
            if (v instanceof VoitureThermique) {
            	VoitureThermique vt = (VoitureThermique) v;
                ps.setInt(6, vt.getCylindree());     
                ps.setInt(7, vt.getEmissionCo2());   
                ps.setNull(8, Types.INTEGER);        
                ps.setNull(9, Types.INTEGER);        
                
            } else if (v instanceof VehiculeElectrique) {
                VehiculeElectrique ve = (VehiculeElectrique) v;
                ps.setNull(6, Types.INTEGER);       
                ps.setNull(7, Types.INTEGER);       
                ps.setInt(8, ve.getBatterieKwh());  
                ps.setInt(9, ve.getAutonomie());    
            }

            ps.setInt(10, v.getId()); 

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erreur dans update : " + e.getMessage());
            e.printStackTrace();
        }
        
        return v;

    }

  
	
	public void delete(Vehicule toto) {
        open();

		 String sql = "DELETE FROM vehicule WHERE id = ?";
		 
		 try {
			 PreparedStatement stmt = connect.prepareStatement(sql);
			 
			 stmt.setInt(1, toto.getId());
			 stmt.executeUpdate() ;
		 }catch(SQLException e) {
			 e.printStackTrace();
		 }
	}
}











