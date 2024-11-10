package fr.lille.junia.etuapp;

import fr.lille.junia.etuapp.Controller.LoginController;
import fr.lille.junia.etuapp.DataBaseConnection.DBConnection;
import fr.lille.junia.etuapp.Service.NavigationManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class HelloApplication extends Application {
    private Stage primaryStage;
    private Connection conn;

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;

        try {

            // Establish database connection
            conn = DBConnection.getConnection();
            System.out.println("Connection successful!");

            // Load the FXML file and get the root node
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fr/lille/junia/etuapp/view/login.fxml"));
            BorderPane root = fxmlLoader.load();

            // Get controller instance and initialize DAO
            LoginController loginController = fxmlLoader.getController();
            loginController.initializeDAO(conn); // Pass the connection to initializeDAO


            // Set up the scene and show the stage
            Scene loginScene = new Scene(root);
            primaryStage.setTitle("Login - EduNotes");
            primaryStage.setScene(loginScene);
            primaryStage.show();

            // Set the primary stage and store the initial login scene
            NavigationManager navigationManager = NavigationManager.getInstance();
            navigationManager.setPrimaryStage(primaryStage);
            navigationManager.setInitialScene(loginScene);

        } catch (SQLException e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
            // Optionally, show an error dialog to the user here
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        // Close the connection when the application stops to prevent resource leaks
        if (conn != null && !conn.isClosed()) {
            conn.close();
            System.out.println("Connection closed!");
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
