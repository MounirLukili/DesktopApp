package fr.lille.junia.etuapp.DAO;



import fr.lille.junia.etuapp.DataBaseConnection.DBConnection;
import fr.lille.junia.etuapp.Model.Etudiant;
import fr.lille.junia.etuapp.Model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EtudiantDAO {
    private Connection connection;

    public EtudiantDAO(Connection connection) {
        this.connection = connection;
    }

    public void create(Etudiant etudiant) {
        String query = "INSERT INTO Etudiant (user_id, departement_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, etudiant.getUserId());
            pstmt.setInt(2, etudiant.getDepartementId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Etudiant read(int id) {
        String query = "SELECT * FROM Etudiant WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Etudiant(
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

    public List<Etudiant> readAll() {
        List<Etudiant> etudiants = new ArrayList<>();
        String query = "SELECT * FROM Etudiant";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                etudiants.add(new Etudiant(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("departement_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return etudiants;
    }

    public void update(Etudiant etudiant) {
        String query = "UPDATE Etudiant SET user_id = ?, departement_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, etudiant.getUserId());
            pstmt.setInt(2, etudiant.getDepartementId());
            pstmt.setInt(3, etudiant.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String query = "DELETE FROM Etudiant WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Etudiant findEtudiantByUserId(int userId) {
        Etudiant etudiant = null;
        String query = "SELECT * FROM Etudiant WHERE user_id = ?"; // Adjust table name and fields as needed

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                int departementId = rs.getInt("departement_id");
                etudiant = new Etudiant(id, userId, departementId);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions
        }

        return etudiant;
    }


    public User getUserById(int userId) {
        String query = "SELECT id, prenom, nom, email, password, role FROM Users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String prenom = rs.getString("prenom");
                String nom = rs.getString("nom");
                String email = rs.getString("email");
                String password = rs.getString("password");
                User.Role role = User.Role.valueOf(rs.getString("role"));
                return new User(id, prenom, nom, email, password, role);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle or log the exception as needed
        }
        return null; // Return null if no user found
    }


    public List<Etudiant> getEtudiantsByDepartement(int departementId) {
        List<Etudiant> etudiants = new ArrayList<>();
        String query = "SELECT id, user_id, departement_id FROM Etudiant WHERE departement_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, departementId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                int userId = rs.getInt("user_id");
                int deptId = rs.getInt("departement_id");
                etudiants.add(new Etudiant(id, userId, deptId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle or log the exception as needed
        }

        return etudiants;
    }

    public List<Etudiant> findEtudiantsByName(String name) {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT e.id AS etudiantId, e.user_id, e.departement_id, u.nom, u.prenom " +
                "FROM Etudiant e " +
                "JOIN Users u ON e.user_id = u.id " +
                "WHERE u.nom LIKE ? OR u.prenom LIKE ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + name + "%");
            stmt.setString(2, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Etudiant etudiant = new Etudiant();
                etudiant.setId(rs.getInt("etudiantId"));
                etudiant.setUserId(rs.getInt("user_id"));
                etudiant.setDepartementId(rs.getInt("departement_id"));

                etudiants.add(etudiant);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return etudiants;
    }
}


