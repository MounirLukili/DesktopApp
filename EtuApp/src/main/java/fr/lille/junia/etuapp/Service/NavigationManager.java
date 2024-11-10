package fr.lille.junia.etuapp.Service;

import fr.lille.junia.etuapp.Controller.LoginController;
import fr.lille.junia.etuapp.DataBaseConnection.DBConnection;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Stack;

public class NavigationManager {
    private static NavigationManager instance;
    private Stage primaryStage;
    private Stack<Scene> sceneStack;
    private Scene initialScene;

    private NavigationManager() {
        sceneStack = new Stack<>();
    }

    // Singleton instance
    public static NavigationManager getInstance() {
        if (instance == null) {
            instance = new NavigationManager();
        }
        return instance;
    }

    // Set the primary stage
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    // Navigate to a new scene
    public void navigateTo(Scene scene) {
        if (primaryStage != null) {
            sceneStack.push(primaryStage.getScene());  // Push the current scene onto the stack
            primaryStage.setScene(scene);  // Set the new scene
        }
    }

    // Go back to the previous scene
    public void goBack() {
        if (!sceneStack.isEmpty()) {
            Scene previousScene = sceneStack.pop();  // Pop the last scene from the stack
            primaryStage.setScene(previousScene);  // Set the previous scene
        }
    }

    public void setInitialScene(Scene scene) {
        this.initialScene = scene;
    }

    public void logout() {
        if (primaryStage != null) {
            sceneStack.clear();  // Clear the scene stack to reset navigation history

            try {
                // Reload the login FXML file to reset all data
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fr/lille/junia/etuapp/view/login.fxml"));
                BorderPane root = fxmlLoader.load();
                Scene loginScene = new Scene(root);

                // Set the new login scene on the primary stage
                primaryStage.setScene(loginScene);

                // Optionally, initialize the login controller again
                LoginController loginController = fxmlLoader.getController();
                loginController.initializeDAO(DBConnection.getConnection()); // Re-initialize with the connection if needed

            } catch (IOException e) {
                e.printStackTrace();
                // Optionally, show an error dialog to the user here
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
