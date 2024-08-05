package com.xdpsx.music.service;

import com.xdpsx.music.model.entity.ConfirmToken;
import com.xdpsx.music.model.entity.Token;
import com.xdpsx.music.model.entity.User;

import java.util.List;

public interface TokenService {
    String generateAndSaveConfirmToken(User user, int validMinutes);
    void revokeAllConfirmTokens(User user);
    List<ConfirmToken> getTodayConfirmTokensByUser(User user);
    Token createJwtToken(User user);
    void revokeAllJwtTokens(User user);
    void revokeTokenByAccessToken(String accessToken);
}
