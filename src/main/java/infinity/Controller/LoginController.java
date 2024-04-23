package infinity.Controller;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.*;

@CrossOrigin
@SpringBootApplication
@RestController
public class LoginController {

    private final DataSource dataSource;

    public LoginController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
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
                            String fullName = resultSet.getString("full_name");
                            String authority = resultSet.getString("authority");
                            // Kiểm tra quyền của người dùng
                            if (authority != null && authority.equals("admin")) {
                                return new LoginResponse("admin", userId, fullName);
                            } else {
                                return new LoginResponse("employee", userId, fullName);
                            }
                        } else {
                            // Trả về 'error' nếu tên người dùng hoặc mật khẩu không đúng
                            return new LoginResponse("error", -1, null);
                        }
                    }
                }
            } else {
                // Trả về 'error' nếu không kết nối được cơ sở dữ liệu
                return new LoginResponse("error", -1, null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Trả về 'error' nếu có lỗi xảy ra trong quá trình xử lý cơ sở dữ liệu
            return new LoginResponse("error", -1, null);
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

    public static class LoginResponse {
        private String role;
        private int userId;
        private String userName;

        public LoginResponse(String role, int userId, String userName) {
            this.role = role;
            this.userId = userId;
            this.userName = userName;
        }

        // Getter cho role
        public String getRole() {
            return role;
        }

        // Getter cho user_id
        public int getUserId() {
            return userId;
        }

        // Getter cho user_name
        public String getUserName() {
            return userName;
        }
    }
}
