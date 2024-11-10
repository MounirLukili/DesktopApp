package fr.lille.junia.etuapp.Controller.Etudiant;

import fr.lille.junia.etuapp.Controller.SessionManager;
import fr.lille.junia.etuapp.DAO.EtudiantDAO;
import fr.lille.junia.etuapp.DAO.EtudiantMatiereDAO;
import fr.lille.junia.etuapp.DAO.MatiereDAO;
import fr.lille.junia.etuapp.DataBaseConnection.DBConnection;
import fr.lille.junia.etuapp.Model.Etudiant;
import fr.lille.junia.etuapp.Model.Matiere;
import fr.lille.junia.etuapp.Model.User;
import fr.lille.junia.etuapp.Service.BaseController;
import fr.lille.junia.etuapp.Service.NavigationManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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

public class EtudiantMController extends BaseController {
    @FXML
    private TableColumn<Matiere, String> matiereColumn; // Declare TableColumn
    @FXML
    private TableView<Matiere> matiereTable; // Declare TableView
    private Stage primaryStage;
    @FXML
    private ComboBox<Matiere> matiereComboBox;

    private MatiereDAO matiereDAO;
    private EtudiantDAO etudiantDAO;
    private EtudiantMatiereDAO etudiantMatiereDAO;
    private ObservableList<Matiere> matieres;



    public EtudiantMController() throws SQLException {
        // Initialize DAO objects
        Connection connection = DBConnection.getConnection();
        etudiantMatiereDAO = new EtudiantMatiereDAO(connection);
        matiereDAO = new MatiereDAO(connection);
        etudiantDAO = new EtudiantDAO(connection);

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
            Etudiant etudiant = etudiantDAO.findEtudiantByUserId(userId);

            if (etudiant != null) {
                int etudiantId = etudiant.getId();
                System.out.println("Etudiant ID: " + etudiantId);
                List<Integer> matiereIds = etudiantMatiereDAO.readMatieresByEtudiant(etudiantId);
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
                Etudiant etudiant = etudiantDAO.findEtudiantByUserId(userId);
                etudiantMatiereDAO.delete(etudiant.getId(), selectedMatiere.getId());
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
    private void handleAddMatiere(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/lille/junia/etuapp/view/etudiant/addMatiere.fxml"));
        Parent root = loader.load();

        EtudiantMAddController addController = loader.getController();
        addController.setEtudiantMController(this);

        Stage selectStage = new Stage();
        selectStage.setScene(new Scene(root));
        selectStage.initModality(Modality.APPLICATION_MODAL);
        selectStage.showAndWait();
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
