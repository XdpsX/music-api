package com.xdpsx.music.mapper;

import com.xdpsx.music.dto.response.UserProfileResponse;
import com.xdpsx.music.dto.response.UserResponse;
import com.xdpsx.music.model.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserProfileResponse fromEntityToProfileResponse(User user);
    UserResponse fromEntityToResponse(User user);
}
