package fr.lille.junia.etuapp.Controller.Admin;

import fr.lille.junia.etuapp.Controller.SessionManager;
import fr.lille.junia.etuapp.DAO.*;
import fr.lille.junia.etuapp.Controller.LoginController;
import fr.lille.junia.etuapp.DataBaseConnection.DBConnection;
import fr.lille.junia.etuapp.Model.*;
import fr.lille.junia.etuapp.Service.BaseController;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminController extends BaseController {

    @FXML
    private TextField etudiantPrenomField;
    @FXML
    private TextField etudiantNomField;
    @FXML
    private TextField etudiantEmailField;
    //@FXML
   // private TextField etudiantDeptField;
    @FXML
    private Button returnButton;
    private NavigationManager navigationManager;



    @FXML
    private TextField professeurNameField;
    @FXML
    private TextField professeurEmailField;
    @FXML
    private TextField professeurSubjectField;
    @FXML
    private ComboBox<Etudiant> etudiantComboBox;
    @FXML
    private TextField matiereIdField;
    @FXML
    private TextField matiereNameField;
    @FXML
    private TextField matiereDescriptionField;
    @FXML
    private ComboBox<String> departmentComboBox;
    private List<Departement> allDepartments = DepartementDAO.readAll();




    private Connection connection;
    private EtudiantDAO etudiantDAO;
    private ProfesseurDAO professeurDAO;
    private MatiereDAO matiereDAO;
    private Stage primaryStage;
    private Etudiant selectedStudent;
    private DatabaseService databaseService;
    private Etudiant selectedEtudiant;
    private DepartementDAO departementDAO;

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void initialize() {
        loadDepartments();
        databaseService = new DatabaseService(connection);
        //loadEtudiants();
       // etudiantComboBox.setOnAction(e -> populateFields(etudiantComboBox.getValue()));
    }

    private void loadEtudiants() {
        List<Etudiant> etudiants = databaseService.getAllEtudiants();
        etudiantComboBox.getItems().addAll(etudiants);
    }

    private void populateFields(Etudiant etudiant) {
        if (etudiant != null) {
            this.selectedEtudiant = etudiant;
            User user = databaseService.getUserById(etudiant.getUserId());
            etudiantPrenomField.setText(user.getPrenom());
            etudiantNomField.setText(user.getNom());
            etudiantEmailField.setText(user.getEmail());
            departmentComboBox.setValue(String.valueOf(etudiant.getDepartementId()));
        }
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


    public AdminController() {
        try {
            connection = DBConnection.getConnection();
            etudiantDAO = new EtudiantDAO(connection);
            professeurDAO = new ProfesseurDAO(connection);
            matiereDAO = new MatiereDAO(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // CRUD operations for Etudiant

    @FXML
    public void addEtudiant(ActionEvent event) {
        String nom = etudiantNomField.getText().trim(); // Corrected variable name
        String prenom = etudiantPrenomField.getText().trim();
        String email = etudiantEmailField.getText().trim();
        String department = departmentComboBox.getValue();

        // Validate input fields
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || department == null) {
            showAlert("Input Error", "Please fill in all fields.");
            return;
        }

        String password = nom + "." + prenom; // Set password as nom.prenom

        // Insert user into users table
        try {
            String insertUserQuery = "INSERT INTO Users (nom, prenom, email, password, role) VALUES (?, ?, ?, ?, 'Etudiant')";
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
                String insertEtudiantQuery = "INSERT INTO Etudiant (user_id, departement_id) VALUES (?, ?)";
                PreparedStatement etudiantStatement = connection.prepareStatement(insertEtudiantQuery);
                etudiantStatement.setInt(1, userId);
                etudiantStatement.setInt(2, getDepartmentId(department)); // You need to implement this method to get the department ID
                etudiantStatement.executeUpdate();

                // Optional: Show success message or perform other actions
                showAlert("Success", "Etudiant added successfully!");
                clearFields();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Helper method to get department ID based on department name
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










    private void loadScene(String fxmlPath) {
        NavigationManager navigationManager = NavigationManager.getInstance();

        if (navigationManager == null) {
            System.err.println("NavigationManager instance is not available! Cannot load scene.");
            return; // Exit the method if NavigationManager is not available
        }

        try {
            System.out.println("Attempting to load FXML at path: " + fxmlPath); // Debug
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Log the controller class loaded
            Object loadedController = loader.getController();
            System.out.println("Loaded controller: " + loadedController.getClass().getName()); // Debug

            Scene scene = new Scene(root, 800, 600); // Set preferred width and height
            navigationManager.navigateTo(scene); // Use NavigationManager to navigate to the new scene
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load the requested page.");
        } catch (ClassCastException e) {
            e.printStackTrace();
            System.out.println("ClassCastException: Mismatch in controller type"); // Debug
            showAlert("Error", "Controller type mismatch.");
        }
    }





    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void handleEtudiantAction(ActionEvent actionEvent) {
        loadScene("/fr/lille/junia/etuapp/view/admin/etudiant_management.fxml");
    }

    public void handleProfesseurAction(ActionEvent actionEvent) {
        loadScene("/fr/lille/junia/etuapp/view/admin/professeur_management.fxml");
    }

    public void handleMatiereAction(ActionEvent actionEvent) {
        loadScene("/fr/lille/junia/etuapp/view/admin/matiere_management.fxml");
    }

    public void handleLogout(ActionEvent actionEvent) {
        SessionManager.clearSession();

        NavigationManager nav = NavigationManager.getInstance();
        nav.logout();
    }


    @FXML
    //private TextField EtudiantEmailField; // Field to input the student's email
    //hjdfjsd

    public void deleteEtudiant(ActionEvent actionEvent) {
        String email = etudiantEmailField.getText().trim();

        // Validate input
        if (email.isEmpty()) {
            showAlert("Input Error", "Please enter the student's email.");
            return;
        }

        // Delete from etudiant and users table
        try {
            // Start transaction
            connection.setAutoCommit(false); // Disable auto-commit for transaction management

            // First, delete from etudiant table
            String deleteEtudiantQuery = "DELETE FROM Etudiant WHERE user_id = (SELECT id FROM Users WHERE email = ?)";
            PreparedStatement deleteEtudiantStatement = connection.prepareStatement(deleteEtudiantQuery);
            deleteEtudiantStatement.setString(1, email);
            int etudiantRowsAffected = deleteEtudiantStatement.executeUpdate();

            // Then, delete from users table
            String deleteUserQuery = "DELETE FROM Users WHERE email = ?";
            PreparedStatement deleteUserStatement = connection.prepareStatement(deleteUserQuery);
            deleteUserStatement.setString(1, email);
            int userRowsAffected = deleteUserStatement.executeUpdate();

            // Commit transaction if both deletions were successful
            if (etudiantRowsAffected > 0 && userRowsAffected > 0) {
                connection.commit(); // Commit the transaction
                showAlert("Success", "Etudiant deleted successfully!");
            } else {
                connection.rollback(); // Rollback if no rows affected
                showAlert("Error", "No student found with the provided email.");
                clearFields();
            }
        } catch (SQLException e) {
            try {
                connection.rollback(); // Rollback on exception
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while trying to delete the student.");
        } finally {
            try {
                connection.setAutoCommit(true); // Re-enable auto-commit
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void modifyEtudiant(ActionEvent actionEvent) {
        if (selectedStudent == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/lille/junia/etuapp/view/admin/SelectStudent.fxml"));
                Parent root = loader.load();

                Stage selectStage = new Stage();
                selectStage.setScene(new Scene(root));
                selectStage.initModality(Modality.APPLICATION_MODAL);
                selectStage.showAndWait();

                EditStudent controller = loader.getController();
                selectedStudent = controller.getSelectedStudent();

                if (selectedStudent != null) {
                    UserDAO userDAO = new UserDAO(connection);
                    User user = userDAO.read(selectedStudent.getUserId());

                    if (user != null) {
                        etudiantNomField.setText(user.getNom());
                        etudiantPrenomField.setText(user.getPrenom());
                        etudiantEmailField.setText(user.getEmail());

                        Departement selectedDepartement = allDepartments.stream()
                                .filter(dept -> dept.getId() == selectedStudent.getDepartementId())
                                .findFirst()
                                .orElse(null);
                        departmentComboBox.setValue(String.valueOf(selectedDepartement));
                    } else {
                        showAlert("Error", "User not found for the selected student.");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            updateStudentInDatabase();
            showAlert("Modification Successful", "Student information has been updated.");
            clearFields();
            selectedStudent = null;
        }
    }

    private void updateStudentInDatabase() {
        String updateQuery = "UPDATE Users SET nom = ?, prenom = ?, email = ? WHERE id = ?";
        String departmentUpdateQuery = "UPDATE Etudiant SET departement_id = ? WHERE user_id = ?";

        try {
            connection.setAutoCommit(false); // Start transaction

            // Update user details
            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                String nom = etudiantNomField.getText().trim();
                String prenom = etudiantPrenomField.getText().trim();
                String email = etudiantEmailField.getText().trim();
                int userId = selectedStudent.getUserId();

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
                int studentId = selectedStudent.getUserId();

                System.out.println("Updating etudiant table:");
                System.out.println("Department ID: " + departmentId);
                System.out.println("User ID: " + studentId);

                deptStatement.setInt(1, departmentId);
                deptStatement.setInt(2, studentId);

                int deptRowsUpdated = deptStatement.executeUpdate();
                System.out.println("Rows updated in etudiant table: " + deptRowsUpdated);
            }

            connection.commit(); // Commit transaction if both updates succeed

        } catch (SQLException e) {
            try {
                connection.rollback(); // Rollback on error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while updating the student.");
        } finally {
            try {
                connection.setAutoCommit(true); // Reset auto-commit mode
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }



    public void searchEtudiant(ActionEvent actionEvent) {
        String nom = etudiantNomField.getText().trim();
        String prenom = etudiantPrenomField.getText().trim();
        String email = etudiantEmailField.getText().trim();
        String departement = departmentComboBox.getValue();

        // Build the SQL query based on provided information
        StringBuilder queryBuilder = new StringBuilder(
                "SELECT u.nom, u.prenom, u.email, d.nom AS departement " +
                        "FROM Users u " +
                        "JOIN Etudiant e ON u.id = e.user_id " +
                        "JOIN Departement d ON e.departement_id = d.id " +
                        "WHERE 1=1"
        );

        List<String> parameters = new ArrayList<>();

        if (!nom.isEmpty()) {
            queryBuilder.append(" AND u.nom LIKE ?");
            parameters.add("%" + nom + "%");
        }
        if (!prenom.isEmpty()) {
            queryBuilder.append(" AND u.prenom LIKE ?");
            parameters.add("%" + prenom + "%");
        }
        if (!email.isEmpty()) {
            queryBuilder.append(" AND u.email LIKE ?");
            parameters.add("%" + email + "%");
        }
        if (departement != null) {
            queryBuilder.append(" AND d.nom = ?");
            parameters.add(departement);
        }

        // Execute the query
        try (PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                statement.setString(i + 1, parameters.get(i));
            }
            ResultSet resultSet = statement.executeQuery();

            // Check if student was found
            if (resultSet.next()) {
                String foundNom = resultSet.getString("nom");
                String foundPrenom = resultSet.getString("prenom");
                String foundEmail = resultSet.getString("email");
                String foundDepartement = resultSet.getString("departement");

                // Show student details in a pop-up
                String details = "Nom: " + foundNom + "\nPrénom: " + foundPrenom +
                        "\nEmail: " + foundEmail + "\nDépartement: " + foundDepartement;
                showAlert("Student Found", details);
            } else {
                // Show message if not found
                showAlert("Not Found", "No student found with the provided information.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while searching for the student.");
        }
    }


    private void clearFields() {
        etudiantPrenomField.clear();
        etudiantNomField.clear();
        etudiantEmailField.clear();
        departmentComboBox.setValue(null); // Reset ComboBox to default
        etudiantComboBox.setValue(null); // Reset ComboBox to default, if needed
    }


    public void handleReturnAction(ActionEvent actionEvent) {
        NavigationManager navigationManager = NavigationManager.getInstance(); // Get the instance of NavigationManager
        navigationManager.goBack();
    }
}
