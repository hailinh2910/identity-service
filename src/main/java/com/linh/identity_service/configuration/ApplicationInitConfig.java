package com.linh.identity_service.configuration;

import com.linh.identity_service.entity.User;
import com.linh.identity_service.enums.Role;
import com.linh.identity_service.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
public class ApplicationInitConfig {


    private static final Logger log = LoggerFactory.getLogger(ApplicationInitConfig.class);
    private final PasswordEncoder passwordEncoder;

        @Bean
        ApplicationRunner applicationRunner(UserRepository userRepository){
            return args -> {

                    if(userRepository.findByUsername("admin").isEmpty()){
                        var roles = new HashSet<String>();
                        roles.add(Role.ADMIN.name());
                       User user = User.builder()
                               .username("admin")
                               .password(passwordEncoder.encode("admin"))
                               .roles(roles)
                               .build();
                       userRepository.save(user);
                        log.warn("Default admin user has been created with username {} and password {}. Please change the password!",
                                user.getUsername(), "admin");


                    }
            };
        }
}
