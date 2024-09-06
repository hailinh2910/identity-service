package com.linh.identity_service.configuration;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.linh.identity_service.entity.Role;
import com.linh.identity_service.entity.User;
import com.linh.identity_service.repository.PermissionRepository;
import com.linh.identity_service.repository.RoleRepository;
import com.linh.identity_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ApplicationInitConfig {

    private static final Logger log = LoggerFactory.getLogger(ApplicationInitConfig.class);
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            name = "datasource.driver-class-name",
            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                Set<Role> roles = new HashSet<>();
                Role adminRole = new Role();
                // khi set khóa chính. thay vì new thì JPA/Hibernate will auto check if primary key already
                // existed then get this entity
                adminRole.setName("ADMIN"); // ADMIN role da ton tai, -> lay ra object Role trong Database
                roles.add(adminRole);

                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .roles(roles)
                        .build();
                userRepository.save(user);
                log.warn(
                        "Default admin user has been created with username {} and password {}. Please change the password!",
                        user.getUsername(),
                        "admin");
            }
        };
    }
}
