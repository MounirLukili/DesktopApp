package fr.lille.junia.etuapp.Controller.Etudiant;

import fr.lille.junia.etuapp.Controller.LoginController;
import fr.lille.junia.etuapp.Controller.SessionManager;
import fr.lille.junia.etuapp.DAO.DepartementDAO;
import fr.lille.junia.etuapp.DAO.EtudiantDAO;
import fr.lille.junia.etuapp.DAO.MatiereDAO;
import fr.lille.junia.etuapp.DAO.ProfesseurDAO;
import fr.lille.junia.etuapp.DataBaseConnection.DBConnection;
import fr.lille.junia.etuapp.Service.BaseController;
import fr.lille.junia.etuapp.Service.DatabaseService;
import fr.lille.junia.etuapp.Service.NavigationManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.Modality;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class EtudiantController extends BaseController {

    private DatabaseService databaseService;
    private Connection connection;
    private Stage primaryStage;

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void initialize() {
        databaseService = new DatabaseService(connection);
        setPrimaryStage(primaryStage);
        // Initialize any necessary data or UI components here
    }

    public EtudiantController() {
        try {
            connection = DBConnection.getConnection();
            EtudiantDAO etudiantDAO = new EtudiantDAO(connection);
            ProfesseurDAO professeurDAO = new ProfesseurDAO(connection);
            MatiereDAO matiereDAO = new MatiereDAO(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void handleNotes(ActionEvent actionEvent) {
        loadScene("/fr/lille/junia/etuapp/view/etudiant/notes.fxml");
    }

    public void handleDocRequest(ActionEvent actionEvent) {
        setPrimaryStage(primaryStage);
        loadScene("/fr/lille/junia/etuapp/view/etudiant/docrequest.fxml");
    }

    public void handleLogout(ActionEvent actionEvent) {
        SessionManager.clearSession();
        NavigationManager.getInstance().logout();

    }

    private void loadScene(String fxmlPath) {
        try {
            System.out.println("Loading FXML from path: " + fxmlPath); // Debug
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Get the loaded controller
            BaseController loadedController = loader.getController();
            System.out.println("Loaded controller: " + loadedController.getClass().getName()); // Debug

            // Set the primary stage for any controller that extends BaseController
            if (loadedController != null) {
                loadedController.setPrimaryStage(primaryStage); // Assuming all controllers inherit this method
            } else {
                System.out.println("Loaded controller is null."); // Debug
            }

            // Use NavigationManager to navigate to the new scene
            NavigationManager.getInstance().navigateTo(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the requested page.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleMatieres(ActionEvent actionEvent) {
        loadScene("/fr/lille/junia/etuapp/view/etudiant/matieres.fxml" );
    }
}
