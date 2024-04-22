package infinity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import javax.sql.DataSource;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

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
        private int callId;
        private String phoneNumber;
        private Date callDate;
        private String description;
        private int duration;
        private int userId;
        private int auId;
        private int staId;

        // Các getter và setter
        public int getCallId() {
            return callId;
        }

        public void setCallId(int callId) {
            this.callId = callId;
        }
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
    @GetMapping("/searchCustomer")
    public List<CustomerData> searchCustomerData(@RequestParam String cusName) {
        List<CustomerData> result = new ArrayList<>();
        try (Connection connection = getConnection()) {
            String sql = "SELECT * FROM customer_data WHERE cus_name LIKE ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, "%" + cusName + "%");

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                CustomerData customerData = new CustomerData();
                customerData.setCusId(resultSet.getInt("cus_id"));
                customerData.setCallId(resultSet.getInt("call_id"));
                customerData.setStaId(resultSet.getInt("sta_id"));
                customerData.setCusName(resultSet.getString("cus_name"));
                result.add(customerData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    @GetMapping("/searchCall")
    public List<Call> searchCallsByPhoneNumber(@RequestParam String phoneNumber) {
        List<Call> result = new ArrayList<>();
        try (Connection connection = getConnection()) {
            String sql = "SELECT * FROM calls WHERE phone_number = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, phoneNumber);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Call call = new Call();
                call.setCallId(resultSet.getInt("call_id"));
                call.setPhoneNumber(resultSet.getString("phone_number"));
                call.setCallDate(resultSet.getDate("call_date"));
                call.setDescription(resultSet.getString("description"));
                call.setDuration(resultSet.getInt("duration"));
                call.setUserId(resultSet.getInt("user_id"));
                call.setAuId(resultSet.getInt("au_id"));
                call.setStaId(resultSet.getInt("sta_id"));
                result.add(call);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    @GetMapping("/filter-calls")
    public List<Call> filterCalls(@RequestParam(required = false) Integer userId,
                                  @RequestParam(required = false) Integer statusId) {
        List<Call> result = new ArrayList<>();
        try (Connection connection = getConnection()) {
            StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM calls WHERE 1=1");

            if (userId != null) {
                sqlBuilder.append(" AND user_id = ").append(userId);
            }

            if (statusId != null) {
                sqlBuilder.append(" AND sta_id = ").append(statusId);
            }

            String sql = sqlBuilder.toString();
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Call call = new Call();
                call.setCallId(resultSet.getInt("call_id"));
                call.setPhoneNumber(resultSet.getString("phone_number"));
                call.setCallDate(resultSet.getDate("call_date"));
                call.setDescription(resultSet.getString("description"));
                call.setDuration(resultSet.getInt("duration"));
                call.setUserId(resultSet.getInt("user_id"));
                call.setAuId(resultSet.getInt("au_id"));
                call.setStaId(resultSet.getInt("sta_id"));
                result.add(call);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    @GetMapping("/filter-customer-data")
    public List<CustomerData> filterCustomerData(@RequestParam(required = false) Integer userId,
                                                 @RequestParam(required = false) Integer statusId) {
        List<CustomerData> result = new ArrayList<>();
        try (Connection connection = getConnection()) {
            StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM customer_data WHERE 1=1");

            if (userId != null) {
                sqlBuilder.append(" AND user_id = ").append(userId);
            }

            if (statusId != null) {
                sqlBuilder.append(" AND sta_id = ").append(statusId);
            }

            String sql = sqlBuilder.toString();
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                CustomerData customerData = new CustomerData();
                customerData.setCusId(resultSet.getInt("cus_id"));
                customerData.setCallId(resultSet.getInt("call_id"));
                customerData.setStaId(resultSet.getInt("sta_id"));
                customerData.setCusName(resultSet.getString("cus_name"));
                result.add(customerData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
