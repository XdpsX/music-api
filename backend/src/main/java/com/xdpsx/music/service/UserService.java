package com.xdpsx.music.service;

import com.xdpsx.music.dto.common.PageResponse;
import com.xdpsx.music.dto.request.ChangePasswordRequest;
import com.xdpsx.music.dto.request.UserProfileRequest;
import com.xdpsx.music.dto.request.params.UserParams;
import com.xdpsx.music.dto.response.UserProfileResponse;
import com.xdpsx.music.dto.response.UserResponse;
import com.xdpsx.music.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    void changePassword(ChangePasswordRequest request, User loggedUser);
    UserProfileResponse getUserProfile(User loggedUser);
    UserProfileResponse updateUserProfile(User loggedUser, UserProfileRequest request, MultipartFile image);
    void lockUser(Long userId);
    void unlockUser(Long userId);
    void deleteUserById(Long userId);
    PageResponse<UserResponse> getAllUsers(UserParams params);
}
