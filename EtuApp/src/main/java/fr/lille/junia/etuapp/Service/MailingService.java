package fr.lille.junia.etuapp.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailingService {
    private String destinataire;
    private String sujet;
    private String contenu;

    private final String senderEmail = "EtuAppMailer@gmail.com"; // replace with your Gmail address
    private final String senderPassword = "xopt cowe oidt fkoo"; // replace with your Gmail password

    // Method to set the recipient
    public void setDestinataire(String destinataire) {
        this.destinataire = destinataire;
    }

    // Method to set the subject
    public void setSujet(String sujet) {
        this.sujet = sujet;
    }

    // Method to set the content
    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    // Method to send the email
    public boolean envoyerEmail() {
        // Gmail SMTP server configuration
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Create a session with authentication
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            // Create a message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
            message.setSubject(sujet);
            message.setText(contenu);

            // Send the email
            Transport.send(message);
            System.out.println("Email sent successfully to " + destinataire);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Failed to send email");
            return false;
        }
    }
}

