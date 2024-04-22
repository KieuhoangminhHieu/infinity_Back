package infinity.Controller;

import infinity.database.DatabaseManager;
import infinity.model.CustomerData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RestController
public class CustomerDataController {

    @Autowired
    private DatabaseManager databaseManager;

    @PostMapping("/customer-data")
    public void saveCustomerDataToDatabase(@RequestBody CustomerData customerData) {
        try (Connection connection = databaseManager.getConnection()) {
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
        try (Connection connection = databaseManager.getConnection()) {
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
        try (Connection connection = databaseManager.getConnection()) {
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
        try (Connection connection = databaseManager.getConnection()) {
            String sql = "DELETE FROM customer_data WHERE cus_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, cusId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
