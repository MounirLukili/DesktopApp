package fr.lille.junia.etuapp.Controller.Etudiant;

import fr.lille.junia.etuapp.Service.BaseController;
import fr.lille.junia.etuapp.Service.NavigationManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import fr.lille.junia.etuapp.Controller.SessionManager;
import fr.lille.junia.etuapp.Model.User;
import fr.lille.junia.etuapp.Service.MailingService;
import javafx.stage.Stage;

public class DemandeDoc extends BaseController {

    @FXML
    private ComboBox<String> documentComboBox;




    // Handle the send request button action
    public void handleSendRequest(ActionEvent actionEvent) {
        // Get the selected document from the ComboBox
        String selectedDocument = documentComboBox.getValue();

        // Check if a document was selected
        if (selectedDocument == null) {
            showAlert("No Document Selected", "Please select a document type from the dropdown.");
            return;
        }

        // Retrieve the current user from the session
        User currentUser = SessionManager.getCurrentUser();

        // Check if there is a current user in session
        if (currentUser == null) {
            showAlert("User Not Logged In", "Please log in to make a document request.");
            return;
        }

        // Prepare email content with user information
        String emailContent = "Document Request: " + selectedDocument + "\n\n" +
                "Requested by:\n" +
                "Name: " + currentUser.getPrenom() + " " + currentUser.getNom() + "\n" +
                "Email: " + currentUser.getEmail() + "\n" +
                "Role: " + currentUser.getRole();

        // Create a new MailingService instance
        MailingService mailingService = new MailingService();
        mailingService.setDestinataire("pfemailer45@gmail.com");  // Replace with actual recipient email
        mailingService.setSujet("Document Request: " + selectedDocument);
        mailingService.setContenu(emailContent);

        // Send the email and provide feedback
        boolean success = mailingService.envoyerEmail();
        if (success) {
            showAlert("Request Sent", "Your document request has been sent successfully.");
        } else {
            showAlert("Request Failed", "There was an error sending your document request. Please try again.");
        }
    }

    // Utility method to show an alert dialog
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
}
