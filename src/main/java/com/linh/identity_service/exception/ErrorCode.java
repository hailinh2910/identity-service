package com.linh.identity_service.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SUCCESS(1000, "Success"),
    USER_EXISTED(1001, "User existed"),
    USER_NOT_FOUND(1002, "User not found"),
    USERNAME_ERROR(1003, "Username must be at least 3 characters"),
    PASSWORD_ERROR(1004, "Password must be at least 8 characters"),
    USER_NOT_EXISTED(1005, "User not existed"),
    UNAUTHENTICATED(1006, "Unauthenticated"),


    JOSEE_ERROR(9996, "JOSE Exception"),
    PARSE_ERROR(9997, "Parse Exception"),
    INVALID_ENUM_VALUE(9998, "Invalid enum value"),
    UNCATEGORIZED_ERROR(9999, "Uncategorized error");


    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
