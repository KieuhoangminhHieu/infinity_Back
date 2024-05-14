package infinity.Controller;

import infinity.database.DatabaseManager;
import infinity.model.UserInfor;
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
public class UserInforController {

    @Autowired
    private DatabaseManager databaseManager;

    @GetMapping("/user-infor")
    public List<UserInfor> getAllUserInfor() {
        List<UserInfor> userInforList = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection()) {
            String sql = "SELECT user_id, user_name, password, full_name, email, status, authority FROM user_infor";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                UserInfor userInfor = new UserInfor();
                userInfor.setUserId(resultSet.getInt("user_id"));
                userInfor.setUserName(resultSet.getString("user_name"));
                userInfor.setPassword(resultSet.getString("password"));
                userInfor.setFullName(resultSet.getString("full_name"));
                userInfor.setEmail(resultSet.getString("email"));
                userInfor.setStatus(resultSet.getString("status"));
                userInfor.setAuthority(resultSet.getString("authority"));
                userInforList.add(userInfor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userInforList;
    }

    @PutMapping("/user-infor/{userId}")
    public void updateUserInfor(@PathVariable int userId, @RequestBody UserInfor userInfor) {
        try (Connection connection = databaseManager.getConnection()) {
            String sql = "UPDATE user_infor SET user_name = ?, password = ?, full_name = ?, email = ?, status = ?, authority = ? WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, userInfor.getUserName());
            statement.setString(2, userInfor.getPassword());
            statement.setString(3, userInfor.getFullName());
            statement.setString(4, userInfor.getEmail());
            statement.setString(5, userInfor.getStatus());
            statement.setString(6, userInfor.getAuthority());
            statement.setInt(7, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @DeleteMapping("/user-infor/{userId}")
    public void deleteUserInfor(@PathVariable int userId) {
        try (Connection connection = databaseManager.getConnection()) {
            String sql = "DELETE FROM user_infor WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @PutMapping("/user-infor/{userId}/status")
    public void updateUserStatus(@PathVariable int userId, @RequestBody String status) {
        List<String> allowedStatuses = List.of("Dừng", "Hoạt động", "Tạm nghỉ");
        if (!allowedStatuses.contains(status)) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }

        try (Connection connection = databaseManager.getConnection()) {
            String sql = "UPDATE user_infor SET status = ? WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, status);
            statement.setInt(2, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @PutMapping("/user-infor/{userId}/authority")
    public void updateUserAuthority(@PathVariable int userId, @RequestBody String authority) {
        List<String> allowedAuthorities = List.of("admin", "Lead", "Employee");
        if (!allowedAuthorities.contains(authority)) {
            throw new IllegalArgumentException("Invalid authority value: " + authority);
        }

        try (Connection connection = databaseManager.getConnection()) {
            String sql = "UPDATE user_infor SET authority = ? WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, authority);
            statement.setInt(2, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
