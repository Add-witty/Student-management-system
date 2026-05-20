import java.sql.*;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/university";
    private static final String USER = "root";
    private static final String PASSWORD = "Sai_Khairnar@811";

    public static Connection connect() {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish Connection
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println(" Database connected successfully!");
            return conn;
        } catch (ClassNotFoundException e) {
            System.out.println(" MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println(" Database connection failed!");
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        connect();
    }
}
