package com.linh.identity_service.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.linh.identity_service.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // null object will be not displayed in response
// normalize response
public class ApiResponse<T> {
    @Builder.Default
    private int code = ErrorCode.SUCCESS.getCode();
    private String message;
    private T result;
}
