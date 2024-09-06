package com.linh.identity_service.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;

import com.linh.identity_service.dto.request.UserCreationRequest;
import com.linh.identity_service.dto.response.UserResponse;
import com.linh.identity_service.entity.Role;
import com.linh.identity_service.entity.User;
import com.linh.identity_service.exception.AppException;
import com.linh.identity_service.repository.RoleRepository;
import com.linh.identity_service.repository.UserRepository;

@SpringBootTest
@TestPropertySource("/test.properties")
public class UserServiceTest {
    @Autowired
    UserService userService;

    @MockBean
    UserRepository
            userRepository; // create emulator and  don't actually use it during testing it mean don't use database

    @MockBean
    RoleRepository roleRepository;

    //    @MockBean
    //    UserMapper userMapper;
    // khong mock ra thi thoi. mock ra thi phai test
    // tại sao cần mock? mock ra để xem tính tương tác  ( kiểm soát hành vi của nó ) và trả về đối tượng mong muốn
    // ở đây ko cần mock userMapper vì nó là 1 dependency
    // nếu ko mock no sẽ sử dụng data real khi test mà ko phải data giả lập khi mock ( khi .thenReturn)
    // và mockBean neu khong chi dinh hanh vi thi no se mac dinh la NULL
    // NHƯNG @Mock thì lại có thể mock ra và không use!!! vi khong can spring context ( khong can bean )
    // tức là khi mock Bean thì nó mô phỏng Bean mà khi test ko chỉ định hành vi nó thì nó sẽ là null
    // cho nên thường mock using for unit test / mockBean for integration tests

    // summary: khi MockBean no se tao 1 bean mo phong -> nen khi test ma khong chi dinh hanh vi
    // -> khi thuc thi test den doan bean mo phong
    // -> đó no se tu mac dinh bean mo phong mặc định ( ở đây là null )-> nullPointerException
    // null cho các đối tượng (Reference types)
    // notice:  MockBean sẽ có giá trị mặc định nếu là  int,long = 0, boolean = false, char = '\u0000'

    private UserCreationRequest userCreationRequest;
    private UserResponse userResponse;
    private User user;
    private Role role;

    private LocalDate dob;

    @BeforeEach
    public void setUp() {
        dob = LocalDate.of(1990, 1, 1);
        userCreationRequest = UserCreationRequest.builder()
                .username("john")
                .firstName("John")
                .lastName("Doe")
                .password("12345678")
                .dob(dob)
                .build();
        userResponse = UserResponse.builder()
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

        role = Role.builder().build();
    }

    @Test
    void createUser_validRequest_success() {
        // GIVEN
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        //    when(userMapper.toUser(any(UserCreationRequest.class))).thenReturn(user);
        when(userRepository.save(any())).thenReturn(user);
        when(roleRepository.findById(any())).thenReturn(Optional.ofNullable(role));
        //   when(userMapper.toUserResponse(user)).thenReturn(userResponse);
        // WHEN
        UserResponse userResponse = userService.createUser(userCreationRequest);

        // THEN
        Assertions.assertEquals(userResponse.getId(), "cf321032132");
        Assertions.assertEquals(userResponse.getUsername(), "john");
    }

    @Test
    void createUser_userExisted_fail() {
        // GIVEN
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // WHEN
        AppException response =
                Assertions.assertThrows(AppException.class, () -> userService.createUser(userCreationRequest));

        // THEN
        Assertions.assertEquals(response.getErrorCode().getCode(), 1001);
        Assertions.assertEquals(response.getMessage(), "User existed");
    }

    @Test
    void createUser_roleNotFound_fail() {
        // GIVEN
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(roleRepository.findById("USER")).thenReturn(Optional.empty());

        // WHEN
        AppException thrownException =
                Assertions.assertThrows(AppException.class, () -> userService.createUser(userCreationRequest));

        // THEN
        Assertions.assertEquals(thrownException.getErrorCode().getCode(), 1008);
        Assertions.assertEquals(thrownException.getMessage(), "Role not found");
    }

    @Test
    @WithMockUser(username = "john") // properties can have role because some method need role ex: ADMIN
    void getMyinfo_validRequest_success() {

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        //  when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse response = userService.myInfo();

        Assertions.assertEquals(response.getUsername(), "john");
    }

    @Test
    @WithMockUser(username = "john") // properties can have role because some method need role ex: ADMIN
    void getMyinfo_userNotFound_error() {

        when(userRepository.findByUsername("john")).thenReturn(Optional.ofNullable(null));
        // when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        AppException thrownException = Assertions.assertThrows(AppException.class, () -> userService.myInfo());

        Assertions.assertEquals(thrownException.getErrorCode().getCode(), 1004);
    }
}
