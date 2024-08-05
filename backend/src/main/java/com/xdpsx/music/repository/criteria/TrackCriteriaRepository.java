package com.xdpsx.music.repository.criteria;

import com.xdpsx.music.model.entity.Track;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TrackCriteriaRepository {
    Page<Track> findLikedTracksByUserId(Long userId, Pageable pageable, String name, String sort);
    Page<Track> findWithFilters(Pageable pageable, String name, String sort);
    Page<Track> findWithAlbumFilters(Pageable pageable, String name, String sort, Long albumId);
    Page<Track> findWithGenreFilters(Pageable pageable, String name, String sort, Integer genreId);
    Page<Track> findWithArtistFilters(Pageable pageable, String name, String sort, Long artistId);
}
