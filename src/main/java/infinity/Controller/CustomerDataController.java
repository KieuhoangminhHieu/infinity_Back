package infinity.Controller;

import infinity.database.DatabaseManager;
import infinity.model.CustomerData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
public class CustomerDataController {

    @Autowired
    private DatabaseManager databaseManager;

    @GetMapping("/customer-data")
    public List<CustomerData> getAllCustomerData() {
        List<CustomerData> customerDataList = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection()) {
            String sql = "SELECT * FROM customer_data";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            int stt = 1;
            while (resultSet.next()) {
                CustomerData customerData = new CustomerData();
                // Set STT
                customerData.setStt(stt++);
                customerData.setCusId(resultSet.getString("cus_id"));
                customerData.setCallId(resultSet.getString("call_id"));
                customerData.setStaId(resultSet.getString("sta_id"));
                customerData.setCusName(resultSet.getString("cus_name"));
                customerData.setDatatype(resultSet.getString("datatype")); //
                customerData.setPhoneNumber(resultSet.getString("phone_number"));
                customerDataList.add(customerData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customerDataList;
    }





    @PostMapping("/customer-data")
    public void saveCustomerDataToDatabase(@RequestBody CustomerData customerData) {
        try (Connection connection = databaseManager.getConnection()) {
            String sql = "INSERT INTO customer_data (cus_id, call_id, sta_id, cus_name, datatype, phone_number) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, customerData.getCusId());
            statement.setString(2, customerData.getCallId());
            statement.setString(3, customerData.getStaId());
            statement.setString(4, customerData.getCusName());
            statement.setString(5, customerData.getDatatype()); // Thêm cột mới
            statement.setString(6, customerData.getPhoneNumber()); // Thêm cột mới
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/customer-data/{cusId}")
    public CustomerData getCustomerDataById(@PathVariable String cusId) {
        CustomerData customerData = null;
        try (Connection connection = databaseManager.getConnection()) {
            String sql = "SELECT * FROM customer_data WHERE cus_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, cusId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                customerData = new CustomerData();
                customerData.setCusId(resultSet.getString("cus_id"));
                customerData.setCallId(resultSet.getString("call_id"));
                customerData.setStaId(resultSet.getString("sta_id"));
                customerData.setCusName(resultSet.getString("cus_name"));
                customerData.setDatatype(resultSet.getString("datatype")); // Thêm cột mới
                customerData.setPhoneNumber(resultSet.getString("phone_number")); // Thêm cột mới
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customerData;
    }

    @PutMapping("/customer-data/{cusId}")
    public void updateCustomerDataInDatabase(@PathVariable String cusId, @RequestBody CustomerData customerData) {
        try (Connection connection = databaseManager.getConnection()) {
            String sql = "UPDATE customer_data SET call_id = ?, sta_id = ?, cus_name = ?, datatype = ?, phone_number = ? WHERE cus_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, customerData.getCallId());
            statement.setString(2, customerData.getStaId());
            statement.setString(3, customerData.getCusName());
            statement.setString(4, customerData.getDatatype()); // Thêm cột mới
            statement.setString(5, customerData.getPhoneNumber()); // Thêm cột mới
            statement.setString(6, cusId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @DeleteMapping("/customer-data/{cusId}")
    public void deleteCustomerDataFromDatabase(@PathVariable String cusId) {
        try (Connection connection = databaseManager.getConnection()) {
            String sql = "DELETE FROM customer_data WHERE cus_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, cusId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
