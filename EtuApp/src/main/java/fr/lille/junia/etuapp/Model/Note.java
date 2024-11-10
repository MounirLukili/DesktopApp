package fr.lille.junia.etuapp.Model;



public class Note {
    private int id;
    private int valeur;
    private int etudiantId;
    private int matiereId;

    // Constructors
    public Note() {}

    public Note(int id, int valeur, int etudiantId, int matiereId) {
        this.id = id;
        this.valeur = valeur;
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

    public int getValeur() {
        return valeur;
    }

    public void setValeur(int valeur) {
        this.valeur = valeur;
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


    @Override
    public String toString() {
        return "Valeur: " + valeur + ", Etudiant ID: " + etudiantId + ", Matiere ID: " + matiereId;
    }
}

