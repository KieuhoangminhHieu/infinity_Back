package infinity.Controller;

import infinity.database.DatabaseManager;
import infinity.model.Call;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@CrossOrigin
@RestController
public class CallController {

    @Autowired
    private DatabaseManager databaseManager;

    @GetMapping("/calls")
    public List<Call> getAllCalls() {
        List<Call> calls = new ArrayList<>();
        int stt = 1; // Khởi tạo số thứ tự

        try (Connection connection = databaseManager.getConnection()) {
            String sql = "SELECT * FROM calls";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Call call = new Call();
                call.setCallId(resultSet.getInt("call_id"));
                call.setPhoneNumber(resultSet.getString("phone_number"));
                call.setCallDate(resultSet.getString("call_date"));
                call.setDescription(resultSet.getString("description"));
                call.setDuration(resultSet.getString("duration"));
                call.setStart(resultSet.getString("start")); // Thêm cột start
                call.setEnd(resultSet.getString("end")); // Thêm cột end
                call.setRecord(resultSet.getString("record")); // Thêm cột record
                call.setUserId(resultSet.getInt("user_id"));
                call.setAuId(resultSet.getInt("au_id"));
                call.setStaId(resultSet.getInt("sta_id"));


                // Thêm cột STT
                call.setStt(stt++); // Gán số thứ tự và tăng giá trị biến đếm
                calls.add(call);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return calls;
    }


    @PostMapping("/calls")
    public Call saveCallToDatabase(@RequestBody Call call) {
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
            String sql = "INSERT INTO calls (call_id, phone_number, call_date, description, duration, start, end, record, user_id, au_id, sta_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, nextId); // Sử dụng giá trị `call_id` mới
            statement.setString(2, call.getPhoneNumber());
            statement.setString(3, call.getCallDate());
            statement.setString(4, call.getDescription());
            statement.setString(5, call.getDuration());
            statement.setString(6, call.getStart());
            statement.setString(7, call.getEnd());
            statement.setString(8, call.getRecord());
            statement.setInt(9, call.getUserId());
            statement.setInt(10, call.getAuId());
            statement.setInt(11, call.getStaId());
            statement.executeUpdate();

            // Set the call_id of the call object
            call.setCallId(nextId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return call;
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
    public Call updateCallInDatabase(@PathVariable long id, @RequestBody Call call) {
        try (Connection connection = databaseManager.getConnection()) {
            String sql = "UPDATE calls SET phone_number = ?, call_date = ?, description = ?, duration = ?, " +
                    "start = ?, end = ?, record = ?, user_id = ?, au_id = ?, sta_id = ? WHERE call_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, call.getPhoneNumber());
            statement.setString(2, call.getCallDate());
            statement.setString(3, call.getDescription());
            statement.setString(4, call.getDuration());
            statement.setString(5, call.getStart());
            statement.setString(6, call.getEnd());
            statement.setString(7, call.getRecord());
            statement.setInt(8, call.getUserId());
            statement.setInt(9, call.getAuId());
            statement.setInt(10, call.getStaId());
            statement.setLong(11, id);
            statement.executeUpdate();

            // Ensure the call_id of the call object is set to the provided id
            call.setCallId((int) id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return call;
    }
}
