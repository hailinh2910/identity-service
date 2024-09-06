package com.linh.identity_service.service;

import com.linh.identity_service.dto.request.UserCreationRequest;
import com.linh.identity_service.dto.request.UserUpdateRequest;
import com.linh.identity_service.dto.response.UserResponse;
import com.linh.identity_service.entity.Role;
import com.linh.identity_service.entity.User;
import com.linh.identity_service.exception.AppException;
import com.linh.identity_service.exception.ErrorCode;
import com.linh.identity_service.mapper.UserMapper;
import com.linh.identity_service.repository.RoleRepository;
import com.linh.identity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {


    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;



    public UserResponse createUser(UserCreationRequest request) {
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById("USER").ifPresent(roles::add);

        user.setRoles(roles);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        return userMapper.toUserResponse(user);
    }
//    @PreAuthorize("hasRole('ADMIN') and hasRole('MANAGER')")
//@PostAuthorize("principal.claims['user_id'] == 'userId'") user_id is name of claims

    @PreAuthorize("hasRole('ADMIN')") // hasRole se tim prefix la ROLE_
 //   @PreAuthorize("hasAuthority('CREATE_DATA')") // hasAuthority o day se tim permission
    public List<UserResponse> getAllUsers() {
        SecurityContextHolder.getContext().getAuthentication().getAuthorities().forEach(x -> log.info(x.getAuthority()));

        log.info("Get all users by ADMIN");
        return  userMapper.toUserResponses(userRepository.findAll());
    }

// vi du ve postAuthorize, trong do .owner la field of object document, authentication.name is subject trong claimsSet
//    @PostAuthorize("returnObject.owner == authentication.name")
//    public Document getDocument(Long documentId) {
//        // Giả sử đây là cách bạn lấy tài liệu từ cơ sở dữ liệu
//        Document document = documentRepository.findById(documentId);
//        return document;
//    }



//    @PostAuthorize("hasRole('ADMIN')")
    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUserById(String userId) {
        log.info("PostAuthorize ");

       var auth =  SecurityContextHolder.getContext().getAuthentication();

        log.info("username: {}", auth.getName());
        auth.getAuthorities().forEach( a -> log.info(a.getAuthority()));

        return userMapper.toUserResponse
                (userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }
    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userMapper.updateUser(user,request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));

        return  userMapper.toUserResponse(userRepository.save(user));
    }
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    public UserResponse myInfo(){
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }
}
