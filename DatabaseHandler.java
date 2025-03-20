package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHandler {
    private static DatabaseHandler instance;

    private DatabaseHandler() {
        // Private constructor to enforce singleton pattern
    }

    public static DatabaseHandler getInstance() {
        if (instance == null) {
            synchronized (DatabaseHandler.class) {
                if (instance == null) {
                    instance = new DatabaseHandler();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/quiz_app", "root", "33Shadowsan@");
            connection.setAutoCommit(true);
            System.out.println("Database connection successful!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Database connection failed!");
        }
        return connection;
    }
}