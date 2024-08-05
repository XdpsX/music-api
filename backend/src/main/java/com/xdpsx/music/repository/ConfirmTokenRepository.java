package com.xdpsx.music.repository;

import com.xdpsx.music.entity.ConfirmToken;
import com.xdpsx.music.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

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

    @Query("""
            select ct from ConfirmToken ct inner join User u on ct.user.id = u.id
            where u.id = :userId and ct.revoked = false
            """)
    List<ConfirmToken> findAllValidTokensByUser(Long userId);
}
