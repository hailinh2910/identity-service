package com.linh.identity_service.service;

import com.linh.identity_service.dto.request.UserCreationRequest;
import com.linh.identity_service.dto.request.UserUpdateRequest;
import com.linh.identity_service.dto.response.UserResponse;
import com.linh.identity_service.entity.User;
import com.linh.identity_service.exception.AppException;
import com.linh.identity_service.exception.ErrorCode;
import com.linh.identity_service.mapper.UserMapper;
import com.linh.identity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;
    UserMapper userMapper;
    public UserResponse createRequest(UserCreationRequest request) {

        if(userRepository.existsByUsername(request.getUsername())){
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        User user = userMapper.toUser(request);
        return userMapper.toUserResponse(userRepository.save(user));

    }

    public List<UserResponse> getAllUsers() {
        return  userMapper.toUserResponses(userRepository.findAll());
    }
    public UserResponse getUserById(String userId) {
        return userMapper.toUserResponse
                (userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
    }
    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        userMapper.updateUser(user,request);
        return  userMapper.toUserResponse(userRepository.save(user));
    }
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }
}
