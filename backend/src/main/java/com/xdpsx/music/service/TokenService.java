package com.xdpsx.music.service;

import com.xdpsx.music.model.entity.ConfirmToken;
import com.xdpsx.music.model.entity.Token;
import com.xdpsx.music.model.entity.User;

import java.util.List;

public interface TokenService {
    String generateAndSaveConfirmToken(User user, int validMinutes);
    ConfirmToken findConfirmTokenByCode(String code);
    void validatesConfirmToken(ConfirmToken confirmToken);
    List<ConfirmToken> getTodayConfirmTokensByUser(User user);
    Token createJwtToken(User user);
    void revokeTokenByAccessToken(String accessToken);
    Token findTokenByRefreshToken(String refreshToken);
}
