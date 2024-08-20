package com.linh.identity_service.exception;

import com.linh.identity_service.dto.request.ApiResponse;
import com.nimbusds.jose.JOSEException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.text.ParseException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

//    //default exception
//    @ExceptionHandler(Exception.class)
//    ResponseEntity<ApiResponse> handleDefaultError(Exception e) {
//        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_ERROR;
//
//        return ResponseEntity.badRequest().body(ApiResponse.builder()
//                .code(errorCode.getCode())
//                .message(errorCode.getMessage())
//                .build());
//    }

    @ExceptionHandler(AppException.class)
    ResponseEntity<ApiResponse> handleAppException(AppException e) {
        return ResponseEntity.badRequest().body(ApiResponse.builder()
                .code(e.getErrorCode().getCode())
                .message(e.getErrorCode().getMessage())
                .build());
    }

    @ExceptionHandler(ParseException.class)
    ResponseEntity<ApiResponse> handleParseException(ParseException e) {
        ErrorCode errorCode = ErrorCode.PARSE_ERROR;
        return ResponseEntity.badRequest().body(ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build());
    }

    @ExceptionHandler(JOSEException.class)
    ResponseEntity<ApiResponse> handleParseException(JOSEException e) {
        ErrorCode errorCode = ErrorCode.JOSEE_ERROR;
        return ResponseEntity.badRequest().body(ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build());
    }





    // validation error
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String enumkey = e.getFieldError().getDefaultMessage();
        ErrorCode errorCode = ErrorCode.INVALID_ENUM_VALUE;

        try {
            errorCode = ErrorCode.valueOf(enumkey);
        } catch (IllegalArgumentException ex) {
            log.info("Invalid enum value: {}", enumkey);
        }
        return ResponseEntity.badRequest().body(ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build());
    }
}
