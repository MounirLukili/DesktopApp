package fr.lille.junia.etuapp.Model;


public class Departement {
    private int id;
    private String nom;

    // Constructors
    public Departement() {}

    public Departement(int id, String nom) {
        this.id = id;
        this.nom = nom;
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
}

