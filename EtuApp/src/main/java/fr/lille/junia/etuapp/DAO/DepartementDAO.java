package fr.lille.junia.etuapp.DAO;



import fr.lille.junia.etuapp.DataBaseConnection.DBConnection;
import fr.lille.junia.etuapp.Model.Departement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartementDAO {


    static Connection connection;

    static {
        try {
            connection = DBConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public DepartementDAO(Connection connection) throws SQLException {
        this.connection = connection;
    }

    public void create(Departement departement) {
        String query = "INSERT INTO Departement (nom) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, departement.getNom());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Departement read(int id) {
        String query = "SELECT * FROM Departement WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Departement(
                        rs.getInt("id"),
                        rs.getString("nom")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Departement> readAll() {
        List<Departement> departements = new ArrayList<>();
        String query = "SELECT * FROM Departement";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                departements.add(new Departement(
                        rs.getInt("id"),
                        rs.getString("nom")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departements;
    }

    public void update(Departement departement) {
        String query = "UPDATE Departement SET nom = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, departement.getNom());
            pstmt.setInt(2, departement.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String query = "DELETE FROM Departement WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

