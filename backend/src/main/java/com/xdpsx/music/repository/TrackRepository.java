package com.xdpsx.music.repository;

import com.xdpsx.music.entity.Track;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackRepository extends JpaRepository<Track, Long> {
    int countByAlbumId(Long albumId);
    List<Track> findByAlbumIdOrderByTrackNumberAsc(Long albumId);
}
