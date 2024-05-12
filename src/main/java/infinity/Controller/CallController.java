package infinity.Controller;

import infinity.database.DatabaseManager;
import infinity.model.Call;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.io.ByteArrayOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
            String sql = "INSERT INTO calls (call_id, phone_number, call_date, description, duration, start, end, record, user_id, au_id, sta_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, nextId); // Sử dụng giá trị `call_id` mới
            statement.setString(2, call.getPhoneNumber());
            statement.setString(3, call.getCallDate());
            statement.setString(4, call.getDescription());
            statement.setString(5, call.getDuration()); // Cập nhật thành kiểu String
            statement.setString(6, call.getStart()); // Thêm cột start
            statement.setString(7, call.getEnd()); // Thêm cột end
            statement.setString(8, call.getRecord()); // Thêm cột record
            statement.setInt(9, call.getUserId());
            statement.setInt(10, call.getAuId());
            statement.setInt(11, call.getStaId());
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
                    "start = ?, end = ?, record = ?, user_id = ?, au_id = ?, sta_id = ? WHERE call_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, call.getPhoneNumber());
            statement.setString(2, call.getCallDate());
            statement.setString(3, call.getDescription());
            statement.setString(4, call.getDuration()); // Cập nhật thành kiểu String
            statement.setString(5, call.getStart()); // Thêm cột start
            statement.setString(6, call.getEnd()); // Thêm cột end
            statement.setString(7, call.getRecord()); // Thêm cột record
            statement.setInt(8, call.getUserId());
            statement.setInt(9, call.getAuId());
            statement.setInt(10, call.getStaId());
            statement.setLong(11, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @GetMapping("/calls/export/excel")
    public ResponseEntity<byte[]> exportToExcel() {
        try (Connection connection = databaseManager.getConnection()) {
            String sql = "SELECT * FROM calls";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            // Tạo workbook mới
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Calls");

            // Tạo header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Call ID");
            headerRow.createCell(1).setCellValue("Phone Number");
            headerRow.createCell(2).setCellValue("Call Date");
            headerRow.createCell(3).setCellValue("Description");
            headerRow.createCell(4).setCellValue("Duration");
            headerRow.createCell(5).setCellValue("Start");
            headerRow.createCell(6).setCellValue("End");
            headerRow.createCell(7).setCellValue("Record");
            headerRow.createCell(8).setCellValue("User ID");
            headerRow.createCell(9).setCellValue("AU ID");
            headerRow.createCell(10).setCellValue("STA ID");

            // Đổ dữ liệu từ ResultSet vào workbook
            int rowNum = 1;
            while (resultSet.next()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(resultSet.getInt("call_id"));
                row.createCell(1).setCellValue(resultSet.getString("phone_number"));
                row.createCell(2).setCellValue(resultSet.getString("call_date"));
                row.createCell(3).setCellValue(resultSet.getString("description"));
                row.createCell(4).setCellValue(resultSet.getString("duration"));
                row.createCell(5).setCellValue(resultSet.getString("start"));
                row.createCell(6).setCellValue(resultSet.getString("end"));
                row.createCell(7).setCellValue(resultSet.getString("record"));
                row.createCell(8).setCellValue(resultSet.getInt("user_id"));
                row.createCell(9).setCellValue(resultSet.getInt("au_id"));
                row.createCell(10).setCellValue(resultSet.getInt("sta_id"));
            }

            // Ghi workbook vào ByteArrayOutputStream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            workbook.close();

            // Thiết lập HttpHeaders
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=calls.xlsx");

            // Trả về ResponseEntity chứa byte array của file Excel và HttpHeaders
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(org.springframework.http.MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(baos.toByteArray());

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
