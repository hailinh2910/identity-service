package com.linh.identity_service.service;

import com.linh.identity_service.dto.request.AuthenticationRequest;
import com.linh.identity_service.dto.request.IntrospectRequest;
import com.linh.identity_service.dto.response.AuthenticationResponse;
import com.linh.identity_service.dto.response.IntrospectResponse;
import com.linh.identity_service.entity.User;
import com.linh.identity_service.exception.AppException;
import com.linh.identity_service.exception.ErrorCode;
import com.linh.identity_service.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService {

//fDwZgw2+vAe1D85fxOG+VjWofIe9UpM9g8eYyXNhdJ4ELtKxb4jiyn7uDxMXulb2

    @NonFinal
    @Value("${jwt.signerKey}")
    private String SIGNER_KEY;

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return AuthenticationResponse.builder()
                .token(generateToken(user))
                .authenticated(authenticated)
                .build();
    }

    public IntrospectResponse introspect(IntrospectRequest introspectRequest){

        try {
            JWSObject jwsObject = JWSObject.parse(introspectRequest.getToken());

            JWSVerifier verifier = new MACVerifier(SIGNER_KEY);

            JWTClaimsSet claims = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());

            boolean verify =   jwsObject.verify(verifier)
                    && new Date().before(claims.getExpirationTime());
            log.info(String.valueOf(verify));
            return IntrospectResponse.builder()
                    .valid(verify)
                    .build();

        } catch (ParseException | JOSEException e) {
            return IntrospectResponse.builder()
                    .valid(false)
                    .build();
        }


    }


    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512); // header

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder() // body
                .subject(user.getUsername())
                .issuer("hailinh")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .claim("scope",buildScope(user))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);
        try {

            JWSSigner signer = new MACSigner(SIGNER_KEY);

            jwsObject.sign(signer);
        } catch (JOSEException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return jwsObject.serialize();
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if(!CollectionUtils.isEmpty(user.getRoles())) {
        //    user.getRoles().forEach(stringJoiner::add);
        }
        return stringJoiner.toString();
    }
}
