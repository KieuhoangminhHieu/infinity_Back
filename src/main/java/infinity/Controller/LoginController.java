package infinity.Controller;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.*;

@SpringBootApplication
@RestController
public class LoginController {

    private final DataSource dataSource;

    public LoginController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        // Thực hiện xác thực trong cơ sở dữ liệu và trả về kết quả tương ứng
        try (Connection conn = dataSource.getConnection()) {
            if (conn != null) {
                try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user_infor WHERE user_name = ? AND password = ?")) {
                    stmt.setString(1, username);
                    stmt.setString(2, password);
                    try (ResultSet resultSet = stmt.executeQuery()) {
                        if (resultSet.next()) {
                            int userId = resultSet.getInt("user_id");
                            try (PreparedStatement authorityStmt = conn.prepareStatement("SELECT * FROM authority WHERE user_id = ?")) {
                                authorityStmt.setInt(1, userId);
                                try (ResultSet authorityResult = authorityStmt.executeQuery()) {
                                    if (authorityResult.next()) {
                                        // Nếu có, trả về 'admin'
                                        return "admin";
                                    } else {
                                        // Ngược lại, trả về 'employee'
                                        return "employee";
                                    }
                                }
                            }
                        } else {
                            // Trả về 'error' nếu tên người dùng hoặc mật khẩu không đúng
                            return "error";
                        }
                    }
                }
            } else {
                // Trả về 'error' nếu không kết nối được cơ sở dữ liệu
                return "error";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Trả về 'error' nếu có lỗi xảy ra trong quá trình xử lý cơ sở dữ liệu
            return "error";
        }
    }
    public static class LoginRequest {
        private String username;
        private String password;

        // Getter và setter cho username
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        // Getter và setter cho password
        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }


}




