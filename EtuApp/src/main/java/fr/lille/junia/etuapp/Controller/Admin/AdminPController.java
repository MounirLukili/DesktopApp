package fr.lille.junia.etuapp.Controller.Admin;

import fr.lille.junia.etuapp.DAO.*;
import fr.lille.junia.etuapp.DataBaseConnection.DBConnection;
import fr.lille.junia.etuapp.Model.Departement;
import fr.lille.junia.etuapp.Model.Professeur;
import fr.lille.junia.etuapp.Model.User;
import fr.lille.junia.etuapp.Service.DatabaseService;
import fr.lille.junia.etuapp.Service.NavigationManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.List;

public class AdminPController {
    @FXML
    private TextField professeurIdField;


    @FXML
    private TextField professeurNomField;
    @FXML
    private TextField professeurPrenomField;


    @FXML
    private TextField professeurEmailField;


    @FXML
    private ComboBox<String> departmentComboBox;

    private ProfesseurDAO professeurDAO;
    private Connection connection;
    private EtudiantDAO etudiantDAO;
    private MatiereDAO matiereDAO;

    private Professeur selectedProf;
    private List<Departement> allDepartments = DepartementDAO.readAll();



    public void initialize() {
        loadDepartments();
        DatabaseService databaseService = new DatabaseService(connection);
        //loadEtudiants();
        // etudiantComboBox.setOnAction(e -> populateFields(etudiantComboBox.getValue()));
    }

    private void loadDepartments() {
        ObservableList<String> departments = FXCollections.observableArrayList();
        try {
            // Ensure departmentComboBox is not null
            if (departmentComboBox == null) {
                System.out.println("departmentComboBox is not initialized.");
                return;
            }

            String query = "SELECT nom FROM Departement";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                departments.add(resultSet.getString("nom"));
            }
            departmentComboBox.setItems(departments);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public AdminPController() {
        try {
            connection = DBConnection.getConnection();
            etudiantDAO = new EtudiantDAO(connection);
            professeurDAO = new ProfesseurDAO(connection);
            matiereDAO = new MatiereDAO(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    @FXML
    public void addProfesseur(ActionEvent event) {
        String nom = professeurNomField.getText().trim(); // Corrected variable name
        String prenom = professeurPrenomField.getText().trim();
        String email = professeurEmailField.getText().trim();
        String department = departmentComboBox.getValue();

        // Validate input fields
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || department == null) {
            showAlert("Input Error", "Please fill in all fields.");
            return;
        }

        String password = nom + "." + prenom; // Set password as nom.prenom

        // Insert user into users table
        try {
            String insertUserQuery = "INSERT INTO Users (nom, prenom, email, password, role) VALUES (?, ?, ?, ?, 'Professeur')";
            PreparedStatement userStatement = connection.prepareStatement(insertUserQuery, Statement.RETURN_GENERATED_KEYS);
            userStatement.setString(1, nom);
            userStatement.setString(2, prenom);
            userStatement.setString(3, email);
            userStatement.setString(4, password);
            userStatement.executeUpdate();

            // Get the generated user ID
            ResultSet generatedKeys = userStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1); // Get the generated user ID

                // Insert into etudiant table
                String insertEtudiantQuery = "INSERT INTO Professeur (user_id, departement_id) VALUES (?, ?)";
                PreparedStatement etudiantStatement = connection.prepareStatement(insertEtudiantQuery);
                etudiantStatement.setInt(1, userId);
                etudiantStatement.setInt(2, getDepartmentId(department)); // You need to implement this method to get the department ID
                etudiantStatement.executeUpdate();

                // Optional: Show success message or perform other actions
                showAlert("Success", "Professeur added successfully!");
                clearFields();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void deleteProfesseur(ActionEvent actionEvent) {
        String email = professeurEmailField.getText().trim();

        // Validate input
        if (email.isEmpty()) {
            showAlert("Input Error", "Please enter the prof's email.");
            return;
        }

        // Delete from etudiant and users table
        try {
            // Start transaction
            connection.setAutoCommit(false); // Disable auto-commit for transaction management

            // First, delete from etudiant table
            String deleteProfesseurQuery = "DELETE FROM Professeur WHERE user_id = (SELECT id FROM Users WHERE email = ?)";
            PreparedStatement deleteProfesseurStatement = connection.prepareStatement(deleteProfesseurQuery);
            deleteProfesseurStatement.setString(1, email);
            int professeurRowsAffected = deleteProfesseurStatement.executeUpdate();

            // Then, delete from users table
            String deleteUserQuery = "DELETE FROM Users WHERE email = ?";
            PreparedStatement deleteUserStatement = connection.prepareStatement(deleteUserQuery);
            deleteUserStatement.setString(1, email);
            int userRowsAffected = deleteUserStatement.executeUpdate();

            // Commit transaction if both deletions were successful
            if (professeurRowsAffected > 0 && userRowsAffected > 0) {
                connection.commit(); // Commit the transaction
                showAlert("Success", "Professeur deleted successfully!");
            } else {
                connection.rollback(); // Rollback if no rows affected
                showAlert("Error", "No Professeur found with the provided email.");
            }
        } catch (SQLException e) {
            try {
                connection.rollback(); // Rollback on exception
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while trying to delete the prof.");
        } finally {
            try {
                connection.setAutoCommit(true); // Re-enable auto-commit
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }





    @FXML
    public void modifyProf(ActionEvent actionEvent) {
        if (selectedProf == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/lille/junia/etuapp/view/admin/SelectProf.fxml"));
                Parent root = loader.load();

                Stage selectStage = new Stage();
                selectStage.setScene(new Scene(root));
                selectStage.initModality(Modality.APPLICATION_MODAL);
                selectStage.showAndWait();

                EditProf controller = loader.getController();
                selectedProf = controller.getSelectedProf();

                if (selectedProf != null) {
                    UserDAO userDAO = new UserDAO(connection);
                    User user = userDAO.read(selectedProf.getUserId());

                    if (user != null) {
                        professeurNomField.setText(user.getNom());
                        professeurPrenomField.setText(user.getPrenom());
                        professeurEmailField.setText(user.getEmail());

                        Departement selectedDepartement = allDepartments.stream()
                                .filter(dept -> dept.getId() == selectedProf.getDepartementId())
                                .findFirst()
                                .orElse(null);
                        departmentComboBox.setValue(String.valueOf(selectedDepartement));
                    } else {
                        showAlert("Error", "User not found for the selected prof.");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            updateStudentInDatabase();
            showAlert("Modification Successful", "Prof information has been updated.");
            selectedProf = null;
            clearFields();
        }
    }

    private void updateStudentInDatabase() {
        String updateQuery = "UPDATE Users SET nom = ?, prenom = ?, email = ? WHERE id = ?";
        String departmentUpdateQuery = "UPDATE Professeur SET departement_id = ? WHERE user_id = ?";

        try {
            connection.setAutoCommit(false); // Start transaction

            // Update user details
            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                String nom = professeurNomField.getText().trim();
                String prenom = professeurPrenomField.getText().trim();
                String email = professeurEmailField.getText().trim();
                int userId = selectedProf.getUserId();

                System.out.println("Updating users table:");
                System.out.println("Nom: " + nom);
                System.out.println("Prenom: " + prenom);
                System.out.println("Email: " + email);
                System.out.println("User ID: " + userId);

                statement.setString(1, nom);
                statement.setString(2, prenom);
                statement.setString(3, email);
                statement.setInt(4, userId);

                int rowsUpdated = statement.executeUpdate();
                System.out.println("Rows updated in users table: " + rowsUpdated);
            }

            // Update department
            try (PreparedStatement deptStatement = connection.prepareStatement(departmentUpdateQuery)) {
                int departmentId = getDepartmentId(departmentComboBox.getValue());
                int studentId = selectedProf.getUserId();

                System.out.println("Updating etudiant table:");
                System.out.println("Department ID: " + departmentId);
                System.out.println("User ID: " + studentId);

                deptStatement.setInt(1, departmentId);
                deptStatement.setInt(2, studentId);

                int deptRowsUpdated = deptStatement.executeUpdate();
                System.out.println("Rows updated in prof table: " + deptRowsUpdated);
            }

            connection.commit(); // Commit transaction if both updates succeed

        } catch (SQLException e) {
            try {
                connection.rollback(); // Rollback on error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while updating the prof.");
        } finally {
            try {
                connection.setAutoCommit(true); // Reset auto-commit mode
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void clearFields() {
        professeurPrenomField.clear();
        professeurNomField.clear();
        professeurEmailField.clear();
        departmentComboBox.setValue(null); // Reset ComboBox to default
        departmentComboBox.setValue(null); // Reset ComboBox to default, if needed
    }




    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private int getDepartmentId(String departmentName) {
        int departmentId = -1; // Default value in case not found
        try {
            String query = "SELECT id FROM Departement WHERE nom = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, departmentName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                departmentId = resultSet.getInt("id"); // Assuming "id" is the primary key of the departement table
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departmentId;
    }

    public void handleReturnAction(ActionEvent actionEvent) {
        NavigationManager navigationManager = NavigationManager.getInstance(); // Get the instance of NavigationManager
        navigationManager.goBack();
    }
}
