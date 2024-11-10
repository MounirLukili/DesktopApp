package fr.lille.junia.etuapp.DAO;



import fr.lille.junia.etuapp.DataBaseConnection.DBConnection;
import fr.lille.junia.etuapp.Model.Etudiant;
import fr.lille.junia.etuapp.Model.Professeur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfesseurDAO {
    private Connection connection;

    public ProfesseurDAO(Connection connection) {
        this.connection = connection;
    }

    public void create(Professeur professeur) {
        String query = "INSERT INTO Professeur (user_id, departement_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, professeur.getUserId());
            pstmt.setInt(2, professeur.getDepartementId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Professeur read(int id) {
        String query = "SELECT * FROM Professeur WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Professeur(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("departement_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Professeur> readAll() {
        List<Professeur> professeurs = new ArrayList<>();
        String query = "SELECT * FROM Professeur";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                professeurs.add(new Professeur(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("departement_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return professeurs;
    }

    public void update(Professeur professeur) {
        String query = "UPDATE Professeur SET user_id = ?, departement_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, professeur.getUserId());
            pstmt.setInt(2, professeur.getDepartementId());
            pstmt.setInt(3, professeur.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String query = "DELETE FROM Professeur WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Professeur findprofByUserId(int userId) {
        Professeur professeur = null;
        String query = "SELECT * FROM Professeur WHERE user_id = ?"; // Adjust table name and fields as needed

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                int departementId = rs.getInt("departement_id");
                professeur = new Professeur(id, userId, departementId);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions
        }

        return professeur;
    }
}
