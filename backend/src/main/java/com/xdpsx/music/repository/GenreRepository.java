package com.xdpsx.music.repository;

import com.xdpsx.music.model.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Integer> {
    boolean existsByName(String name);
}
