package com.linh.identity_service.controller;

import com.linh.identity_service.dto.request.ApiResponse;
import com.linh.identity_service.dto.request.AuthenticationRequest;
import com.linh.identity_service.dto.request.IntrospectRequest;
import com.linh.identity_service.dto.request.LogoutRequest;
import com.linh.identity_service.dto.response.AuthenticationResponse;
import com.linh.identity_service.dto.response.IntrospectResponse;
import com.linh.identity_service.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class AuthenticationController {
        AuthenticationService authenticationService;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {

        AuthenticationResponse result =  authenticationService.authenticate(authenticationRequest);
       return ApiResponse.<AuthenticationResponse>builder()
               .result(result)
               .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody  IntrospectRequest introspectRequest) throws ParseException, JOSEException {

        IntrospectResponse introspectResponse = authenticationService.introspect(introspectRequest);
        return ApiResponse.<IntrospectResponse>builder()
                .result(introspectResponse)
                .build();
    }
    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest logoutRequest) throws ParseException, JOSEException {
        authenticationService.logout(logoutRequest);
        return  ApiResponse.<Void>builder()
                .build();
    }

}
