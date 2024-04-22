package infinity.Controller;

import infinity.database.DatabaseManager;
import infinity.model.Call;
import infinity.model.CustomerData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class SearchController {

    @GetMapping("/searchCustomer")
    public List<CustomerData> searchCustomerData(@RequestParam String cusName) {
        List<CustomerData> result = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection()) {
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
        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "SELECT * FROM calls WHERE phone_number = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, phoneNumber);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Call call = new Call();
                call.setCallId(resultSet.getInt("call_id"));
                call.setPhoneNumber(resultSet.getString("phone_number"));
                // Sử dụng getDate() để lấy giá trị ngày từ cột call_date
                java.sql.Timestamp callTimestamp = resultSet.getTimestamp("call_date");

                call.setCallDate(callTimestamp);
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

    // Các phương thức tìm kiếm khác
}
