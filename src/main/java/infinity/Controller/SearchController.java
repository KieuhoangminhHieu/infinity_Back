package infinity.Controller;

import infinity.database.DatabaseManager;
import infinity.model.Call;
import infinity.model.CustomerData;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
public class SearchController {

    @GetMapping("/searchCustomer")
    public List<CustomerData> searchCustomerData(
            @RequestParam(required = false) String cusName,
            @RequestParam(required = false) String phoneNumber) {
        List<CustomerData> result = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "SELECT * FROM customer_data WHERE 1=1";
            if (cusName != null) {
                sql += " AND cus_name LIKE ?";
            }
            if (phoneNumber != null) {
                sql += " AND phone_number LIKE ?";
            }
            PreparedStatement statement = connection.prepareStatement(sql);
            int parameterIndex = 1;
            if (cusName != null) {
                statement.setString(parameterIndex++, "%" + cusName + "%");
            }
            if (phoneNumber != null) {
                statement.setString(parameterIndex++, "%" + phoneNumber + "%");
            }
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                CustomerData customerData = new CustomerData();
                customerData.setCusId(resultSet.getString("cus_id"));
                customerData.setCallId(resultSet.getString("call_id"));
                customerData.setStaId(resultSet.getString("sta_id"));
                customerData.setCusName(resultSet.getString("cus_name"));
                customerData.setDatatype(resultSet.getString("datatype")); // Thêm cột mới
                customerData.setPhoneNumber(resultSet.getString("phone_number")); // Thêm cột mới
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
                // Gán giá trị chuỗi từ cột call_date trực tiếp vào thuộc tính callDate
                call.setCallDate(resultSet.getString("call_date"));
                call.setDescription(resultSet.getString("description"));
                call.setDuration(resultSet.getString("duration")); // Sửa thành String
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
