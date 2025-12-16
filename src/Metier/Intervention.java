package Metier;

import java.sql.Date;

public class Intervention implements IFacturable {
	private int id;
    private Date dateDebut;
    private Date dateFin;
    private String description;
    private String statut;
    
    private Vehicule vehicule;
    private Mecanicien mecanicien;
    private double prixMainOeuvre;

    // Constructeur complet (lecture BDD)
    public Intervention(int id, Date dateDebut, Date dateFin, String description, String statut, Vehicule vehicule, Mecanicien mecanicien, double prixMainOeuvre) {
        this.id = id;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.description = description;
        this.statut = statut;
        this.vehicule = vehicule;
        this.mecanicien = mecanicien;
        this.prixMainOeuvre = prixMainOeuvre;
    }

    // Constructeur simplifié (Création)
    public Intervention(Vehicule vehicule, Mecanicien mecanicien, String description, double prixMainOeuvre) {
        this.vehicule = vehicule;
        this.mecanicien = mecanicien;
        this.description = description;
        this.prixMainOeuvre = prixMainOeuvre;
        this.dateDebut = new Date(System.currentTimeMillis());
        this.statut = "EN_COURS";
    }

    // Getters / Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Date getDateDebut() { return dateDebut; }
    public Date getDateFin() { return dateFin; }
    public void setDateFin(Date dateFin) { this.dateFin = dateFin; }
    public String getDescription() { return description; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public Vehicule getVehicule() { return vehicule; }
    public Mecanicien getMecanicien() { return mecanicien; }
    public double getPrixMainOeuvre() { return prixMainOeuvre; }
    
    // --- C'EST ICI QUE TU AVAIS LE PROBLEME (INTERFACE IFACTURABLE) ---

    @Override
    public double getMontantTotal() {
        return this.prixMainOeuvre; // Il faut bien renvoyer le prix !
    }

    @Override
    public String getDescriptionFacture() {
        // Sécurité pour éviter "null"
        String desc = (description != null) ? description : "Intervention standard";
        String immat = (vehicule != null) ? vehicule.getImmatriculation() : "Inconnue";
        return "Atelier : " + desc + " sur " + immat;
    }

    @Override
    public Client getClientFacture() {
        // Comme l'intervention n'a pas de client direct, on renvoie null
        // Le service d'impression affichera "Client de passage", c'est correct.
        return null; 
    }

    @Override
    public String getReference() {
        return "REP-" + this.id;
    }
}