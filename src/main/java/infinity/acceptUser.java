package infinity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
@SpringBootApplication
@RestController
public class acceptUser {private final String DB_URL = "jdbc:mysql://localhost/isd";
    private final String DB_USER = "Kieuhieu2003";
    private final String DB_PASSWORD = "Kieuhieu@2003";

    public static void main(String[] args) {
        SpringApplication.run(acceptUser.class, args);
    }

    @GetMapping("/acceptUser")
    public String acceptUser(@RequestParam("id") int userId) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Lấy user_id lớn nhất trong bảng user_infor
            int maxUserId = getMaxUserId(conn);

            // Tạo user_id mới
            int newUserId = maxUserId + 1;

            // Truy vấn để lấy dữ liệu của người dùng từ bảng "wait"
            PreparedStatement selectStmt = conn.prepareStatement("SELECT * FROM wait WHERE user_id=?");
            selectStmt.setInt(1, userId);
            ResultSet resultSet = selectStmt.executeQuery();

            if (resultSet.next()) {
                String fullName = resultSet.getString("full_name");
                String userName = resultSet.getString("user_name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");

                // Truy vấn để chèn dữ liệu vào bảng "user_infor" với user_id mới
                PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO user_infor (user_id, full_name, user_name, email, password) VALUES (?, ?, ?, ?, ?)");
                insertStmt.setInt(1, newUserId);
                insertStmt.setString(2, fullName);
                insertStmt.setString(3, userName);
                insertStmt.setString(4, email);
                insertStmt.setString(5, password);
                int rowsInserted = insertStmt.executeUpdate();

                if (rowsInserted > 0) {
                    // Nếu chèn dữ liệu thành công, tiến hành xóa dữ liệu khỏi bảng "wait"
                    PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM wait WHERE user_id=?");
                    deleteStmt.setInt(1, userId);
                    int rowsDeleted = deleteStmt.executeUpdate();

                    if (rowsDeleted > 0) {
                        return "Chấp nhận người dùng thành công";
                    } else {
                        return "Lỗi: Không thể xóa người dùng khỏi bảng 'wait'.";
                    }
                } else {
                    return "Lỗi: Không thể chèn dữ liệu của người dùng vào bảng 'user_info'.";
                }
            } else {
                return "Lỗi: Không tìm thấy người dùng trong bảng 'wait'.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private int getMaxUserId(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery("SELECT MAX(user_id) AS max_user_id FROM user_infor");
        if (resultSet.next()) {
            return resultSet.getInt("max_user_id");
        }
        return 0;
    }
}

