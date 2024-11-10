package fr.lille.junia.etuapp.Controller.Admin;

import fr.lille.junia.etuapp.DataBaseConnection.DBConnection;
import fr.lille.junia.etuapp.DAO.EtudiantDAO;
import fr.lille.junia.etuapp.DAO.UserDAO;

import fr.lille.junia.etuapp.Model.Etudiant;
import fr.lille.junia.etuapp.Model.User;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EditStudent {
    @FXML
    private TextField searchField;
    @FXML private ListView<String> studentListView;  // Assuming you have a Student model class

    private Etudiant selectedStudent;
    private EtudiantDAO etudiantDAO;
    private UserDAO userDAO;

    Connection connection=DBConnection.getConnection();

    public EditStudent() throws SQLException {
        Connection connection = DBConnection.getConnection();
        this.etudiantDAO = new EtudiantDAO(connection);
        this.userDAO = new UserDAO(connection);
    }


    @FXML
    public void selectStudent() {
        String selectedItem = studentListView.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            // Assuming the selectedItem format is "ID - Name Surname"
            int studentId = Integer.parseInt(selectedItem.split(" - ")[0]); // Extract the ID

            // Fetch the full Etudiant object using the ID
            selectedStudent = etudiantDAO.read(studentId);

            // Close the pop-up window
            ((Stage) studentListView.getScene().getWindow()).close();
        } else {
            System.out.println("No student selected.");
        }
    }


    public Etudiant getSelectedStudent() {
        return selectedStudent;
    }





    @FXML
    public void searchStudent() {
        String searchText = searchField.getText().trim();
        List<Etudiant> allStudents = etudiantDAO.readAll(); // Fetch all students
        List<Etudiant> filteredStudents = new ArrayList<>();

        // Filter students by their name or email
        for (Etudiant student : allStudents) {
            User user = userDAO.read(student.getUserId()); // Fetch the User for the student
            if (user.getNom().contains(searchText) || user.getPrenom().contains(searchText) || user.getEmail().contains(searchText)) {
                filteredStudents.add(student);
            }
        }

        // Populate the ListView with the filtered students
        studentListView.getItems().clear();
        for (Etudiant student : filteredStudents) {
            User user = userDAO.read(student.getUserId()); // Fetch the User again to get details
            String displayText = student.getId() + " - " + user.getPrenom() + " " + user.getNom();
            studentListView.getItems().add(displayText);
        }
    }



}
