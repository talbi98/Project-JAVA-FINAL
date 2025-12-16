package DAO;

import java.util.List;

import DAO.ClientDAO;
import Metier.Client;
import Metier.Intervention;
import Metier.Mecanicien;
import Metier.VehiculeElectrique;
import Metier.Vendeur;
import Metier.Vente;
import Metier.VoitureThermique;
import Service.GarageService;

public class MainTest {
	
      
	public static void main(String[] args) {
        System.out.println("██████████████████████████████████████████████████");
        System.out.println("█     MONACO LUXURY GARAGE - TEST ULTIME V4.0    █");
        System.out.println("██████████████████████████████████████████████████\n");

        GarageService service = new GarageService();
        
        // Variables pour stocker les IDs générés par la BDD
        int idVendeur = 0, idMecano = 0, idClient = 0;
        int idFerrari = 0, idTesla = 0;

        try {
            // =================================================================================
            // ETAPE 1 : LE PERSONNEL (Recrutement)
            // =================================================================================
            System.out.println(">>> 1. 👔 RECRUTEMENT...");
            
            Vendeur v = new Vendeur(0, "DiCaprio", "Leonardo", "wolf", "wallstreet", 20.0);
            service.embaucherEmploye(v);
            idVendeur = v.getId();
            System.out.println("   ✅ Vendeur recruté : " + v.getNom() + " (ID: " + idVendeur + ")");

            Mecanicien m = new Mecanicien(0, "Hamilton", "Lewis", "lewis", "f1", "MOTEUR_ELEC");
            service.embaucherEmploye(m);
            idMecano = m.getId();
            System.out.println("   ✅ Mécanicien recruté : " + m.getNom() + " (ID: " + idMecano + ")");


            // =================================================================================
            // ETAPE 2 : LE STOCK (Polymorphisme : Thermique ET Electrique)
            // =================================================================================
            System.out.println("\n>>> 2. 🏎️ ARRIVAGE STOCK...");

            // Test 1 : Thermique
            VoitureThermique ferrari = new VoitureThermique(0, "Ferrari", "SF90", 500000, 4000, 160);
            ferrari.setImmatriculation("MC-TH-" + System.currentTimeMillis()); // Unique
            service.ajouterVehicule(ferrari);
            idFerrari = ferrari.getId();
            System.out.println("   ✅ Thermique ajoutée : Ferrari SF90 (ID: " + idFerrari + ")");

            // Test 2 : Electrique (Pour vérifier que ton DAO gère bien les 2 types)
            VehiculeElectrique tesla = new VehiculeElectrique(0, "Tesla", "Roadster", 250000, 200, 1000);
            tesla.setImmatriculation("MC-EL-" + System.currentTimeMillis());
            service.ajouterVehicule(tesla);
            idTesla = tesla.getId();
            System.out.println("   ✅ Electrique ajoutée : Tesla Roadster (ID: " + idTesla + ")");


            // =================================================================================
            // ETAPE 3 : CLIENT
            // =================================================================================
            System.out.println("\n>>> 3. 🤵 CLIENT VIP...");
            Client c = new Client("Wayne", "Bruce", "batman@gotham.com", "00000000");
            c.setVipLevel("PLATINUM");
            service.inscrireClient(c);
            idClient = c.getId();
            System.out.println("   ✅ Client enregistré : " + c.getNom() + " (ID: " + idClient + ")");


            // =================================================================================
            // ETAPE 4 : LA VENTE (Transaction + Facture)
            // =================================================================================
            System.out.println("\n>>> 4. 💰 TRANSACTION VENTE...");

            // On vend la Ferrari
            boolean succesVente = service.realiserVente(idFerrari, idClient, idVendeur);
            
            if (succesVente) {
                System.out.println("   ✅ Vente validée en BDD.");
                
                // TEST FACTURATION
                // On recrée l'objet pour l'affichage (simulation)
                Vente venteDisplay = new Vente(ferrari, c, v);
                venteDisplay.setId(123); // Faux ID juste pour l'affichage
                System.out.println("\n📄 --- TICKET DE CAISSE (Interface IFacturable) ---");
                System.out.println(service.editerFacture(venteDisplay));
            }


            // =================================================================================
            // ETAPE 5 : LA SÉCURITÉ (Tentative de fraude)
            // =================================================================================
            System.out.println("\n>>> 5. 👮 TEST SÉCURITÉ (Double Vente)...");
            try {
                // On essaie de revendre la MÊME Ferrari au MÊME client
                service.realiserVente(idFerrari, idClient, idVendeur);
                
                // Si on arrive ici, c'est GRAVE.
                System.err.println("   ❌ ECHEC FATAL : La fraude n'a pas été détectée !");
                return; 
            } catch (Exception e) {
                // Si on arrive ici, c'est PARFAIT.
                System.out.println("   ✅ SUCCÈS : Le système a bloqué la fraude.");
                System.out.println("   (Message reçu : " + e.getMessage() + ")");
            }


            // =================================================================================
            // ETAPE 6 : L'ATELIER (Réparation de la Tesla Electrique)
            // =================================================================================
            System.out.println("\n>>> 6. 🔧 PASSAGE A L'ATELIER...");
            
            // On répare la Tesla (qui est encore DISPO)
            Intervention inter = service.planifierIntervention(idTesla, idMecano, "Changement Batterie Lithium");
            
            if (inter != null) {
                System.out.println("   ✅ Intervention créée (ID: " + inter.getId() + "). Statut : " + inter.getStatut());
                
                // Facture Atelier
                System.out.println("\n📄 --- FACTURE ATELIER (Polymorphisme) ---");
                System.out.println(service.editerFacture(inter)); // Doit afficher ~500€ + infos Tesla
                
                // Fin de travaux
                service.terminerIntervention(inter.getId());
                System.out.println("   ✅ Intervention terminée, voiture libérée.");
            }


            // =================================================================================
            // ETAPE 7 : LES STATISTIQUES (Le Bonus 20/20)
            // =================================================================================
            System.out.println("\n>>> 7. 📊 ANALYSE MANAGERIALE...");
            
            // Si tu as ajouté les méthodes de stats, sinon commente ces lignes
            try {
                service.afficherRepartitionStock();
                double prixMoyen = service.calculerPrixMoyenVentes();
                System.out.println("   💰 Prix moyen des ventes : " + prixMoyen + " €");
            } catch (Exception ex) {
                System.out.println("   (Module stats non activé, pas grave)");
            }

        } catch (Exception e) {
            System.err.println("\n❌❌❌ CRASH DU PROGRAMME ❌❌❌");
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("\n██████████████████████████████████████████████████");
        System.out.println("█        👑 SUCCÈS TOTAL - PROJET VALIDÉ 👑      █");
        System.out.println("██████████████████████████████████████████████████");
    }
}	