package com.linh.identity_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    SUCCESS(1000, "Success", HttpStatus.OK),
    USER_EXISTED(1001, "User existed",HttpStatus.BAD_REQUEST),
    USERNAME_ERROR(1002, "Username must be at least 3 characters",HttpStatus.BAD_REQUEST),
    PASSWORD_ERROR(1003, "Password must be at least 8 characters",HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1004, "User not existed",HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1005, "Unauthenticated",HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),

    JOSEE_ERROR(9996, "JOSE Exception",HttpStatus.BAD_REQUEST),
    PARSE_ERROR(9997, "Parse Exception",HttpStatus.BAD_REQUEST),
    INVALID_ENUM_VALUE(9998, "Invalid enum value",HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_ERROR(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR);


    ErrorCode(int code, String message,HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }


    private final int code;
    private final String message;
    private final HttpStatusCode httpStatusCode;




}
