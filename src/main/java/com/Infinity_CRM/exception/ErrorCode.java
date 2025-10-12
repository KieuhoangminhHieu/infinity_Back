package com.Infinity_CRM.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // USER
    USER_NOT_FOUND(1004, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    USER_EXISTED(1001, "Người dùng đã tồn tại", HttpStatus.CONFLICT),
    USERNAME_INVALID(1002, "Tên người dùng phải có ít nhất 3 ký tự", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1003, "Mật khẩu phải có ít nhất 8 ký tự", HttpStatus.BAD_REQUEST),
    INVALID_DOB(1011, "Tuổi phải từ 18 trở lên", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(1012, "Email không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_PHONE_NUMBER(1013, "Số điện thoại không hợp lệ", HttpStatus.BAD_REQUEST),

    // AUTH
    UNAUTHORIZED(1010, "Bạn không có quyền truy cập", HttpStatus.FORBIDDEN),
    UNAUTHENTICATED(1019, "Bạn chưa đăng nhập", HttpStatus.UNAUTHORIZED),

    // SYSTEM
    INVALID_KEY(1000, "Khóa không hợp lệ", HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_EXISTED(999, "Lỗi hệ thống chưa xác định", HttpStatus.INTERNAL_SERVER_ERROR);

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
