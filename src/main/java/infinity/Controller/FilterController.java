package infinity.Controller;

import infinity.database.DatabaseManager;
import infinity.model.Call;
import infinity.model.CustomerData;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class FilterController {

    @GetMapping("/filter-calls")
    public List<Call> filterCalls(@RequestParam(required = false) Integer userId,
                                  @RequestParam(required = false) Integer statusId) {
        List<Call> result = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "SELECT * FROM calls WHERE 1=1";
            if (userId != null) {
                sql += " AND user_id = ?";
            }
            if (statusId != null) {
                sql += " AND sta_id = ?";
            }
            PreparedStatement statement = connection.prepareStatement(sql);
            int parameterIndex = 1;
            if (userId != null) {
                statement.setInt(parameterIndex++, userId);
            }
            if (statusId != null) {
                statement.setInt(parameterIndex++, statusId);
            }
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Call call = new Call();
                call.setCallId(resultSet.getInt("call_id"));
                call.setPhoneNumber(resultSet.getString("phone_number"));
                // Gán giá trị chuỗi từ cột call_date trực tiếp vào thuộc tính callDate
                call.setCallDate(resultSet.getString("call_date"));
                call.setDescription(resultSet.getString("description"));
                call.setDuration(resultSet.getString("duration"));
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
    public List<CustomerData> filterCustomerData(@RequestParam(required = false) Integer statusId) {
        List<CustomerData> result = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "SELECT * FROM customer_data WHERE 1=1";
            if (statusId != null) {
                sql += " AND sta_id = ?";
            }
            PreparedStatement statement = connection.prepareStatement(sql);
            if (statusId != null) {
                statement.setInt(1, statusId);
            }
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                CustomerData customerData = new CustomerData();
                customerData.setCusId(resultSet.getString("cus_id"));
                customerData.setCallId(resultSet.getString("call_id"));
                customerData.setStaId(resultSet.getString("sta_id"));
                customerData.setCusName(resultSet.getString("cus_name"));
                result.add(customerData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

}

