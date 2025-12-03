package DAO;

import java.util.List;

import DAO.ClientDAO;
import Metier.Client;
import Metier.Mecanicien;
import Metier.Vendeur;
import Metier.VoitureThermique;
import Service.GarageService;

public class MainTest {
	

	public static void main(String[] args) {
      
	
		
		// --- Création de Mécaniciens ---
		Mecanicien m1 = new Mecanicien(0, "Rossi", "Marco", "Rossi88", "mrossi", "turbo123");
		// Spécialité : Moteurs italiens
		m1.setSpecialite("Sportives Italiennes"); 

		Mecanicien m2 = new Mecanicien(0, "Bernard", "Julie", "BerBer", "jbernard", "clede12");
	
		m2.setSpecialite("Systèmes Hybrides");


		
		Vendeur v1 = new Vendeur(0, "Dubois", "Charles", "DubCharle", "cdubois", 0.0);
		
		v1.setCommissionPct(2.0); 

		Vendeur v2 = new Vendeur(0, "Smith", "Sarah", "Sarah28", "ssmith", 0.0);
		
		v2.setCommissionPct(5.0);


		// --- Test du DAO ---
		EmployeDAO employeDAO = new EmployeDAO();
		employeDAO.create(m2); // Sauvegarde Marco
		employeDAO.create(v2); // Sauvegarde Charles

		System.out.println("Employés ajoutés avec succès !");
	}	
}