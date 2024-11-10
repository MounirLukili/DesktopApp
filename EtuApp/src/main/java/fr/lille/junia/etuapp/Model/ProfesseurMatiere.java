package fr.lille.junia.etuapp.Model;


public class ProfesseurMatiere {
    private int id;
    private int professeurId;
    private int matiereId;

    // Constructors
    public ProfesseurMatiere() {}

    public ProfesseurMatiere(int id, int professeurId, int matiereId) {
        this.id = id;
        this.professeurId = professeurId;
        this.matiereId = matiereId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProfesseurId() {
        return professeurId;
    }

    public void setProfesseurId(int professeurId) {
        this.professeurId = professeurId;
    }

    public int getMatiereId() {
        return matiereId;
    }

    public void setMatiereId(int matiereId) {
        this.matiereId = matiereId;
    }
}

