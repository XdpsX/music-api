package com.xdpsx.music.repository;

import com.xdpsx.music.model.entity.Track;
import com.xdpsx.music.repository.criteria.TrackCriteriaRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackRepository extends JpaRepository<Track, Long>, TrackCriteriaRepository {
    int countByAlbumId(Long albumId);
}
