package fr.lille.junia.etuapp.Model;



public class Matiere {
    private int id;
    private String nom;
    private int departementId;

    // Constructors
    public Matiere() {}

    public Matiere(int id, String nom, int departementId) {
        this.id = id;
        this.nom = nom;
        this.departementId = departementId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getDepartementId() {
        return departementId;
    }

    public void setDepartementId(int departementId) {
        this.departementId = departementId;
    }

    @Override
    public String toString() {
        return nom; // This helps when printing the list, if needed.
    }
}

