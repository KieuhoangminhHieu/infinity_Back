package infinity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import javax.sql.DataSource;
import java.sql.*;
import java.util.Date;

@SpringBootApplication
@RestController
public class AdminManagement {

    @Autowired
    private DataSource dataSource;

    public static void main(String[] args) {
        SpringApplication.run(AdminManagement.class, args);
    }

    private static final String URL = "jdbc:mysql://localhost:3306/isd";
    private static final String USERNAME = "Kieuhieu2003";
    private static final String PASSWORD = "Kieuhieu@2003";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    @PostMapping("/calls")
    public void saveCallToDatabase(@RequestBody Call call) {
        try (Connection connection = getConnection()) {
            String sql = "INSERT INTO calls (phone_number, call_date, description, duration, user_id, au_id, sta_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, call.getPhoneNumber());
            statement.setTimestamp(2, new java.sql.Timestamp(call.getCallDate().getTime()));
            statement.setString(3, call.getDescription());
            statement.setInt(4, call.getDuration());
            statement.setInt(5, call.getUserId());
            statement.setInt(6, call.getAuId());
            statement.setInt(7, call.getStaId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @DeleteMapping("/calls/{id}")
    public void deleteCallFromDatabase(@PathVariable long id) {
        try (Connection connection = getConnection()) {
            String sql = "DELETE FROM calls WHERE call_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @PutMapping("/calls/{id}")
    public void updateCallInDatabase(@PathVariable long id, @RequestBody Call call) {
        try (Connection connection = getConnection()) {
            String sql = "UPDATE calls SET phone_number = ?, call_date = ?, description = ?, duration = ?, " +
                    "user_id = ?, au_id = ?, sta_id = ? WHERE call_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, call.getPhoneNumber());
            statement.setTimestamp(2, new java.sql.Timestamp(call.getCallDate().getTime()));
            statement.setString(3, call.getDescription());
            statement.setInt(4, call.getDuration());
            statement.setInt(5, call.getUserId());
            statement.setInt(6, call.getAuId());
            statement.setInt(7, call.getStaId());
            statement.setLong(8, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    class Call {
        private String phoneNumber;
        private Date callDate;
        private String description;
        private int duration;
        private int userId;
        private int auId;
        private int staId;

        // Các getter và setter
        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public Date getCallDate() {
            return callDate;
        }

        public void setCallDate(Date callDate) {
            this.callDate = callDate;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getAuId() {
            return auId;
        }

        public void setAuId(int auId) {
            this.auId = auId;
        }

        public int getStaId() {
            return staId;
        }

        public void setStaId(int staId) {
            this.staId = staId;
        }
    }
    @PostMapping("/customer-data")
    public void saveCustomerDataToDatabase(@RequestBody CustomerData customerData) {
        try (Connection connection = getConnection()) {
            String sql = "INSERT INTO customer_data (cus_id, call_id, sta_id, cus_name) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, customerData.getCusId());
            statement.setInt(2, customerData.getCallId());
            statement.setInt(3, customerData.getStaId());
            statement.setString(4, customerData.getCusName());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/customer-data/{cusId}")
    public CustomerData getCustomerDataById(@PathVariable int cusId) {
        CustomerData customerData = null;
        try (Connection connection = getConnection()) {
            String sql = "SELECT * FROM customer_data WHERE cus_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, cusId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                customerData = new CustomerData();
                customerData.setCusId(resultSet.getInt("cus_id"));
                customerData.setCallId(resultSet.getInt("call_id"));
                customerData.setStaId(resultSet.getInt("sta_id"));
                customerData.setCusName(resultSet.getString("cus_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customerData;
    }

    @PutMapping("/customer-data/{cusId}")
    public void updateCustomerDataInDatabase(@PathVariable int cusId, @RequestBody CustomerData customerData) {
        try (Connection connection = getConnection()) {
            String sql = "UPDATE customer_data SET call_id = ?, sta_id = ?, cus_name = ? WHERE cus_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, customerData.getCallId());
            statement.setInt(2, customerData.getStaId());
            statement.setString(3, customerData.getCusName());
            statement.setInt(4, cusId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @DeleteMapping("/customer-data/{cusId}")
    public void deleteCustomerDataFromDatabase(@PathVariable int cusId) {
        try (Connection connection = getConnection()) {
            String sql = "DELETE FROM customer_data WHERE cus_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, cusId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    class CustomerData {
        private int cusId;
        private int callId;
        private int staId;
        private String cusName;

        // Các getter và setter
        public int getCusId() {
            return cusId;
        }

        public void setCusId(int cusId) {
            this.cusId = cusId;
        }

        public int getCallId() {
            return callId;
        }

        public void setCallId(int callId) {
            this.callId = callId;
        }

        public int getStaId() {
            return staId;
        }

        public void setStaId(int staId) {
            this.staId = staId;
        }

        public String getCusName() {
            return cusName;
        }

        public void setCusName(String cusName) {
            this.cusName = cusName;
        }
    }
}