package Service;

import java.util.List;
import java.util.stream.Collectors;

import DAO.ClientDAO;
import DAO.VehiculeDAO;
import Metier.Client;
import Metier.Vehicule;
import Metier.VehiculeElectrique;

public class GarageService {
    
    private VehiculeDAO vehiculeDAO = new VehiculeDAO();
    private ClientDAO clientDAO = new ClientDAO();

    public void ajouterVehicule(Vehicule v) {
        vehiculeDAO.create(v);
    }

    public List<Vehicule> listerToutLeGarage() {
        return vehiculeDAO.findAll();
    }
    
    public void ajouterClient(Client c) {
        clientDAO.create(c);
    }
    
    public List<Client> listerClients() {
        return clientDAO.findAll();
    }

    public boolean vendreVehicule(int idVehicule, int idClient) {
        Vehicule v = vehiculeDAO.findById(idVehicule);
        Client c = clientDAO.findById(idClient);
        
        if (v != null && c != null && "DISPO".equals(v.getStatut())) {
            
            v.setStatut("VENDU"); 
            
            vehiculeDAO.update(v);
            
            System.out.println("SUCCES : La " + v.getMarque() + " a été vendue à " + c.getNom());
            return true;
        } else {
            System.err.println("ECHEC : Véhicule introuvable, déjà vendu ou Client inconnu.");
            return false;
        }
    }

    public List<Vehicule> listerVehiculesElectriques() {
        return vehiculeDAO.findAll().stream()
                .filter(v -> v instanceof VehiculeElectrique)
                .collect(Collectors.toList());
    }
    
    public List<Vehicule> listerVehiculesThermique() {
        return vehiculeDAO.findAll().stream()
                .filter(v -> v instanceof VoitureThermique)
                .collect(Collectors.toList());
    }
}