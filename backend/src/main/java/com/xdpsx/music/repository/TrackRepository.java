package com.xdpsx.music.repository;

import com.xdpsx.music.entity.Track;
import com.xdpsx.music.repository.criteria.TrackCriteriaRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackRepository extends JpaRepository<Track, Long>, TrackCriteriaRepository {
    int countByAlbumId(Long albumId);
    List<Track> findByAlbumIdOrderByTrackNumberAsc(Long albumId);
}
