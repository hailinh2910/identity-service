package com.linh.identity_service.configuration;

import com.linh.identity_service.dto.request.IntrospectRequest;
import com.linh.identity_service.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

@Component
public class CustomJwtDecoder implements JwtDecoder {

    @Value("${jwt.signerKey}")
    private String signerKey;

    @Autowired
   // @Lazy break circular of init bean
    AuthenticationService authenticationService;

    NimbusJwtDecoder nimbusJwtDecoder = null;


    @Override
    public Jwt decode(String token) throws JwtException {
        try {
           var isValid = authenticationService.introspect(IntrospectRequest.builder().token(token).build());
            if(!isValid.isValid()){
                throw new BadJwtException("invalid token by expiry time or existed!");
            }
        } catch (JOSEException | ParseException e) {
            throw new JwtException(e.getMessage());
        }
        if(Objects.isNull(nimbusJwtDecoder)){
            nimbusJwtDecoder = NimbusJwtDecoder
                    .withSecretKey( new SecretKeySpec(signerKey.getBytes(),"HS512"))
                    .macAlgorithm(MacAlgorithm.HS512).build();
        }
        return nimbusJwtDecoder.decode(token);
    }
}
