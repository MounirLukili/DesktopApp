package fr.lille.junia.etuapp.DAO;



import fr.lille.junia.etuapp.DataBaseConnection.DBConnection;
import fr.lille.junia.etuapp.Model.Matiere;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfesseurMatiereDAO {
    private Connection connection;

    public ProfesseurMatiereDAO(Connection connection) {
        this.connection = connection;
    }

    public void create(int professeurId, int matiereId) {
        String query = "INSERT INTO Professeur_Matiere (professeur_id, matiere_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, professeurId);
            pstmt.setInt(2, matiereId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int professeurId, int matiereId) {
        String query = "DELETE FROM Professeur_Matiere WHERE professeur_id = ? AND matiere_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, professeurId);
            pstmt.setInt(2, matiereId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Integer> readMatieresByProfesseur(int professeurId) {
        List<Integer> matiereIds = new ArrayList<>();
        String query = "SELECT matiere_id FROM Professeur_Matiere WHERE professeur_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, professeurId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                matiereIds.add(rs.getInt("matiere_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matiereIds;
    }

    public List<Integer> readMatieresByprof(int etudiantId) {
        List<Integer> matiereIds = new ArrayList<>();
        String query = "SELECT matiere_id FROM Professeur_Matiere WHERE professeur_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, etudiantId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                matiereIds.add(rs.getInt("matiere_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matiereIds;
    }



    public boolean matiereExistsForProf(int etudiantId, int matiereId) {
        // Implement the logic to check if the Matiere exists for the given Etudiant
        String query = "SELECT COUNT(*) FROM Professeur_Matiere WHERE professeur_id = ? AND matiere_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, etudiantId);
            statement.setInt(2, matiereId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // Return true if count is greater than 0
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exception appropriately
        }
        return false;
    }


    public List<Matiere> getMatieresByProfId(int etudiantId) {
        List<Matiere> matieres = new ArrayList<>();

        String query = "SELECT m.id, m.nom, m.departement_id " + // Include departement_id in the SELECT statement
                "FROM professeur_matiere em " +
                "JOIN matiere m ON em.matiere_id = m.id " +
                "WHERE em.professeur_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, etudiantId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nom = resultSet.getString("nom");
                int departementId = resultSet.getInt("departement_id"); // Retrieve the departement_id

                // Create the Matiere object with the departementId included
                Matiere matiere = new Matiere(id, nom, departementId);
                matieres.add(matiere);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exception properly in production code
        }

        return matieres;
    }

}

