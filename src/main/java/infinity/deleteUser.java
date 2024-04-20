package infinity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
@SpringBootApplication
@RestController
public class deleteUser {
    private static final String DB_URL = "jdbc:mysql://localhost/isd";
    private static final String DB_USER = "Kieuhieu2003";
    private static final String DB_PASSWORD = "Kieuhieu@2003";

    public static void main(String[] args) {
        SpringApplication.run(deleteUser.class, args);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") int userId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Tạo và thực thi truy vấn xóa người dùng từ cơ sở dữ liệu
            String query = "DELETE FROM wait WHERE user_id = " + userId;
            try (Statement stmt = conn.createStatement()) {
                int rowsAffected = stmt.executeUpdate(query);
                if (rowsAffected > 0) {
                    return ResponseEntity.ok("Người dùng đã được xóa thành công");
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng có id " + userId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Xóa người dùng không thành công");
        }
    }
}
