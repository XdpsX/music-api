package com.xdpsx.music.repository;

import com.xdpsx.music.model.entity.Album;
import com.xdpsx.music.repository.criteria.AlbumCriteriaRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumRepository extends JpaRepository<Album, Long>, AlbumCriteriaRepository {
}
