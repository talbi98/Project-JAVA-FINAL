package DAO;

import java.util.List;

import DAO.ClientDAO;
import Metier.Client;
import Metier.Intervention;
import Metier.Mecanicien;
import Metier.Vendeur;
import Metier.Vente;
import Metier.VoitureThermique;
import Service.GarageService;

public class MainTest {
	
      
		public static void main(String[] args) {
	        System.out.println("=================================================");
	        System.out.println("   MONACO LUXURY GARAGE - TEST FINAL (V 3.1)     ");
	        System.out.println("=================================================\n");

	        GarageService service = new GarageService();

	        try {
	            // 1. SETUP
	            Vendeur vendeur = new Vendeur(0, "Belfort", "Jordan", "wolf", "money", 15.0);
	            service.embaucherEmploye(vendeur);
	            Mecanicien mecano = new Mecanicien(0, "Da Vinci", "Leonardo", "leo", "art", "MOTEUR");
	            service.embaucherEmploye(mecano);
	            
	            VoitureThermique ferrari = new VoitureThermique(0, "Ferrari", "488 Pista", 280000, 320, 5000);
	            ferrari.setImmatriculation("MC-LUXE-" + (int)(System.currentTimeMillis())); 
	            service.ajouterVehicule(ferrari);
	            
	            Client client = new Client("Stark", "Tony", "ironman@avengers.com", "0606060606");
	            service.inscrireClient(client);

	            // 2. VENTE LEGITIME
	            System.out.println("\n>>> 2. TRANSACTION VENTE...");
	            service.realiserVente(ferrari.getId(), client.getId(), vendeur.getId());
	            
	            // On affiche la facture Vente
	            Vente vObj = new Vente(ferrari, client, vendeur); 
	            vObj.setId(1); 
	            System.out.println(service.editerFacture(vObj));


	            // 3. TEST SECURITE (JUSTE APRES LA VENTE)
	            // La voiture est VENDU. Si on essaie de la revendre, ça DOIT planter.
	            System.out.println("\n>>> 3. TEST DE SECURITE (Fraude)...");
	            try {
	                service.realiserVente(ferrari.getId(), client.getId(), vendeur.getId());
	                System.err.println("❌ ECHEC : La revente aurait dû être interdite !");
	            } catch (Exception e) {
	                System.out.println("✅ SUCCES : Revente bloquée (" + e.getMessage() + ")");
	            }


	            // 4. ATELIER
	            System.out.println("\n>>> 4. PASSAGE A L'ATELIER...");
	            Intervention i = service.planifierIntervention(ferrari.getId(), mecano.getId(), "Révision V8");
	            
	            if (i != null) {
	                 System.out.println(service.editerFacture(i));
	                 service.terminerIntervention(i.getId());
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        System.out.println("\n>>> FIN DU PROGRAMME");
	    
		
		
		
	}
	}