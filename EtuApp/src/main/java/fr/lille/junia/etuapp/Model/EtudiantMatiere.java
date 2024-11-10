package fr.lille.junia.etuapp.Model;



public class EtudiantMatiere {
    private int id;
    private int etudiantId;
    private int matiereId;

    // Constructors
    public EtudiantMatiere() {}

    public EtudiantMatiere(int id, int etudiantId, int matiereId) {
        this.id = id;
        this.etudiantId = etudiantId;
        this.matiereId = matiereId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEtudiantId() {
        return etudiantId;
    }

    public void setEtudiantId(int etudiantId) {
        this.etudiantId = etudiantId;
    }

    public int getMatiereId() {
        return matiereId;
    }

    public void setMatiereId(int matiereId) {
        this.matiereId = matiereId;
    }
}

