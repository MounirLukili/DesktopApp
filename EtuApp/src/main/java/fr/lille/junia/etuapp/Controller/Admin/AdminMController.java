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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.List;

public class AdminMController {



    @FXML
    private TextField MatiereNomField;


    @FXML
    private ComboBox<String> departmentComboBox;

    private MatiereDAO matiereDAO;
    private Connection connection;
    private Stage primaryStage;
    private List<Departement> allDepartments = DepartementDAO.readAll();

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

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

    public AdminMController() {
        try {
            connection = DBConnection.getConnection();
            matiereDAO = new MatiereDAO(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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




    @FXML
    public void addMatiere(ActionEvent event) {
        String nom = MatiereNomField.getText().trim();
        String department = departmentComboBox.getValue();

        // Validate input fields
        if (nom.isEmpty() || department == null) {
            showAlert("Input Error", "Please fill in all fields.");
            return;
        }

        int departmentId = getDepartmentId(department);
        if (departmentId == -1) {
            showAlert("Error", "Selected department not found.");
            return;
        }

        // Insert matiere into matiere table
        try {
            String insertMatiereQuery = "INSERT INTO Matiere (nom, departement_id) VALUES (?, ?)";
            PreparedStatement matiereStatement = connection.prepareStatement(insertMatiereQuery, Statement.RETURN_GENERATED_KEYS);
            matiereStatement.setString(1, nom);
            matiereStatement.setInt(2, departmentId);
            matiereStatement.executeUpdate();

            showAlert("Success", "Matière added successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void deleteMatiere(ActionEvent actionEvent) {
        // Create a new stage for the popup window
        Stage deleteStage = new Stage();
        deleteStage.initModality(Modality.APPLICATION_MODAL);
        deleteStage.setTitle("Delete Matiere");

        // Layout for the popup window
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));

        // ComboBox to list all existing matieres
        ComboBox<String> matiereComboBox = new ComboBox<>();
        loadMatiereOptions(matiereComboBox);  // Load options from the database

        // Delete button
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            String selectedMatiere = matiereComboBox.getValue();
            if (selectedMatiere != null) {
                deleteMatiereFromDatabase(selectedMatiere);
                showAlert("Success", "Matiere deleted successfully.");
                deleteStage.close();
            } else {
                showAlert("Error", "Please select a matiere to delete.");
            }
        });

        vbox.getChildren().addAll(new Label("Select Matiere to Delete:"), matiereComboBox, deleteButton);

        Scene scene = new Scene(vbox, 300, 200);
        deleteStage.setScene(scene);
        deleteStage.showAndWait();
    }

    private void loadMatiereOptions(ComboBox<String> matiereComboBox) {
        try {
            ObservableList<String> matieres = FXCollections.observableArrayList();
            String query = "SELECT nom FROM Matiere";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                matieres.add(resultSet.getString("nom"));
            }
            matiereComboBox.setItems(matieres);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteMatiereFromDatabase(String matiereName) {
        try {
            String query = "DELETE FROM Matiere WHERE nom = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, matiereName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void handleReturnAction(ActionEvent actionEvent) {
        NavigationManager navigationManager = NavigationManager.getInstance(); // Get the instance of NavigationManager
        navigationManager.goBack();
    }

    private void clearFields() {
        MatiereNomField.clear();
        departmentComboBox.setValue(null); // Reset ComboBox to default

    }
}
