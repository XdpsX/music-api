package com.xdpsx.music.repository;

import com.xdpsx.music.model.entity.User;
import com.xdpsx.music.repository.criteria.UserCriteriaRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserCriteriaRepository {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
