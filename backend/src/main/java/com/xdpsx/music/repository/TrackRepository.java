package com.xdpsx.music.repository;

import com.xdpsx.music.model.entity.Track;
import com.xdpsx.music.repository.criteria.TrackCriteriaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TrackRepository extends JpaRepository<Track, Long>, TrackCriteriaRepository {
    int countByAlbumId(Long albumId);
    List<Track> findByAlbumIdOrderByTrackNumberAsc(Long albumId);

    @Query("select count(l) from Like l where l.track.id = :trackId")
    Long countLikesByTrackId(@Param("trackId") Long trackId);
}
