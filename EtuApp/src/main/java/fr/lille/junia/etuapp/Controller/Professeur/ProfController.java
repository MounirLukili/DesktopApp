package fr.lille.junia.etuapp.Controller.Professeur;

import fr.lille.junia.etuapp.Controller.SessionManager;
import fr.lille.junia.etuapp.Service.BaseController;
import fr.lille.junia.etuapp.Service.NavigationManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class ProfController extends BaseController {
    private Stage primaryStage;

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void handleLogout(ActionEvent actionEvent) {
        SessionManager.clearSession();
        NavigationManager.getInstance().logout();
    }

    public void handlePartagerNotes(ActionEvent actionEvent) {
        loadScene("/fr/lille/junia/etuapp/view/prof/partageNote.fxml" , primaryStage);

    }

    public void handleMesCours(ActionEvent actionEvent) {
        loadScene("/fr/lille/junia/etuapp/view/prof/GestionCours.fxml" , primaryStage);

    }

    private void loadScene(String fxmlPath, Stage stage) {
        NavigationManager navigationManage = NavigationManager.getInstance();



        try {
            System.out.println("Attempting to load FXML at path: " + fxmlPath); // Debug
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Log the controller class loaded
            Object loadedController = loader.getController();
            System.out.println("Loaded controller: " + loadedController.getClass().getName()); // Debug

            Scene scene = new Scene(root, 800, 600); // Set preferred width and height
            navigationManage.navigateTo(scene);

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
}
