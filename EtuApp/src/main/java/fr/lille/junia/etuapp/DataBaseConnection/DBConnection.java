package fr.lille.junia.etuapp.DataBaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



public class DBConnection {
    private static final String URL = "jdbc:mysql://edudb.clswo428ulpi.eu-west-3.rds.amazonaws.com:3306/edudb";
    private static final String USER = "root";
    private static final String PASSWORD = "sDnylJfVO5S4O0Qsq5hB";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }


}


// private static final String URL = "jdbc:mysql://sql7.freesqldatabase.com/sql7742691";
//    private static final String USER = "sql7742691";
//    private static final String PASSWORD = "qVfWQKtGSi";
