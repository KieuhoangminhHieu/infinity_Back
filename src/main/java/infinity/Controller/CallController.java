package infinity.Controller;

import infinity.database.DatabaseManager;
import infinity.model.Call;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RestController
public class CallController {

    @Autowired
    private DatabaseManager databaseManager;

    @PostMapping("/calls")
    public void saveCallToDatabase(@RequestBody Call call) {
        try (Connection connection = databaseManager.getConnection()) {
            // Lấy `call_id` lớn nhất từ bảng `calls`
            String getMaxIdQuery = "SELECT MAX(call_id) FROM calls";
            PreparedStatement getMaxIdStatement = connection.prepareStatement(getMaxIdQuery);
            ResultSet resultSet = getMaxIdStatement.executeQuery();
            int nextId = 1; // Giá trị mặc định nếu không có bản ghi nào trong bảng
            if (resultSet.next()) {
                nextId = resultSet.getInt(1) + 1;
            }

            // Chèn bản ghi mới với `call_id` được xác định
            String sql = "INSERT INTO calls (call_id, phone_number, call_date, description, duration, user_id, au_id, sta_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, nextId); // Sử dụng giá trị `call_id` mới
            statement.setString(2, call.getPhoneNumber());
            statement.setTimestamp(3, new java.sql.Timestamp(call.getCallDate().getTime()));
            statement.setString(4, call.getDescription());
            statement.setInt(5, call.getDuration());
            statement.setInt(6, call.getUserId());
            statement.setInt(7, call.getAuId());
            statement.setInt(8, call.getStaId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @DeleteMapping("/calls/{id}")
    public void deleteCallFromDatabase(@PathVariable long id) {
        try (Connection connection = databaseManager.getConnection()) {
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
        try (Connection connection = databaseManager.getConnection()) {
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

    @GetMapping("/calls/{id}")
    public Call getCallById(@PathVariable long id) {
        Call call = null;
        try (Connection connection = databaseManager.getConnection()) {
            String sql = "SELECT * FROM calls WHERE call_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                call = new Call();
                call.setCallId(resultSet.getInt("call_id"));
                call.setPhoneNumber(resultSet.getString("phone_number"));
                call.setCallDate(resultSet.getTimestamp("call_date"));
                call.setDescription(resultSet.getString("description"));
                call.setDuration(resultSet.getInt("duration"));
                call.setUserId(resultSet.getInt("user_id"));
                call.setAuId(resultSet.getInt("au_id"));
                call.setStaId(resultSet.getInt("sta_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return call;
    }
}
