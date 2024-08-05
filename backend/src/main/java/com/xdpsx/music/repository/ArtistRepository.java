package com.xdpsx.music.repository;

import com.xdpsx.music.model.entity.Artist;
import com.xdpsx.music.repository.criteria.ArtistCriteriaRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<Artist, Long>, ArtistCriteriaRepository {
}
