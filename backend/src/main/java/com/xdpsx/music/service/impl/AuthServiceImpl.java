package com.xdpsx.music.service.impl;

import com.xdpsx.music.constant.Keys;
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
import com.xdpsx.music.repository.RoleRepository;
import com.xdpsx.music.repository.UserRepository;
import com.xdpsx.music.security.JwtProvider;
import com.xdpsx.music.service.AuthService;
import com.xdpsx.music.service.CacheService;
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

import static com.xdpsx.music.constant.AppConstants.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final CacheService cacheService;

    @Value("${app.mail.frontend.activation-url}")
    private String activationUrl;



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
        String sendMailKey = Keys.getSendMailKey(user.getId());
        boolean exists = cacheService.hasKey(sendMailKey);
        if (exists){
            throw new TooManyRequestsException(String.format("Please re-send mail after %s minutes", DELAY_SEND_MAIL_MINUTES));
        }

        List<ConfirmToken> existingTokens =  tokenService.getTodayConfirmTokensByUser(user);
        if (existingTokens.size() >= EMAILS_PER_DAY){
            throw new TooManyRequestsException(
                    String.format("You can send request %s times per day", EMAILS_PER_DAY));
        }
        String activeCode = tokenService.generateAndSaveConfirmToken(user, ACTIVE_CODE_MINUTES);

        cacheService.setValue(sendMailKey, "1", DELAY_SEND_MAIL_MINUTES*60*1000);

        Map<String, Object> properties = new HashMap<>();
        properties.put("username", user.getName());
        properties.put("confirmationUrl", activationUrl);
        properties.put("activeCode", activeCode);
        properties.put("validTime", ACTIVE_CODE_MINUTES);
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
        var confirmToken = tokenService.findConfirmTokenByCode(activeCode);
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

        tokenService.validatesConfirmToken(confirmToken);

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
                Token token = tokenService.findTokenByRefreshToken(refreshToken);
                if (token != null && !token.isRevoked()){
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
        ConfirmToken confirmToken = tokenService.findConfirmTokenByCode(request.getResetCode());
        if (LocalDateTime.now().isAfter(confirmToken.getExpiredAt())
                || confirmToken.isRevoked()
                || confirmToken.getValidatedAt() != null) {
            throw new BadRequestException("Reset code has expired");
        }
        var userToUpdate = confirmToken.getUser();
        userToUpdate.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(userToUpdate);

        tokenService.validatesConfirmToken(confirmToken);
    }

    private void sendResetPasswordEmail(User user) throws MessagingException {
        String sendMailKey = Keys.getSendMailKey(user.getId());
        boolean exists = cacheService.hasKey(sendMailKey);
        if (exists){
            throw new TooManyRequestsException(String.format("Please re-send mail after %s minutes", DELAY_SEND_MAIL_MINUTES));
        }

        List<ConfirmToken> existingTokens =  tokenService.getTodayConfirmTokensByUser(user);
        if (existingTokens.size() >= EMAILS_PER_DAY){
            throw new TooManyRequestsException(
                    String.format("You can send request %s times per day", EMAILS_PER_DAY));
        }

        String resetCode = tokenService.generateAndSaveConfirmToken(user, ACTIVE_CODE_MINUTES);

        cacheService.setValue(sendMailKey, "1", DELAY_SEND_MAIL_MINUTES*60*1000);

        Map<String, Object> properties = new HashMap<>();
        properties.put("username", user.getName());
        properties.put("resetCode", resetCode);
        properties.put("validTime", ACTIVE_CODE_MINUTES);
        emailService.sendEmail(
                user.getEmail(),
                EmailTemplateName.RESET_PASSWORD,
                "Reset Password",
                properties

        );
    }

}
