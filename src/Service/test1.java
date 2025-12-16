package Service;

import Metier.Client;
import Metier.IFacturable;
import Metier.Vendeur;
import Metier.Vente;
import Metier.VoitureThermique;

public class test1 {

	public static void main(String[] args) {
		
		
		VoitureThermique s = new VoitureThermique(55,"Ferra","pal",102559,125,12);
		Client RR = new Client(12,"poc","fss","ttag@ggg.cc","0650835364","ok");
		
		Vendeur Isa = new Vendeur(12,"aodaof","gffd","rgffdf","ogijio",10);


		
		Vente v2 = new Vente(1,null,10000,s,RR,Isa);
		
		

		System.out.println(GarageService.editerFacture(v2));
		
	}

}
