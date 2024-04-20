package infinity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import java.sql.*;

@SpringBootApplication
@RestController
public class login {

    private final String DB_URL = "jdbc:mysql://127.0.0.1/isd";
    private final String DB_USER = "Kieuhieu2003";
    private final String DB_PASSWORD = "Kieuhieu@2003";

    public static void main(String[] args) {
        SpringApplication.run(login.class, args);
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            if (conn != null) {
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user_infor WHERE user_name = ? AND password = ?");
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet resultSet = stmt.executeQuery();

                if (resultSet.next()) {
                    int userId = resultSet.getInt("user_id");

                    PreparedStatement authorityStmt = conn.prepareStatement("SELECT * FROM authority WHERE user_id = ?");
                    authorityStmt.setInt(1, userId);
                    ResultSet authorityResult = authorityStmt.executeQuery();

                    if (authorityResult.next()) {
                        // Nếu có, trả về 'admin'
                        return "admin";
                    } else {
                        // Ngược lại, trả về 'employee'
                        return "employee";
                    }
                } else {
                    // Trả về 'error' nếu tên người dùng hoặc mật khẩu không đúng
                    return "error";
                }
            } else {
                // Trả về 'error' nếu không kết nối được cơ sở dữ liệu
                return "error";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Trả về 'error' nếu có lỗi xảy ra trong quá trình xử lý cơ sở dữ liệu
            return "error";
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
}
