package fr.lille.junia.etuapp.Controller.Professeur;

import fr.lille.junia.etuapp.DAO.*;
import fr.lille.junia.etuapp.DataBaseConnection.DBConnection;
import fr.lille.junia.etuapp.Model.Matiere;
import fr.lille.junia.etuapp.Model.Etudiant;
import fr.lille.junia.etuapp.Model.Professeur;
import fr.lille.junia.etuapp.Model.User;
import fr.lille.junia.etuapp.Controller.SessionManager;
import fr.lille.junia.etuapp.Service.BaseController;
import fr.lille.junia.etuapp.Service.NavigationManager;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PartageNote extends BaseController {

    @FXML
    private ComboBox<Matiere> matiereComboBox;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Etudiant> studentTable;

    @FXML
    private TableColumn<Etudiant, Integer> studentIdColumn;

    @FXML
    private TableColumn<Etudiant, String> studentNameColumn;

    @FXML
    private TextField noteField;

    private MatiereDAO matiereDAO;
    private ProfesseurDAO profDAO;
    private EtudiantDAO etudiantDAO;
    private NoteDAO noteDAO;
    private ProfesseurMatiereDAO profMatiereDAO;
    private ObservableList<Etudiant> etudiants;

    public PartageNote() throws SQLException {
        Connection connection = DBConnection.getConnection();
        matiereDAO = new MatiereDAO(connection);
        profDAO = new ProfesseurDAO(connection);
        etudiantDAO = new EtudiantDAO(connection);
        profMatiereDAO = new ProfesseurMatiereDAO(connection);
        noteDAO = new NoteDAO(connection);
    }

    @FXML
    public void initialize() {
        setupTable();
        loadMatieres();
        setupSearch();
    }

    private void setupTable() {
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Custom cell value factory for the studentNameColumn
        studentNameColumn.setCellValueFactory(cellData -> {
            Etudiant etudiant = cellData.getValue();
            User user = etudiantDAO.getUserById(etudiant.getUserId());
            if (user != null) {
                // Combine `nom` and `prenom` into a full name
                return new ReadOnlyStringWrapper(user.getNom() + " " + user.getPrenom());
            }
            return new ReadOnlyStringWrapper(""); // Return empty if User is null
        });
    }


    private void loadMatieres() {
        Professeur currentProf = profDAO.findprofByUserId(SessionManager.getCurrentUser().getId());
        if (currentProf != null) {
            int departementId = currentProf.getDepartementId();
            List<Matiere> matieres = matiereDAO.findMatieresByDepartementId(departementId);
            matiereComboBox.setItems(FXCollections.observableArrayList(matieres));
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            List<Etudiant> filteredStudents = etudiantDAO.findEtudiantsByName(newValue);
            etudiants = FXCollections.observableArrayList(filteredStudents);
            studentTable.setItems(etudiants);
        });
    }

    @FXML
    private void handleShareNote(ActionEvent event) {
        Matiere selectedMatiere = matiereComboBox.getValue();
        Etudiant selectedEtudiant = studentTable.getSelectionModel().getSelectedItem();
        String noteText = noteField.getText();

        if (selectedMatiere == null || selectedEtudiant == null || noteText.isEmpty()) {
            showAlert("Erreur", "Veuillez sélectionner une matière, un étudiant, et saisir une note.");
            return;
        }

        try {
            double note = Double.parseDouble(noteText);
            // Implement the logic to save or update the note in the database
            noteDAO.addGrade(selectedEtudiant.getId(), selectedMatiere.getId(), note);
            showAlert("Succès", "La note a été partagée avec succès.");
            clearfields();
        } catch (NumberFormatException e) {
            showAlert("Erreur", "La note doit être un nombre valide.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleRetour(ActionEvent actionEvent) {
        NavigationManager navigationManager = NavigationManager.getInstance(); // Get the instance of NavigationManager
        navigationManager.goBack();
    }

    public void clearfields(){
        matiereComboBox.setValue(null);
        searchField.clear();
        noteField.clear();
        studentTable.setItems(null);


    }
}
