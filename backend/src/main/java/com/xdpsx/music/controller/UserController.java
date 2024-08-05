package com.xdpsx.music.controller;

import com.xdpsx.music.dto.request.ChangePasswordRequest;
import com.xdpsx.music.dto.response.UserProfileResponse;
import com.xdpsx.music.entity.User;
import com.xdpsx.music.security.UserContext;
import com.xdpsx.music.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile() {
        User loggedUser = userContext.getLoggedUser();
        UserProfileResponse response = userService.getUserProfile(loggedUser);
        return ResponseEntity.ok(response);
    }
}
