package infinity.Controller;

import infinity.database.DatabaseManager;
import infinity.model.Wait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
public class WaitController {

    @Autowired
    private DatabaseManager databaseManager;

    @GetMapping("/wait")
    public List<Wait> getAllWait() {
        List<Wait> waitList = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection()) {
            String sql = "SELECT user_id, user_name, password, full_name, email, status FROM wait";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            int stt = 1;  // Initialize the STT counter
            while (resultSet.next()) {
                Wait wait = new Wait();
                wait.setUserId(resultSet.getInt("user_id"));
                wait.setUserName(resultSet.getString("user_name"));
                wait.setPassword(resultSet.getString("password"));
                wait.setFullName(resultSet.getString("full_name"));
                wait.setEmail(resultSet.getString("email"));
                wait.setStatus(resultSet.getString("status"));
                wait.setStt(stt++);  // Set the STT and increment
                waitList.add(wait);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return waitList;
    }

    @DeleteMapping("/wait/{userId}")
    public void deleteWait(@PathVariable int userId) {
        try (Connection connection = databaseManager.getConnection()) {
            String sql = "DELETE FROM wait WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/wait/accept/{userId}")
    public void acceptWait(@PathVariable int userId) {
        Connection connection = null;
        try {
            connection = databaseManager.getConnection();
            connection.setAutoCommit(false); // Begin transaction

            // Fetch user information from wait table
            String fetchSql = "SELECT user_id, user_name, password, full_name, email, status FROM wait WHERE user_id = ?";
            PreparedStatement fetchStatement = connection.prepareStatement(fetchSql);
            fetchStatement.setInt(1, userId);
            ResultSet resultSet = fetchStatement.executeQuery();

            if (resultSet.next()) {
                // Get the max user_id from user_infor
                String maxIdSql = "SELECT MAX(user_id) AS max_user_id FROM user_infor";
                PreparedStatement maxIdStatement = connection.prepareStatement(maxIdSql);
                ResultSet maxIdResultSet = maxIdStatement.executeQuery();
                int newUserId = 1;  // Default user_id if no records in user_infor
                if (maxIdResultSet.next()) {
                    newUserId = maxIdResultSet.getInt("max_user_id") + 1;
                }

                // Insert the fetched information into user_infor table with new user_id and authority as 'Employee'
                String insertSql = "INSERT INTO user_infor (user_id, user_name, password, full_name, email, status, authority) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement insertStatement = connection.prepareStatement(insertSql);
                insertStatement.setInt(1, newUserId);
                insertStatement.setString(2, resultSet.getString("user_name"));
                insertStatement.setString(3, resultSet.getString("password"));
                insertStatement.setString(4, resultSet.getString("full_name"));
                insertStatement.setString(5, resultSet.getString("email"));
                insertStatement.setString(6, resultSet.getString("Hoạt động"));
                insertStatement.setString(7, "Employee");  // Set authority to 'Employee'
                insertStatement.executeUpdate();

                // Delete the user from wait table after insertion
                String deleteSql = "DELETE FROM wait WHERE user_id = ?";
                PreparedStatement deleteStatement = connection.prepareStatement(deleteSql);
                deleteStatement.setInt(1, userId);
                deleteStatement.executeUpdate();

                connection.commit(); // Commit transaction
            } else {
                connection.rollback(); // Rollback if user not found
            }
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback(); // Rollback transaction on error
                } catch (SQLException rollbackException) {
                    rollbackException.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true); // Reset auto-commit to true
                    connection.close();
                } catch (SQLException closeException) {
                    closeException.printStackTrace();
                }
            }
        }
    }
}
