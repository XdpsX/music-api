package com.xdpsx.music.service;

import com.xdpsx.music.dto.request.ChangePasswordRequest;
import com.xdpsx.music.dto.response.UserProfileResponse;
import com.xdpsx.music.entity.User;

public interface UserService {
    void changePassword(ChangePasswordRequest request, User loggedUser);
    UserProfileResponse getUserProfile(User loggedUser);
}
