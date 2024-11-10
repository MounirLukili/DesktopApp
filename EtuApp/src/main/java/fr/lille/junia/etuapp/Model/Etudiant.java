package fr.lille.junia.etuapp.Model;



public class Etudiant {
    private int id;
    private int userId;
    private int departementId;

    // Constructors
    public Etudiant() {}

    public Etudiant(int id, int userId, int departementId) {
        this.id = id;
        this.userId = userId;
        this.departementId = departementId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getDepartementId() {
        return departementId;
    }

    public void setDepartementId(int departementId) {
        this.departementId = departementId;
    }
}

