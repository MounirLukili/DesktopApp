package fr.lille.junia.etuapp.Controller.Etudiant;

import fr.lille.junia.etuapp.DAO.EtudiantDAO;
import fr.lille.junia.etuapp.DAO.NoteDAO;
import fr.lille.junia.etuapp.DataBaseConnection.DBConnection;
import fr.lille.junia.etuapp.Model.Etudiant;
import fr.lille.junia.etuapp.Model.Note;
import fr.lille.junia.etuapp.Service.BaseController;
import fr.lille.junia.etuapp.Service.NavigationManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import fr.lille.junia.etuapp.DAO.EtudiantMatiereDAO; // Update with your actual DAO
import fr.lille.junia.etuapp.Model.Matiere; // Update with your actual Model
import fr.lille.junia.etuapp.Controller.SessionManager;
import javafx.stage.Modality;
import javafx.stage.Stage;
// Assuming this is where you manage sessions

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class NoteController extends BaseController {
    @FXML
    private ListView<Matiere> matieresListView;
    private EtudiantDAO etudiantDAO;
    private EtudiantMatiereDAO etudiantMatiereDAO;
    @FXML
    private Label matiereLabel;
    @FXML
    private ListView<Note> marksListView;
    private NoteDAO marksDAO;

    public NoteController() {
        try {
            Connection conn = DBConnection.getConnection();
            // Assign the DAO instances to the instance variables
            etudiantMatiereDAO = new EtudiantMatiereDAO(conn);
            etudiantDAO = new EtudiantDAO(conn);
            marksDAO = new NoteDAO(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception accordingly
        }
    }

    @FXML
    public void initialize() throws SQLException {
        loadMatieres();
    }

    private void loadMatieres() {
        // Get the currently connected student ID
        int userID = SessionManager.getCurrentUser().getId();
        Etudiant etudiant = etudiantDAO.findEtudiantByUserId(userID);
        int etudiantId = etudiant.getId();

        // Fetch matieres for the student
        ObservableList<Matiere> matieres = FXCollections.observableArrayList(etudiantMatiereDAO.getMatieresByEtudiantId(etudiantId));

        // Set the items to the ListView
        matieresListView.setItems(matieres);

        // Add click listener
        matieresListView.setOnMouseClicked(this::handleMatiereClick);
    }

    private void handleMatiereClick(MouseEvent event) {
        if (event.getClickCount() == 2) { // Double-click to view marks
            Matiere selectedMatiere = matieresListView.getSelectionModel().getSelectedItem();
            if (selectedMatiere != null) {
                // Open the new pop-up for marks
                openMarksPopup(selectedMatiere);
            }
        }
    }

    private void openMarksPopup(Matiere matiere) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/lille/junia/etuapp/view/etudiant/detailNotes.fxml"));
            Parent root = loader.load();

            NoteDController noteDController = loader.getController();
            noteDController.setMatiere(matiere); // Pass the selected matiere to the controller

            Stage stage = new Stage();
            stage.setTitle("Marks for " + matiere.getNom());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Modal to prevent interaction with main window
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMatiere(Matiere matiere) {
        matiereLabel.setText(matiere.getNom()); // Assuming you have a getName() method
        loadMarks(matiere.getId()); // Assuming you have a getId() method
    }

    private void loadMarks(int matiereId) {

        ObservableList<Note> marks = FXCollections.observableArrayList(marksDAO.read(matiereId));
        marksListView.setItems(marks);
    }

    public void handleRetour(ActionEvent actionEvent) {
        NavigationManager navigationManager = NavigationManager.getInstance(); // Get the instance of NavigationManager
        navigationManager.goBack();
    }
}
