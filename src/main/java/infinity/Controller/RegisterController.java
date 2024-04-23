package infinity.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.*;

@CrossOrigin
@RestController
public class RegisterController {

    private final DataSource dataSource;

    public RegisterController(DataSource dataSource) {
        this.dataSource = dataSource;
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
        try (Connection conn = dataSource.getConnection()) {
            if (conn != null) {
                try (PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO wait (full_name, user_name, password, email) VALUES (?, ?, ?, ?)")) {
                    insertStmt.setString(1, request.getFullName());
                    insertStmt.setString(2, username);
                    insertStmt.setString(3, password);
                    insertStmt.setString(4, email);
                    insertStmt.executeUpdate();
                }
                return ResponseEntity.status(HttpStatus.CREATED).body("Đăng ký thành công. Vui lòng đợi admin phê duyệt tài khoản của bạn.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể kết nối đến cơ sở dữ liệu!");
            }
        } catch (SQLException e) {
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
}

