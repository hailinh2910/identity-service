package com.linh.identity_service.service;

import com.linh.identity_service.dto.request.UserCreationRequest;
import com.linh.identity_service.dto.request.UserUpdateRequest;
import com.linh.identity_service.dto.response.UserResponse;
import com.linh.identity_service.entity.User;
import com.linh.identity_service.enums.Role;
import com.linh.identity_service.exception.AppException;
import com.linh.identity_service.exception.ErrorCode;
import com.linh.identity_service.mapper.UserMapper;
import com.linh.identity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    public UserResponse createRequest(UserCreationRequest request) {

        if(userRepository.existsByUsername(request.getUsername())){
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        User user = userMapper.toUser(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Set<String> roles = new HashSet<>();
        roles.add(Role.USER.name());
    //    user.setRoles(roles);

        return userMapper.toUserResponse(userRepository.save(user));

    }
//    @PreAuthorize("hasRole('ADMIN') and hasRole('MANAGER')")
//@PostAuthorize("principal.claims['user_id'] == 'userId'") user_id is name of claims

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {
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
