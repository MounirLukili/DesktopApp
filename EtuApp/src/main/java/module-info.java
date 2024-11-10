module fr.lille.junia.etuapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires java.mail;


    opens fr.lille.junia.etuapp to javafx.fxml;
    exports fr.lille.junia.etuapp;

    exports fr.lille.junia.etuapp.Controller; // Export the Controller package
    opens fr.lille.junia.etuapp.Controller to javafx.fxml;
    exports fr.lille.junia.etuapp.Controller.Admin to javafx.fxml;
    opens fr.lille.junia.etuapp.Controller.Admin to javafx.fxml;
    exports fr.lille.junia.etuapp.Controller.Etudiant;

    opens fr.lille.junia.etuapp.Controller.Etudiant to javafx.fxml;
    opens fr.lille.junia.etuapp.Model to javafx.fxml;


    exports fr.lille.junia.etuapp.Model;
    // Export the parent package
    exports fr.lille.junia.etuapp.Controller.Professeur;
    opens fr.lille.junia.etuapp.Controller.Professeur to javafx.fxml;
}