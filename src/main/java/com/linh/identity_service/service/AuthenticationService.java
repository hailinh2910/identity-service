package com.linh.identity_service.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.linh.identity_service.dto.request.AuthenticationRequest;
import com.linh.identity_service.dto.request.IntrospectRequest;
import com.linh.identity_service.dto.request.LogoutRequest;
import com.linh.identity_service.dto.request.RefreshTokenRequest;
import com.linh.identity_service.dto.response.AuthenticationResponse;
import com.linh.identity_service.dto.response.IntrospectResponse;
import com.linh.identity_service.entity.InvalidatedToken;
import com.linh.identity_service.entity.User;
import com.linh.identity_service.exception.AppException;
import com.linh.identity_service.exception.ErrorCode;
import com.linh.identity_service.repository.InvalidatedTokenRepository;
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

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService {

    // fDwZgw2+vAe1D85fxOG+VjWofIe9UpM9g8eYyXNhdJ4ELtKxb4jiyn7uDxMXulb2

    @NonFinal
    @Value("${jwt.signerKey}")
    private String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    private long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    private long REFRESHABLE_DURATION;

    UserRepository userRepository;
    // PasswordEncoder passwordEncoder;
    // circular securityConfig need passwordEncoder to init but -> customJwtdecoder -> AuthenticationService ->
    // passwordEncoder
    InvalidatedTokenRepository invalidatedTokenRepository;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        User user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return AuthenticationResponse.builder()
                .token(generateToken(user))
                .authenticated(authenticated)
                .build();
    }

    public IntrospectResponse introspect(IntrospectRequest introspectRequest) throws JOSEException, ParseException {
        boolean isValid = true;

        try {
            verifyToken(introspectRequest.getToken(), false);
        } catch (AppException e) {
            isValid = false;
        }
        return IntrospectResponse.builder().valid(isValid).build();
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest request) throws ParseException, JOSEException {
        JWSObject jwsObject = verifyToken(request.getToken(), true);
        JWTClaimsSet jwtClaimsSet = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());
        String jti = jwtClaimsSet.getJWTID();
        Date expiryDate = jwtClaimsSet.getExpirationTime();
        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().expiryTime(expiryDate).id(jti).build();
        invalidatedTokenRepository.save(invalidatedToken);

        String username = jwtClaimsSet.getSubject();
        User user =
                userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        String token = generateToken(user);
        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    private JWSObject verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {

        JWSObject jwsObject = JWSObject.parse(token);

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY);

        JWTClaimsSet claims = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());

        Date expiryDate = (isRefresh)
                ? new Date(claims.getIssueTime()
                        .toInstant()
                        .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                        .toEpochMilli())
                : claims.getExpirationTime();

        boolean verify = jwsObject.verify(verifier) && new Date().before(expiryDate);

        if (!verify) throw new AppException(ErrorCode.UNAUTHENTICATED);
        boolean isExisted = invalidatedTokenRepository.existsById(claims.getJWTID());
        if (isExisted) throw new AppException(ErrorCode.UNAUTHENTICATED);

        return jwsObject;
    }

    public void logout(LogoutRequest logoutRequest) throws ParseException, JOSEException {
        try {
            JWSObject jwsObject = verifyToken(logoutRequest.getToken(), true);
            JWTClaimsSet claimsSet = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());
            String jti = claimsSet.getJWTID();
            Date expiryTime = claimsSet.getExpirationTime();
            invalidatedTokenRepository.save(
                    InvalidatedToken.builder().id(jti).expiryTime(expiryTime).build());
        } catch (AppException e) {
            log.info("Token already expired!");
        }
    }

    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512); // header

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder() // body
                .subject(user.getUsername())
                .issuer("hailinh")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
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

        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions())) {
                    role.getPermissions().forEach(permission -> {
                        stringJoiner.add(permission.getName());
                    });
                }

                ;
            });
        }
        ;
        return stringJoiner.toString();
    }
}
