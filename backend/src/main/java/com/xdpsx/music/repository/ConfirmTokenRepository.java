package com.xdpsx.music.repository;

import com.xdpsx.music.entity.ConfirmToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ConfirmTokenRepository extends CrudRepository<ConfirmToken, Long> {
    Optional<ConfirmToken> findByToken(String token);
}
