package fr.lille.junia.etuapp.DAO;



import fr.lille.junia.etuapp.Model.Matiere;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatiereDAO {
    private Connection connection;

    public MatiereDAO(Connection connection) {
        this.connection = connection;
    }

    public void create(Matiere matiere) {
        String query = "INSERT INTO Matiere (nom, departement_id, professeur_id) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, matiere.getNom());
            pstmt.setInt(2, matiere.getDepartementId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Matiere read(int id) {
        String query = "SELECT * FROM Matiere WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Matiere(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getInt("departement_id")

                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Matiere> readAll() {
        List<Matiere> matieres = new ArrayList<>();
        String query = "SELECT * FROM Matiere";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                matieres.add(new Matiere(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getInt("departement_id")

                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matieres;
    }

    public void update(Matiere matiere) {
        String query = "UPDATE Matiere SET nom = ?, departement_id = ?, professeur_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, matiere.getNom());
            pstmt.setInt(2, matiere.getDepartementId());
            pstmt.setInt(4, matiere.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String query = "DELETE FROM Matiere WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Matiere findMatiereById(int id) {
        String query = "SELECT * FROM Matiere WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Matiere(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getInt("departement_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Matiere> findMatieresByDepartementId(int departementId) {
        List<Matiere> matieres = new ArrayList<>();
        String query = "SELECT * FROM Matiere WHERE departement_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, departementId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                matieres.add(new Matiere(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getInt("departement_id")

                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matieres;
    }
}


