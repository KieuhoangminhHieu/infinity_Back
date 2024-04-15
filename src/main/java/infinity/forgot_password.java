package infinity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import java.util.Random;

@SpringBootApplication
@RestController
public class forgot_password{

    // Khởi tạo ứng dụng Spring Boot
    public static void main(String[] args) {
        SpringApplication.run(forgot_password.class, args);
    }

    // Hàm tạo mã xác thực
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
