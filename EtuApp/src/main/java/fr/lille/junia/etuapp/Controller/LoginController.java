package fr.lille.junia.etuapp.Controller;

import fr.lille.junia.etuapp.Controller.Admin.AdminController;
import fr.lille.junia.etuapp.Controller.Etudiant.EtudiantController;
import fr.lille.junia.etuapp.Controller.Professeur.ProfController;
import fr.lille.junia.etuapp.DAO.UserDAO;
import fr.lille.junia.etuapp.Model.User;
import fr.lille.junia.etuapp.Service.BaseController;
import fr.lille.junia.etuapp.Service.NavigationManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class LoginController {
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passField;

    @FXML
    private Label errorMessageLabel;

    private UserDAO userDAO;


    // Method to initialize the DAO connection
    public void initializeDAO(Connection connection) {
        this.userDAO = new UserDAO(connection);
        System.out.println("DAO initialized with database connection.");
    }

    @FXML
    private void handleLogin() {
        // Reset the error message at the start
        errorMessageLabel.setText("");

        String email = emailField.getText();
        String password = passField.getText();
        System.out.println("Login attempt with email: " + email);
        String role = login(email, password);

        if ("Invalid login".equals(role)) {
            errorMessageLabel.setText("Invalid email or password");
        } else {
            errorMessageLabel.setText("Login successful as: " + role);
            switch (role) {
                case "Admin":
                    redirectToAdminDashboard();
                    break;
                case "Etudiant":
                    redirectToEtudiantDashboard();
                    break;
                case "Professeur":
                    redirectToProfDashboard();
                    break;
                default:
                    break; // Handle unexpected roles
            }
        }
    }

    private void redirectToProfDashboard() {
        loadScene("/fr/lille/junia/etuapp/view/profdash.fxml");
    }

    private void loadScene(String fxmlPath) {
        try {
            System.out.println("Loading FXML from path: " + fxmlPath); // Debug
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Use NavigationManager to navigate to the new scene
            NavigationManager.getInstance().navigateTo(new Scene(root));

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the requested page.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void redirectToEtudiantDashboard() {
        loadScene("/fr/lille/junia/etuapp/view/etudiantdash.fxml");
    }

    private void redirectToAdminDashboard() {
        loadScene("/fr/lille/junia/etuapp/view/admindash.fxml");
    }

    private String login(String email, String password) {
        System.out.println("Attempting to retrieve user with email: " + email);
        if (!userDAO.isConnectionValid()) {
            System.out.println("Database connection is not valid!");
            return "Invalid login";
        }
        User user = userDAO.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            System.out.println("Login successful for user: " + email);
            SessionManager.setCurrentUser(user); // Set user in session
            return user.getRole().name(); // Convert Enum to String
        } else {
            System.out.println("No user found with email: " + email);
            return "Invalid login";
        }
    }

    @FXML
    private void handleLogout() {
        SessionManager.clearSession();
        showAlert("Logout", "You have been successfully logged out.");
        loadScene("/fr/lille/junia/etuapp/view/login.fxml");
    }
}
