package com.xdpsx.music.service.impl;

import com.xdpsx.music.entity.ConfirmToken;
import com.xdpsx.music.entity.Token;
import com.xdpsx.music.entity.User;
import com.xdpsx.music.repository.ConfirmTokenRepository;
import com.xdpsx.music.repository.TokenRepository;
import com.xdpsx.music.security.JwtProvider;
import com.xdpsx.music.service.TokenService;
import lombok.RequiredArgsConstructor;
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

    private final static int ACTIVE_CODE_LENGTH = 6;

    @Override
    public String generateAndSaveConfirmToken(User user) {
        String generatedCode = generateCode();
        ConfirmToken token = ConfirmToken.builder()
                .code(generatedCode)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .user(user)
                .build();
        confirmTokenRepository.save(token);
        return generatedCode;
    }

    private String generateCode() {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < ACTIVE_CODE_LENGTH; i++){
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
    public Token saveJwtToken(User user) {
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

    // TODO Try using trigger in db or schedule in spring later
    @Override
    public void revokeAllJwtTokens(User user){
        var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(t -> {
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
}
