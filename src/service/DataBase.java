package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase{

    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=Project;encrypt=true;trustServerCertificate=true";
    private static final String USER = "java_app";
    private static final String PASSWORD = "1234";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        System.out.println("Testing connection with user 'java_app'...");
        try (Connection conn = getConnection()) {
            System.out.println("SUCCESS! Connected to database 'Project'.");
        } catch (SQLException e) {
            System.out.println("FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
}