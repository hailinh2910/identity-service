package com.linh.identity_service.service;

import com.linh.identity_service.dto.request.UserCreationRequest;
import com.linh.identity_service.dto.response.UserResponse;
import com.linh.identity_service.entity.Role;
import com.linh.identity_service.entity.User;
import com.linh.identity_service.exception.AppException;
import com.linh.identity_service.repository.RoleRepository;
import com.linh.identity_service.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource("/test.properties")
public class UserServiceTest {

    @Autowired
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    RoleRepository roleRepository;



    private UserCreationRequest userCreationRequest;
    private UserResponse userResponse;
    private User user;
    private Role role;

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

        user = User.builder()
                .id("cf321032132")
                .username("john")
                .firstName("John")
                .lastName("Doe")
                .dob(dob)
                .build();

        role = Role.builder()
                .build();
    }


    @Test
    void createUser_validRequest_success(){
         //GIVEN
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);
        when(roleRepository.findById(any())).thenReturn(Optional.ofNullable(role));

        //WHEN
        UserResponse userResponse = userService.createUser(userCreationRequest);

        //THEN
        Assertions.assertEquals(userResponse.getId(), "cf321032132");
        Assertions.assertEquals(userResponse.getUsername(), "john");

    }


    @Test
    void createUser_userExisted_fail(){
        //GIVEN
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        //WHEN
        AppException response = Assertions.assertThrows(AppException.class, () -> userService.createUser(userCreationRequest));

        //THEN
        Assertions.assertEquals(response.getErrorCode().getCode(),1001);
        Assertions.assertEquals(response.getMessage(), "User existed");
    }


    @Test
    void createUser_roleNotFound_fail(){
        //GIVEN
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(roleRepository.findById("USER")).thenReturn(Optional.empty());
        //WHEN
        AppException thrownException = Assertions
                .assertThrows(AppException.class, () -> userService.createUser(userCreationRequest));

        //THEN
        Assertions.assertEquals(thrownException.getErrorCode().getCode(),1008);
        Assertions.assertEquals(thrownException.getMessage(), "Role not found");
    }



}
