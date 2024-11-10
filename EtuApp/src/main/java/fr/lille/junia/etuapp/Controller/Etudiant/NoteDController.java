package fr.lille.junia.etuapp.Controller.Etudiant;

import fr.lille.junia.etuapp.Controller.SessionManager;
import fr.lille.junia.etuapp.DAO.EtudiantDAO;
import fr.lille.junia.etuapp.DataBaseConnection.DBConnection;
import fr.lille.junia.etuapp.Model.Etudiant;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import fr.lille.junia.etuapp.DAO.NoteDAO; // Update with your actual DAO
import fr.lille.junia.etuapp.Model.Note; // Update with your actual Model
import fr.lille.junia.etuapp.Model.Matiere; // Update with your actual Model

import java.sql.Connection;
import java.sql.SQLException;

public class NoteDController {

    @FXML
    private Label matiereLabel;
    @FXML
    private ListView<Note> marksListView;
    private NoteDAO marksDAO;
    private EtudiantDAO etudiantDAO;

    public NoteDController() throws SQLException {
        Connection conn = DBConnection.getConnection();
        marksDAO = new NoteDAO(conn);
        etudiantDAO = new EtudiantDAO(conn);
    }

    public void initialize() {
        // Set a custom cell factory to format the display of notes
        marksListView.setCellFactory(param -> new ListCell<Note>() {
            @Override
            protected void updateItem(Note note, boolean empty) {
                super.updateItem(note, empty);
                if (empty || note == null) {
                    setText(null);
                } else {
                    // Customize the display here
                    String displayText = String.format(
                            "Note : %.2f\nMatiere ID: %d",
                               // Assuming this is an Integer
                            (double) note.getValeur(),  // Cast to double for formatting as a float
                            note.getMatiereId()   // Assuming this is also an Integer
                    );
                    setText(displayText);
                }

            }
        });
    }

    public void setMatiere(Matiere matiere) {
        matiereLabel.setText(matiere.getNom()); // Assuming you have a getNom() method
        loadMarks(matiere.getId()); // Assuming you have a getId() method
    }

    private void loadMarks(int matiereId) {
        // Fetch the user ID from the current session
        int user_id = SessionManager.getCurrentUser().getId();

        // Find the Etudiant (student) by the user ID
        Etudiant etudiant = etudiantDAO.findEtudiantByUserId(user_id);

        // Get the student's ID
        int student_id = etudiant.getId();

        // Fetch marks for the specific student (student_id) and matiere (matiereId)
        ObservableList<Note> marks = FXCollections.observableArrayList(marksDAO.getMarksByStudentAndMatiereId(student_id, matiereId));

        // Set the ListView's items to the filtered marks
        marksListView.setItems(marks);
    }

}
