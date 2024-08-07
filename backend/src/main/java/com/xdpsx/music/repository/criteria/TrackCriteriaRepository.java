package com.xdpsx.music.repository.criteria;

import com.xdpsx.music.model.entity.Track;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TrackCriteriaRepository {
    Page<Track> findFavoriteTracksByUserId(Pageable pageable, String name, String sort, Long userId);
    Page<Track> findWithFilters(Pageable pageable, String name, String sort);
    Page<Track> findTracksByAlbum(Pageable pageable, String name, String sort, Long albumId);
    Page<Track> findTracksByGenre(Pageable pageable, String name, String sort, Integer genreId);
    Page<Track> findTracksByArtist(Pageable pageable, String name, String sort, Long artistId);
    Page<Track> findTracksInPlaylist(Pageable pageable, String name, String sort, Long playlistId);
}
