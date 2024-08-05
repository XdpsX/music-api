package com.xdpsx.music.controller;

import com.xdpsx.music.dto.common.PageResponse;
import com.xdpsx.music.dto.request.ChangePasswordRequest;
import com.xdpsx.music.dto.request.UserProfileRequest;
import com.xdpsx.music.dto.request.params.UserParams;
import com.xdpsx.music.dto.response.UserProfileResponse;
import com.xdpsx.music.dto.response.UserResponse;
import com.xdpsx.music.entity.User;
import com.xdpsx.music.security.UserContext;
import com.xdpsx.music.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserContext userContext;
    private final UserService userService;

    @PatchMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request
    ){
        User loggedUser = userContext.getLoggedUser();
        userService.changePassword(request, loggedUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile() {
        User loggedUser = userContext.getLoggedUser();
        UserProfileResponse response = userService.getUserProfile(loggedUser);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateUserProfile(
            @Valid @ModelAttribute UserProfileRequest request,
            @RequestParam(required = false) MultipartFile image) {
        User loggedUser = userContext.getLoggedUser();
        UserProfileResponse response = userService.updateUserProfile(loggedUser, request, image);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}/lock")
    public ResponseEntity<Void> lockUser(@PathVariable Long userId) {
        userService.lockUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/unlock")
    public ResponseEntity<Void> unlockUser(@PathVariable Long userId) {
        userService.unlockUser(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId){
        userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> getAllUsers(
            @Valid UserParams params
    ){
        PageResponse<UserResponse> responses = userService.getAllUsers(params);
        return ResponseEntity.ok(responses);
    }
}
