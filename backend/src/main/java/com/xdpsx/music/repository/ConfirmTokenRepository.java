package com.xdpsx.music.repository;

import com.xdpsx.music.model.entity.ConfirmToken;
import com.xdpsx.music.model.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConfirmTokenRepository extends CrudRepository<ConfirmToken, Long> {
    Optional<ConfirmToken> findByCode(String code);

    @Query("SELECT ct FROM ConfirmToken ct " +
            "WHERE ct.user = :user " +
            "AND ct.createdAt BETWEEN :startOfDay AND :endOfDay")
    List<ConfirmToken> findAllTokensByUserAndDate(
            @Param("user") User user,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    @Transactional
    @Modifying
    @Query("DELETE FROM ConfirmToken ct WHERE ct.expiredAt < :now OR ct.validatedAt IS NOT NULL")
    int deleteExpiredTokens(LocalDateTime now);

}
