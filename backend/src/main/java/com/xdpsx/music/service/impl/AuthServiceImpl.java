package com.xdpsx.music.service.impl;

import com.xdpsx.music.dto.request.LoginRequest;
import com.xdpsx.music.dto.request.RegisterRequest;
import com.xdpsx.music.dto.response.TokenResponse;
import com.xdpsx.music.entity.*;
import com.xdpsx.music.exception.BadRequestException;
import com.xdpsx.music.exception.DuplicateResourceException;
import com.xdpsx.music.exception.ResourceNotFoundException;
import com.xdpsx.music.exception.TooManyRequestsException;
import com.xdpsx.music.repository.ConfirmTokenRepository;
import com.xdpsx.music.repository.RoleRepository;
import com.xdpsx.music.repository.UserRepository;
import com.xdpsx.music.service.AuthService;
import com.xdpsx.music.service.EmailService;
import com.xdpsx.music.service.TokenService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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


    @Value("${app.mail.frontend.activation-url}")
    private String activationUrl;

    private final static int NUM_EMAILS_PER_DAY = 5;

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

    public void sendActivateAccountEmail(User user) throws MessagingException {
        List<ConfirmToken> existingTokens =  tokenService.getTodayConfirmTokensByUser(user);
        if (existingTokens.size() >= NUM_EMAILS_PER_DAY){
            throw new TooManyRequestsException(
                    String.format("You can send request %s times per day", NUM_EMAILS_PER_DAY));
        }
        String activeCode = tokenService.generateAndSaveConfirmToken(user);
        emailService.sendEmail(
                user.getEmail(),
                user.getName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                activeCode,
                "Account activation"
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

        Token jwt = tokenService.saveJwtToken(savedUser);
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
        Token jwt = tokenService.saveJwtToken(user);
        return TokenResponse.builder()
                .accessToken(jwt.getAccessToken())
                .refreshToken(jwt.getRefreshToken())
                .build();
    }




}
