package infinity.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.springframework.stereotype.Component;

@Component
public class DatabaseManager {
    public static final String URL = "jdbc:mysql://localhost:3306/isd";
    public static final String USERNAME = "Kieuhieu2003";
    public static final String PASSWORD = "Kieuhieu@2003";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
