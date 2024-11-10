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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class EtudiantMAddController extends BaseController {

    @FXML
    private ComboBox<Matiere> matiereComboBox;
    private MatiereDAO matiereDAO;
    private EtudiantDAO etudiantDAO;
    private EtudiantMatiereDAO etudiantMatiereDAO;
    private ObservableList<Matiere> matieres;
    private Stage primaryStage;

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
    private EtudiantMController etudiantMController;
    public void setEtudiantMController(EtudiantMController controller) {
        this.etudiantMController = controller;
    }


    public EtudiantMAddController() throws SQLException {
        // Initialize DAO objects
        Connection connection = DBConnection.getConnection();
        etudiantMatiereDAO = new EtudiantMatiereDAO(connection);
        matiereDAO = new MatiereDAO(connection);
        etudiantDAO = new EtudiantDAO(connection);

    }

    @FXML
    public void initialize() {
        loadetudiantmatiere();
    }

    private void loadetudiantmatiere() {
        Etudiant currentEtudiant = etudiantDAO.findEtudiantByUserId(SessionManager.getCurrentUser().getId());
        if (currentEtudiant != null) {
            int departementId = currentEtudiant.getDepartementId();
            List<Matiere> matieres = matiereDAO.findMatieresByDepartementId(departementId);
            matiereComboBox.setItems(FXCollections.observableArrayList(matieres));
        }
    }




    @FXML
    private void handleAjouterMatiere() {
        Matiere selectedMatiere = matiereComboBox.getValue();
        if (selectedMatiere != null) {
            User currentUser = SessionManager.getCurrentUser();
            if (currentUser != null) {
                int userID = currentUser.getId();
                Etudiant etudiant = etudiantDAO.findEtudiantByUserId(userID);

                // Check if the Matiere already exists for the Etudiant
                if (etudiantMatiereDAO.matiereExistsForEtudiant(etudiant.getId(), selectedMatiere.getId())) {
                    showAlert("Error", "This Matiere already exists for the selected Etudiant.");
                    return; // Exit the method if it already exists
                }

                // Call DAO method to add the Matiere for the Etudiant
                etudiantMatiereDAO.create(etudiant.getId(), selectedMatiere.getId());
                showAlert("Success", "Matiere added successfully.");
                etudiantMController.refreshMatieres(); // Refresh the table in the main controller
            }
        } else {
            showAlert("Error", "Please select a matiere to add.");
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
