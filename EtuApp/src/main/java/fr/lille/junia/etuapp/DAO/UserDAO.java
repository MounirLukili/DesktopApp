package fr.lille.junia.etuapp.DAO;





import fr.lille.junia.etuapp.Model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    public void create(User user) {
        String query = "INSERT INTO Users (prenom, nom, email, password, role) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, user.getPrenom());
            pstmt.setString(2, user.getNom());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPassword());
            pstmt.setString(5, user.getRole().name());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User read(int id) {
        String query = "SELECT * FROM Users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("prenom"),
                        rs.getString("nom"),
                        rs.getString("email"),
                        rs.getString("password"),
                        User.Role.valueOf(rs.getString("role"))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> readAll() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM Users";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("prenom"),
                        rs.getString("nom"),
                        rs.getString("email"),
                        rs.getString("password"),
                        User.Role.valueOf(rs.getString("role"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void update(User user) {
        String query = "UPDATE Users SET prenom = ?, nom = ?, email = ?, password = ?, role = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, user.getPrenom());
            pstmt.setString(2, user.getNom());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPassword());
            pstmt.setString(5, user.getRole().name());
            pstmt.setInt(6, user.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String query = "DELETE FROM Users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public User findByEmail(String email) {
        String query = "SELECT * FROM Users WHERE email = ?";
        System.out.println("Preparing to find user by email: " + email);

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            System.out.println("Executing query: " + pstmt.toString()); // Print the query for debugging

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("User found in database: " + rs.getString("nom"));

                return new User(
                        rs.getInt("id"),
                        rs.getString("prenom"),
                        rs.getString("nom"),
                        rs.getString("email"),
                        rs.getString("password"),
                        User.Role.valueOf(rs.getString("role"))
                );
            } else {
                System.out.println("No user found with email lololol: " + email);
            }
        } catch (SQLException e) {
            System.out.println("SQL error occurred while finding user by email.");
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("SQL State: " + e.getSQLState());
            e.printStackTrace();
        }
        return null; // Return null if no user found
    }


    public boolean isConnectionValid() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }





}
