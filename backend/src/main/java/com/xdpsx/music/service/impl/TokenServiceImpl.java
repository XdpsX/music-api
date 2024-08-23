package com.xdpsx.music.service.impl;

import com.xdpsx.music.constant.AppConstants;
import com.xdpsx.music.exception.ResourceNotFoundException;
import com.xdpsx.music.model.entity.ConfirmToken;
import com.xdpsx.music.model.entity.Token;
import com.xdpsx.music.model.entity.User;
import com.xdpsx.music.repository.ConfirmTokenRepository;
import com.xdpsx.music.repository.TokenRepository;
import com.xdpsx.music.security.JwtProvider;
import com.xdpsx.music.service.TokenService;
import com.xdpsx.music.util.I18nUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final ConfirmTokenRepository confirmTokenRepository;
    private final TokenRepository tokenRepository;
    private final JwtProvider jwtProvider;
    private final I18nUtils i18nUtils;

    @Override
    public String generateAndSaveConfirmToken(User user, int validMinutes) {
        String generatedCode = generateCode();
        ConfirmToken token = ConfirmToken.builder()
                .code(generatedCode)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(validMinutes))
                .user(user)
                .build();
        confirmTokenRepository.save(token);
        return generatedCode;
    }

    @Override
    public ConfirmToken findConfirmTokenByCode(String code) {
        return confirmTokenRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException(i18nUtils.getConfirmTokenNotFoundMsg(code)));
    }

    @Override
    public void validatesConfirmToken(ConfirmToken confirmToken) {
        confirmToken.setValidatedAt(LocalDateTime.now());
        confirmToken.setRevoked(true);
        confirmTokenRepository.save(confirmToken);
    }

    private String generateCode() {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < AppConstants.ACTIVE_CODE_LENGTH; i++){
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

    @Override
    public List<ConfirmToken> getTodayConfirmTokensByUser(User user) {
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        return confirmTokenRepository.findAllTokensByUserAndDate(user, startOfDay, endOfDay);
    }

    @Override
    public Token createJwtToken(User user) {
        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);
        var token = Token.builder()
                .user(user)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .revoked(false)
                .build();
        return tokenRepository.save(token);
    }

    @Async
    @Override
    public void revokeTokenByAccessToken(String accessToken){
        var token = tokenRepository.findByAccessToken(accessToken)
                .orElse(null);
        if (token != null && !token.isRevoked()){
            token.setRevoked(true);
            tokenRepository.save(token);
        }
    }

    @Override
    public Token findTokenByRefreshToken(String refreshToken) {
        return tokenRepository.findByRefreshToken(refreshToken)
                .orElse(null);
    }
}
