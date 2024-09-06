//package com.linh.identity_service.controller;
//
//import java.time.LocalDate;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.testcontainers.containers.MySQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.linh.identity_service.dto.request.UserCreationRequest;
//import com.linh.identity_service.dto.response.UserResponse;
//import com.linh.identity_service.entity.Role;
//import com.linh.identity_service.repository.RoleRepository;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@SpringBootTest
//@AutoConfigureMockMvc
//@Testcontainers
//public class UserControllerIntegrationTest {
//    // ERROR
//    @Container
//    static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:latest");
//
//    @DynamicPropertySource
//    static void configureDataSource(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
//        registry.add("spring.datasource.username", mysqlContainer::getUsername);
//        registry.add("spring.datasource.password", mysqlContainer::getPassword);
//        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
//        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
//    }
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    private UserCreationRequest userCreationRequest;
//    private UserResponse userResponse;
//    private LocalDate dob;
//
//    @BeforeEach
//    public void setUp() {
//        // Ensure role exists
//        if (!roleRepository.existsById("USER")) {
//            Role userRole = new Role();
//            userRole.setName("USER");
//            roleRepository.save(userRole);
//        }
//
//        dob = LocalDate.of(1990, 1, 1);
//        userCreationRequest = UserCreationRequest.builder()
//                .username("john")
//                .firstName("John")
//                .lastName("Doe")
//                .password("12345678")
//                .dob(dob)
//                .build();
//
//        userResponse = UserResponse.builder()
//                .id("cf321032132")
//                .username("john")
//                .firstName("John")
//                .lastName("Doe")
//                .dob(dob)
//                .build();
//    }
//
//    @Test
//    void createUser_validRequest_success() throws Exception {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//        String content = objectMapper.writeValueAsString(userCreationRequest);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/users")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(content))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
//                .andExpect(MockMvcResultMatchers.jsonPath("result.id").value("cf321032132"));
//    }
//
//    @Test
//    void createUser_validRequest_fail() throws Exception {
//        userCreationRequest.setUsername("joh");
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//        String content = objectMapper.writeValueAsString(userCreationRequest);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/users")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(content))
//                .andExpect(MockMvcResultMatchers.status().isBadRequest())
//                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1002))
//                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Username must be at least 4 characters"));
//    }
//}
