package fr.lille.junia.etuapp.Controller.Professeur;

import fr.lille.junia.etuapp.Controller.Etudiant.EtudiantMController;
import fr.lille.junia.etuapp.Controller.SessionManager;
import fr.lille.junia.etuapp.DAO.*;
import fr.lille.junia.etuapp.DataBaseConnection.DBConnection;
import fr.lille.junia.etuapp.Model.Matiere;
import fr.lille.junia.etuapp.Model.Professeur;
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

public class GestionAddCours extends BaseController {

    @FXML
    private ComboBox<Matiere> matiereComboBox;
    private MatiereDAO matiereDAO;
    private ProfesseurDAO profDAO;
    private ProfesseurMatiereDAO profMatiereDAO;
    private ObservableList<Matiere> matieres;
    private Stage primaryStage;

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
    private GestionCours gestioncoursController;
    public void setGestionCoursController(GestionCours controller) {
        this.gestioncoursController = controller;
    }



    public GestionAddCours() throws SQLException {
        Connection connection = DBConnection.getConnection();
        profMatiereDAO = new ProfesseurMatiereDAO(connection);
        matiereDAO = new MatiereDAO(connection);
        profDAO = new ProfesseurDAO(connection);
    }

    @FXML
    public void initialize() {
        loadetudiantmatiere();

    }

    private void loadetudiantmatiere() {

        Professeur currentProf = profDAO.findprofByUserId(SessionManager.getCurrentUser().getId());
        if (currentProf != null) {
            int departementId = currentProf.getDepartementId();
            List<Matiere> matieres = matiereDAO.findMatieresByDepartementId(departementId);
            matiereComboBox.setItems(FXCollections.observableArrayList(matieres));
        }
    }

    @FXML
    private void handleAjouterMatiere(ActionEvent actionEvent) {
        Matiere selectedMatiere = matiereComboBox.getValue();
        if (selectedMatiere != null) {
            User currentUser = SessionManager.getCurrentUser();
            if (currentUser != null) {
                int userID = currentUser.getId();
                Professeur professeur = profDAO.findprofByUserId(userID);

                // Check if the Matiere already exists for the Prof
                if (profMatiereDAO.matiereExistsForProf(professeur.getId(), selectedMatiere.getId())) {
                    showAlert("Error", "This Matiere already exists for the selected Prof.");
                    return; // Exit the method if it already exists
                }

                // Call DAO method to add the Matiere for the Prof
                profMatiereDAO.create(professeur.getId(), selectedMatiere.getId());
                showAlert("Success", "Matiere added successfully.");
                gestioncoursController.refreshMatieres(); // Refresh the Matiere table directly after adding
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