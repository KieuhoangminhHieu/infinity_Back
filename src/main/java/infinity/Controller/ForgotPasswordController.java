package infinity.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Random;

@RestController
public class ForgotPasswordController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> forgotPassword(@RequestBody EmailRequest emailRequest) {
        String email = emailRequest.getEmail();

        // Kiểm tra xem email có tồn tại trong cơ sở dữ liệu hay không
        String query = "SELECT COUNT(*) FROM user_infor WHERE email = ?";
        int count;
        try {
            count = jdbcTemplate.queryForObject(query, new Object[]{email}, Integer.class);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi truy vấn cơ sở dữ liệu");
        }

        if (count > 0) {
            // Email tồn tại trong cơ sở dữ liệu
            // Tiếp tục với quá trình gửi mã xác thực và xử lý khôi phục mật khẩu
            // Tạo mã xác thực
            String verificationCode = generateRandomString(6);

            // Gửi mã xác thực qua email
            if (sendEmail(email, verificationCode)) {
                return ResponseEntity.ok("Mã xác thực đã được gửi đến email");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi gửi email");
            }
        } else {
            // Email không tồn tại trong cơ sở dữ liệu
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email không tồn tại trong hệ thống!");
        }
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

    public boolean sendEmail(String recipient, String verificationCode) {
        // Thông tin đăng nhập vào tài khoản Gmail
        final String username = "kieuphuonghoai@gmail.com"; // Địa chỉ email của bạn
        final String password = "xtjq xihj ornt ftgy"; // Mật khẩu của bạn

        // Cấu hình thông tin email
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Tạo session
        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            // Tạo message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject("Verification Code for Password Reset");
            message.setText("Your verification code is: " + verificationCode);

            // Gửi email
            Transport.send(message);

            System.out.println("Email sent successfully!");
            return true;

        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
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
