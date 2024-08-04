package com.xdpsx.music.controller;

import com.xdpsx.music.dto.request.LoginRequest;
import com.xdpsx.music.dto.request.RegisterRequest;
import com.xdpsx.music.dto.response.TokenResponse;
import com.xdpsx.music.entity.User;
import com.xdpsx.music.service.AuthService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request
    ) throws MessagingException {
        User existingUser = authService.getUserByEmail(request.getEmail());
        if (existingUser != null){
            if (!existingUser.isEnabled()){
                authService.sendActivateAccountEmail(existingUser);
                return ResponseEntity.ok("Email is already registered but not confirmed. " +
                        "A new confirmation email has been sent.");
            }
        }
        authService.register(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/activate-account")
    public ResponseEntity<TokenResponse> confirmAccount(
            @RequestParam String activeCode
    ) {
        TokenResponse response = authService.activateAccount(activeCode);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponse> refreshToken(
            @RequestHeader("Authorization") String authHeader
    ) {
        TokenResponse response = authService.refreshToken(authHeader);
        if (response == null ){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.ok(response);
    }
}
