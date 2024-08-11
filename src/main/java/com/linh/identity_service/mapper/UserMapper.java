package com.linh.identity_service.mapper;

import com.linh.identity_service.dto.request.UserCreationRequest;
import com.linh.identity_service.dto.request.UserUpdateRequest;
import com.linh.identity_service.dto.response.UserResponse;
import com.linh.identity_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring") //DI
public interface UserMapper {
    User toUser(UserCreationRequest request);
    UserResponse toUserResponse(User user);
    List<UserResponse> toUserResponses(List<User> users);
    void updateUser(@MappingTarget User user, UserUpdateRequest request);

}
