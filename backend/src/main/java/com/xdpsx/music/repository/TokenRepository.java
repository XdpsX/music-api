package com.xdpsx.music.repository;

import com.xdpsx.music.model.entity.Token;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface TokenRepository extends CrudRepository<Token, Long> {
    Optional<Token> findByAccessToken(String accessToken);
    Optional<Token> findByRefreshToken(String refreshToken);

    @Transactional
    @Modifying
    @Query("DELETE FROM Token t WHERE t.revoked = true")
    int deleteRevokedTokens();
}
