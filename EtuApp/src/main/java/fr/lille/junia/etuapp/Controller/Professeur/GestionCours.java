package fr.lille.junia.etuapp.Controller.Professeur;




import fr.lille.junia.etuapp.Controller.SessionManager;
import fr.lille.junia.etuapp.DAO.*;
import fr.lille.junia.etuapp.Model.*;
import fr.lille.junia.etuapp.Service.NavigationManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import fr.lille.junia.etuapp.DataBaseConnection.DBConnection;
import fr.lille.junia.etuapp.Model.Matiere;
import fr.lille.junia.etuapp.Model.User;
import fr.lille.junia.etuapp.Service.BaseController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class GestionCours extends BaseController {
    @FXML
    private TableColumn<Matiere, String> matiereColumn; // Declare TableColumn
    @FXML
    private TableView<Matiere> matiereTable; // Declare TableView
    @FXML
    private ComboBox<Matiere> matiereComboBox;
    private Stage primaryStage;

    private MatiereDAO matiereDAO;
    private ProfesseurDAO profDAO;
    private ProfesseurMatiereDAO profMatiereDAO;

    private ObservableList<Matiere> matieres;



    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public GestionCours() throws SQLException {
        // Initialize DAO objects
        Connection connection = DBConnection.getConnection();
        profMatiereDAO = new ProfesseurMatiereDAO(connection);
        matiereDAO = new MatiereDAO(connection);
        profDAO = new ProfesseurDAO(connection);



    }

    @FXML
    public void initialize() {
        matiereColumn.setCellValueFactory(new PropertyValueFactory<>("nom")); // Set the cell value factory
        loadMatieres();

    }




    private void loadMatieres() {
        User currentUser = SessionManager.getCurrentUser();
        System.out.println("Current User: " + (currentUser != null ? currentUser.getPrenom() + " " + currentUser.getNom() : "No user logged in"));

        if (currentUser != null) {
            int userId = currentUser.getId();
            System.out.println("User ID: " + userId);
            Professeur prof = profDAO.findprofByUserId(userId);

            if (prof != null) {
                int etudiantId = prof.getId();
                System.out.println("Prof ID: " + etudiantId);
                List<Integer> matiereIds = profMatiereDAO.readMatieresByprof(etudiantId);
                System.out.println("Retrieved Matiere IDs: " + matiereIds);

                matieres = FXCollections.observableArrayList();
                for (int matiereId : matiereIds) {
                    Matiere matiere = matiereDAO.findMatiereById(matiereId);
                    System.out.println("Processing Matiere ID: " + matiereId + ", Found Matiere: " + (matiere != null ? matiere.getNom() : "No Matiere found"));

                    if (matiere != null) {
                        matieres.add(matiere);
                    }
                }

                System.out.println("Final list of Matieres to display: " + matieres);
                matiereTable.setItems(matieres);
            } else {
                System.out.println("No Etudiant found for User ID: " + userId);
            }
        }
    }

    @FXML
    private void handleDeleteMatiere(ActionEvent event) {
        // Get the selected Matiere from the TableView
        Matiere selectedMatiere = matiereTable.getSelectionModel().getSelectedItem();
        if (selectedMatiere != null) {
            User currentUser = SessionManager.getCurrentUser();
            if (currentUser != null) {
                // Delete the relation from Etudiant_Matiere
                int userId = currentUser.getId();
                Professeur prof = profDAO.findprofByUserId(userId);
                profMatiereDAO.delete(prof.getId(), selectedMatiere.getId());
                // Remove from the ObservableList to update the TableView
                matieres.remove(selectedMatiere);
                showAlert("Success", "Matiere removed successfully.");
            }
        } else {
            showAlert("Error", "Please select a matiere to delete.");
        }
    }



    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }





    @FXML
    private void handleAddMatiere(ActionEvent event) throws IOException, SQLException {
        // Load the FXML file for adding Matiere
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/lille/junia/etuapp/view/prof/addMatiere.fxml"));
        Parent root = loader.load();

        // Get the current controller (GestionCours)
        GestionAddCours currentController = loader.getController();
        currentController.setGestionCoursController(this);

        // Access the ComboBox in the newly loaded scene
       // ComboBox<Matiere> matiereComboBox = (ComboBox<Matiere>) root.lookup("#matiereComboBox");

        // Load Matiere options for the ComboBox
        //User user = SessionManager.getCurrentUser();
        //int userId = user.getId();
        //Professeur professeur = profDAO.findprofByUserId(userId);
        //int dep = professeur.getDepartementId();
        //List<Matiere> allMatieres = matiereDAO.findMatieresByDepartementId(dep);
        //ObservableList<Matiere> matiereOptions = FXCollections.observableArrayList(allMatieres);

        // Set the ComboBox items
       // matiereComboBox.setItems(matiereOptions);

        // Create and show the new stage for adding Matiere
        Stage selectStage = new Stage();
        selectStage.setScene(new Scene(root));
        selectStage.initModality(Modality.APPLICATION_MODAL);
        selectStage.showAndWait();

        // Refresh Matiere list after adding
        loadMatieres();
    }



    public void refreshMatieres() {
        loadMatieres(); // Call loadMatieres to refresh the data
    }


    public void handleRetour(ActionEvent actionEvent) {
        NavigationManager navigationManager = NavigationManager.getInstance(); // Get the instance of NavigationManager
        navigationManager.goBack();
    }
}


