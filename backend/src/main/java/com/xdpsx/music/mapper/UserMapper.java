package com.xdpsx.music.mapper;

import com.xdpsx.music.dto.response.UserProfileResponse;
import com.xdpsx.music.dto.response.UserResponse;
import com.xdpsx.music.model.entity.User;
import com.xdpsx.music.util.Links;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class UserMapper {
    protected abstract UserProfileResponse mapToProfileResponse(User user);
    public UserProfileResponse fromEntityToProfileResponse(User user){
        UserProfileResponse response = mapToProfileResponse(user);
        Links.addLinksToProfile(response);
        return response;
    }
    public abstract UserResponse fromEntityToResponse(User user);
}
