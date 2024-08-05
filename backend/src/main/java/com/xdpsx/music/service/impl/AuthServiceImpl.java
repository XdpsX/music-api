package com.xdpsx.music.service.impl;

import com.xdpsx.music.dto.request.ForgotPasswordRequest;
import com.xdpsx.music.dto.request.LoginRequest;
import com.xdpsx.music.dto.request.RegisterRequest;
import com.xdpsx.music.dto.request.ResetPasswordRequest;
import com.xdpsx.music.dto.response.TokenResponse;
import com.xdpsx.music.exception.*;
import com.xdpsx.music.model.entity.ConfirmToken;
import com.xdpsx.music.model.entity.Role;
import com.xdpsx.music.model.entity.Token;
import com.xdpsx.music.model.entity.User;
import com.xdpsx.music.model.enums.EmailTemplateName;
import com.xdpsx.music.repository.ConfirmTokenRepository;
import com.xdpsx.music.repository.RoleRepository;
import com.xdpsx.music.repository.TokenRepository;
import com.xdpsx.music.repository.UserRepository;
import com.xdpsx.music.security.JwtProvider;
import com.xdpsx.music.service.AuthService;
import com.xdpsx.music.service.EmailService;
import com.xdpsx.music.service.TokenService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ConfirmTokenRepository confirmTokenRepository;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final TokenRepository tokenRepository;


    @Value("${app.mail.frontend.activation-url}")
    private String activationUrl;

    private final static int NUM_EMAILS_PER_DAY = 5;
    private final static int CODE_VALID_TIME = 10;

    @Override
    public void register(RegisterRequest request) throws MessagingException {
        Role userRole = roleRepository.findByName(Role.USER)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Not found Role %s", Role.USER)));
        if (userRepository.existsByEmail(request.getEmail())){
            throw new DuplicateResourceException(String.format("Email %s has already existed", request.getEmail()));
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .avatar(null)
                .accountLocked(false)
                .enabled(false)
                .role(userRole)
                .build();
        var savedUser = userRepository.save(user);

        sendActivateAccountEmail(savedUser);
    }

    @Override
    public void sendActivateAccountEmail(User user) throws MessagingException {
        List<ConfirmToken> existingTokens =  tokenService.getTodayConfirmTokensByUser(user);
        if (existingTokens.size() >= NUM_EMAILS_PER_DAY){
            throw new TooManyRequestsException(
                    String.format("You can send request %s times per day", NUM_EMAILS_PER_DAY));
        }
        tokenService.revokeAllConfirmTokens(user);
        String activeCode = tokenService.generateAndSaveConfirmToken(user, CODE_VALID_TIME);

        Map<String, Object> properties = new HashMap<>();
        properties.put("username", user.getName());
        properties.put("confirmationUrl", activationUrl);
        properties.put("activeCode", activeCode);
        properties.put("validTime", CODE_VALID_TIME);
        emailService.sendEmail(
                user.getEmail(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                "Account activation",
                properties
        );
    }

    @Transactional
    @Override
    public TokenResponse activateAccount(String activeCode) {
        var confirmToken = confirmTokenRepository.findByCode(activeCode)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Not found active code %s", activeCode)));
        if (LocalDateTime.now().isAfter(confirmToken.getExpiredAt())
                || confirmToken.isRevoked()
                || confirmToken.getValidatedAt() != null) {
            throw new BadRequestException("Active code has expired");
        }
        var user = confirmToken.getUser();
        if (user.isEnabled()){
            throw new BadRequestException("Account has already activated");
        }
        user.setEnabled(true);
        var savedUser = userRepository.save(user);

        confirmToken.setValidatedAt(LocalDateTime.now());
        confirmTokenRepository.save(confirmToken);

        Token jwt = tokenService.createJwtToken(savedUser);
        return TokenResponse.builder()
                .accessToken(jwt.getAccessToken())
                .refreshToken(jwt.getRefreshToken())
                .build();
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public TokenResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = (User) authentication.getPrincipal();
        tokenService.revokeAllJwtTokens(user);
        Token jwt = tokenService.createJwtToken(user);
        return TokenResponse.builder()
                .accessToken(jwt.getAccessToken())
                .refreshToken(jwt.getRefreshToken())
                .build();
    }

    @Override
    public TokenResponse refreshToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            return null;
        }
        final String refreshToken = authHeader.substring(7);
        final String userEmail = jwtProvider.extractUsername(refreshToken);
        if (userEmail != null) {
            User user = userRepository.findByEmail(userEmail)
                    .orElse(null);
            if (user != null && jwtProvider.isTokenValid(refreshToken, user)){
                Token token = tokenRepository.findByRefreshToken(refreshToken)
                        .orElse(null);
                if (token != null && !token.isRevoked()){
                    tokenService.revokeAllJwtTokens(user);
                    Token jwt = tokenService.createJwtToken(user);
                    return TokenResponse.builder()
                            .accessToken(jwt.getAccessToken())
                            .refreshToken(jwt.getRefreshToken())
                            .build();
                }
            }
        }
        return null;
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) throws MessagingException {
        String email = request.getEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(String.format("Email = %s not found", email)));
        if (!user.isEnabled()) {
            throw new DisabledException(String.format("User with email=%s is not active", email));
        }
        if (user.isAccountLocked()) {
            throw new LockedException(String.format("User with email=%s is locked", email));
        }
        sendResetPasswordEmail(user);
    }

    @Transactional
    @Override
    public void resetPassword(ResetPasswordRequest request) {
        var confirmToken = confirmTokenRepository.findByCode(request.getResetCode())
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Not found active code %s", request.getResetCode())));
        if (LocalDateTime.now().isAfter(confirmToken.getExpiredAt())
                || confirmToken.isRevoked()
                || confirmToken.getValidatedAt() != null) {
            throw new BadRequestException("Reset code has expired");
        }
        var userToUpdate = confirmToken.getUser();
        userToUpdate.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(userToUpdate);

        confirmToken.setValidatedAt(LocalDateTime.now());
        confirmTokenRepository.save(confirmToken);
    }

    private void sendResetPasswordEmail(User user) throws MessagingException {
        List<ConfirmToken> existingTokens =  tokenService.getTodayConfirmTokensByUser(user);
        if (existingTokens.size() >= NUM_EMAILS_PER_DAY){
            throw new TooManyRequestsException(
                    String.format("You can send request %s times per day", NUM_EMAILS_PER_DAY));
        }
        tokenService.revokeAllConfirmTokens(user);
        String resetCode = tokenService.generateAndSaveConfirmToken(user, CODE_VALID_TIME);
        Map<String, Object> properties = new HashMap<>();
        properties.put("username", user.getName());
        properties.put("resetCode", resetCode);
        properties.put("validTime", CODE_VALID_TIME);
        emailService.sendEmail(
                user.getEmail(),
                EmailTemplateName.RESET_PASSWORD,
                "Reset Password",
                properties

        );
    }

}
