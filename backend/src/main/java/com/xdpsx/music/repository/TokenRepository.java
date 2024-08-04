package com.xdpsx.music.repository;

import com.xdpsx.music.entity.Token;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends CrudRepository<Token, Long> {
    @Query("""
            select t from Token t inner join User u on t.user.id = u.id
            where u.id = :userId and t.revoked = false
            """)
    List<Token> findAllValidTokensByUser(Long userId);

    Optional<Token> findByAccessToken(String accessToken);
    Optional<Token> findByRefreshToken(String refreshToken);
}
