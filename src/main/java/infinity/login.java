package infinity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@RestController
public class login {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLogin userLogin) {
        String username = userLogin.getUsername();
        String password = userLogin.getPassword();

        String query = "SELECT * FROM user_infor WHERE user_name = ? AND password = ?";
        try {
            User user = jdbcTemplate.queryForObject(query, new Object[]{username, password}, (rs, rowNum) ->
                    new User(rs.getInt("user_id"), rs.getString("user_name")));


            // Nếu người dùng được tìm thấy, trả về dữ liệu người dùng
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            // Nếu không tìm thấy người dùng hoặc có lỗi xảy ra, trả về mã lỗi UNAUTHORIZED
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tên người dùng hoặc mật khẩu không đúng");
        }
    }
}

// Đảm bảo class UserLogin và User được đặt trong các file riêng biệt
class UserLogin {
    private String username;
    private String password;

    // Constructor
    public UserLogin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}


class User {
    private int userId;
    private String userName;

    // Constructor
    public User(int userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    // Getters and setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

