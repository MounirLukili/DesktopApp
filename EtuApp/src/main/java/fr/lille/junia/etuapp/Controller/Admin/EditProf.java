package fr.lille.junia.etuapp.Controller.Admin;

import fr.lille.junia.etuapp.DAO.ProfesseurDAO;
import fr.lille.junia.etuapp.DataBaseConnection.DBConnection;
import fr.lille.junia.etuapp.DAO.UserDAO;
import fr.lille.junia.etuapp.Model.Professeur;
import fr.lille.junia.etuapp.Model.User;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class EditProf {
    @FXML
    private TextField searchField;
    @FXML
    private ListView<String> profListView;

    private Professeur selectedProf;
    private ProfesseurDAO professeurDAO;
    private UserDAO userDAO;
    private ObservableList<String> displayList = FXCollections.observableArrayList();

    Connection connection = DBConnection.getConnection();

    public EditProf() throws SQLException {
        this.professeurDAO = new ProfesseurDAO(connection);
        this.userDAO = new UserDAO(connection);
    }

    @FXML
    public void initialize() {
        // Initialize ListView with ObservableList
        profListView.setItems(displayList);
        profListView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.SINGLE);
    }

    @FXML
    public void selectProf() {
        String selectedItem = profListView.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            // Debugging output
            System.out.println("Selected item: " + selectedItem);
            int profId = Integer.parseInt(selectedItem.split(" - ")[0]); // Extract the ID
            selectedProf = professeurDAO.read(profId);

            // Close the pop-up window
            ((Stage) profListView.getScene().getWindow()).close();
        } else {
            System.out.println("No prof selected.");
        }
    }

    public Professeur getSelectedProf() {
        return selectedProf;
    }

    @FXML
    public void searchProf() {
        displayList.clear(); // Clear previous results
        String searchText = searchField.getText().trim();
        List<Professeur> allProfs = professeurDAO.readAll();

        // Populate displayList based on search criteria
        for (Professeur professeur : allProfs) {
            User user = userDAO.read(professeur.getUserId());
            if (user != null && (user.getNom().contains(searchText) || user.getPrenom().contains(searchText))) {
                String displayText = professeur.getId() + " - " + user.getPrenom() + " " + user.getNom();
                displayList.add(displayText);
            }
        }

        // Confirm that items are in ListView
        System.out.println("Items in ListView: " + displayList.size());
    }
}
