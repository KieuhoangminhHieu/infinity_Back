package infinity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import java.sql.*;
import java.util.Random;

@SpringBootApplication
@RestController
public class LoginController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String DB_URL = "jdbc:mysql://127.0.0.1/isd";
    private final String DB_USER = "Kieuhieu2003";
    private final String DB_PASSWORD = "Kieuhieu@2003";

    public static void main(String[] args) {
        SpringApplication.run(LoginController.class, args);
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
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

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        String confirmPassword = request.getConfirmPassword();
        String email = request.getEmail();

        // Kiểm tra tính hợp lệ của dữ liệu đầu vào
        if (username == null || password == null || confirmPassword == null || email == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vui lòng điền đầy đủ thông tin!");
        }

        if (!password.equals(confirmPassword)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mật khẩu xác nhận không khớp!");
        }

        // Thực hiện thêm người dùng mới vào cơ sở dữ liệu
        try {
            jdbcTemplate.update("INSERT INTO wait (full_name, user_name, password, email) VALUES (?, ?, ?, ?)",
                    request.getFullName(), username, password, email);
            return ResponseEntity.status(HttpStatus.CREATED).body("Đăng ký thành công. Vui lòng đợi admin phê duyệt tài khoản của bạn.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra khi đăng ký!");
        }
    }

    @GetMapping("/success")
    public ResponseEntity<?> success() {
        return ResponseEntity.ok("Đăng ký thành công!");
    }
}

class RegistrationRequest {
    private String fullName;
    private String username;
    private String password;
    private String confirmPassword;
    private String email;

    // Getter and setter for fullName
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // Getter and setter for username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter and setter for password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter and setter for confirmPassword
    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    // Getter and setter for email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String generateRandomString(int length) {
        String characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    // Xử lý yêu cầu POST
    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.OK)
    public String forgotPassword(@RequestBody EmailRequest emailRequest) {
        String email = emailRequest.getEmail();

        // Kiểm tra xem email có tồn tại trong cơ sở dữ liệu hay không
        // Viết mã để thực hiện kiểm tra trong cơ sở dữ liệu ở đây

        // Tạo mã xác thực
        String verificationCode = generateRandomString(6);

        // Gửi mã xác thực qua email (Bạn có thể sử dụng thư viện JavaMail để gửi email)

        return verificationCode;
    }
}

// Class để lưu trữ yêu cầu email
class EmailRequest {
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
