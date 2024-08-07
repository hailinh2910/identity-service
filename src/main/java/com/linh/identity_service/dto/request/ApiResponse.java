package com.linh.identity_service.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.linh.identity_service.exception.ErrorCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
// normalize response
public class ApiResponse<T> {
    private int code = ErrorCode.SUCCESS.getCode();
    private String message;
    private T result;
}
