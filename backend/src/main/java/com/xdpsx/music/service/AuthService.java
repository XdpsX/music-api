package com.xdpsx.music.service;

import com.xdpsx.music.dto.request.ForgotPasswordRequest;
import com.xdpsx.music.dto.request.LoginRequest;
import com.xdpsx.music.dto.request.RegisterRequest;
import com.xdpsx.music.dto.request.ResetPasswordRequest;
import com.xdpsx.music.dto.response.TokenResponse;
import com.xdpsx.music.entity.User;
import jakarta.mail.MessagingException;

public interface AuthService {
    void register(RegisterRequest request) throws MessagingException;
    void sendActivateAccountEmail(User user) throws MessagingException;
    TokenResponse activateAccount(String activeCode);
    User getUserByEmail(String email);
    TokenResponse login(LoginRequest request);
    TokenResponse refreshToken(String authHeader);
    void forgotPassword(ForgotPasswordRequest request) throws MessagingException;
    void resetPassword(ResetPasswordRequest request);
}
