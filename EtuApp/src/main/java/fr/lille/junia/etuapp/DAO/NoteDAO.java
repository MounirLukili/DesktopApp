package fr.lille.junia.etuapp.DAO;



import fr.lille.junia.etuapp.Model.Note;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoteDAO {
    private Connection connection;

    public NoteDAO(Connection connection) {
        this.connection = connection;
    }

    public void create(Note note) {
        String query = "INSERT INTO Note (valeur, etudiant_id, matiere_id) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, note.getValeur());
            pstmt.setInt(2, note.getEtudiantId());
            pstmt.setInt(3, note.getMatiereId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Note read(int id) {
        String query = "SELECT * FROM Note WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Note(
                        rs.getInt("id"),
                        rs.getInt("valeur"),
                        rs.getInt("etudiant_id"),
                        rs.getInt("matiere_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Note> readAll() {
        List<Note> notes = new ArrayList<>();
        String query = "SELECT * FROM Note";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                notes.add(new Note(
                        rs.getInt("id"),
                        rs.getInt("valeur"),
                        rs.getInt("etudiant_id"),
                        rs.getInt("matiere_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notes;
    }

    public void update(Note note) {
        String query = "UPDATE Note SET valeur = ?, etudiant_id = ?, matiere_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, note.getValeur());
            pstmt.setInt(2, note.getEtudiantId());
            pstmt.setInt(3, note.getMatiereId());
            pstmt.setInt(4, note.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String query = "DELETE FROM Note WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<Note> getMarksByMatiereId(int matiereId) {
        List<Note> notes = new ArrayList<>();
        String query = "SELECT * FROM Note WHERE matiere_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, matiereId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                notes.add(new Note(
                        rs.getInt("id"),
                        rs.getInt("valeur"),
                        rs.getInt("etudiant_id"),
                        rs.getInt("matiere_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notes;
    }


    public void addGrade(int etudiantId, int matiereId, double valeur) {
        String query = "INSERT INTO Note (etudiant_id, matiere_id, valeur) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, etudiantId);
            stmt.setInt(2, matiereId);
            stmt.setDouble(3, valeur);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // You may also want to handle this exception by showing an alert or logging it
        }
    }

    public List<Note> getMarksByStudentId(int etudiantId) {
        List<Note> notes = new ArrayList<>();
        String query = "SELECT * FROM Note WHERE etudiant_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, etudiantId); // Set the student ID
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                notes.add(new Note(
                        rs.getInt("id"),
                        rs.getInt("valeur"),
                        rs.getInt("etudiant_id"),
                        rs.getInt("matiere_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notes;
    }


    public List<Note> getMarksByStudentAndMatiereId(int studentId, int matiereId) {
        List<Note> notes = new ArrayList<>();
        String query = "SELECT * FROM Note WHERE etudiant_id = ? AND matiere_id = ?"; // Filter by student and matiere

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, studentId); // Set student ID
            pstmt.setInt(2, matiereId); // Set matiere ID

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                notes.add(new Note(
                        rs.getInt("id"),
                        rs.getInt("valeur"),
                        rs.getInt("etudiant_id"),
                        rs.getInt("matiere_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notes;
    }

}
