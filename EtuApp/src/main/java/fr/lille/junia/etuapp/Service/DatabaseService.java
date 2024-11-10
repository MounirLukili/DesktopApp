package fr.lille.junia.etuapp.Service;

import fr.lille.junia.etuapp.Model.Etudiant;
import fr.lille.junia.etuapp.Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {
    private Connection connection;

    public DatabaseService(Connection connection) {
        this.connection = connection;
    }

    public List<Etudiant> getAllEtudiants() {
        List<Etudiant> etudiants = new ArrayList<>();
        String query = "SELECT * FROM Etudiant";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int userId = rs.getInt("user_id");
                int departementId = rs.getInt("departement_id");
                etudiants.add(new Etudiant(id, userId, departementId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return etudiants;
    }

    public User getUserById(int userId) {
        User user = null;
        String query = "SELECT * FROM User WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String prenom = rs.getString("prenom");
                String nom = rs.getString("nom");
                String email = rs.getString("email");
                String password = rs.getString("password");
                User.Role role = User.Role.valueOf(rs.getString("role"));
                user = new User(userId, prenom, nom, email, password, role);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }
}
