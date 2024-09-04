package com.linh.identity_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.linh.identity_service.dto.request.UserCreationRequest;
import com.linh.identity_service.dto.response.UserResponse;
import com.linh.identity_service.entity.User;
import com.linh.identity_service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc // create mock request
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserCreationRequest userCreationRequest;
    private UserResponse userResponse;
    private LocalDate dob;
    @BeforeEach
    public void setUp() {
        dob = LocalDate.of(1990,1,1);
        userCreationRequest = UserCreationRequest.builder()
                .username("john")
                .firstName("John")
                .lastName("Doe")
                .password("12345678")
                .dob(dob)
                .build();
        userResponse  = UserResponse.builder()
                .id("cf321032132")
                .username("john")
                .firstName("John")
                .lastName("Doe")
                .dob(dob)
                .build();
    }

    @Test
    void createUser_validRequest_success() throws Exception {
        //GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // pom.xml. support convert local date to json
        String content = objectMapper.writeValueAsString(userCreationRequest);

        Mockito.when(userService.createUser(Mockito.any())).thenReturn(userResponse);

        //WHEN,THEN
         mockMvc.perform(MockMvcRequestBuilders.post("/users")
                         .contentType(MediaType.APPLICATION_JSON_VALUE)
                         .content(content))
                 .andExpect(MockMvcResultMatchers.status().isOk())       //THEN
                 .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
                 .andExpect(MockMvcResultMatchers.jsonPath("result.id").value("cf321032132"));//THEN
    }


   //catch valid of username     @Size(min = 4, message = "USERNAME_ERROR")
    @Test
    void createUser_validRequest_fail() throws Exception {
        //GIVEN
        userCreationRequest.setUsername("joh");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // pom.xml. support convert local date to json
        String content = objectMapper.writeValueAsString(userCreationRequest);

        // Mockito.when(userService.createUser(Mockito.any())).thenReturn(userResponse); catch valid so no need
        // because  it catches at validation  framework layer

        //WHEN,THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())       //THEN
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1002))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Username must be at least 4 characters"));//THEN
    }
}
