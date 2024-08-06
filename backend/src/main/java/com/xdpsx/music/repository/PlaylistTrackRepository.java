package com.xdpsx.music.repository;

import com.xdpsx.music.model.entity.PlaylistTrack;
import com.xdpsx.music.model.id.PlaylistTrackId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistTrackRepository extends JpaRepository<PlaylistTrack, PlaylistTrackId> {
    int countByPlaylistId(Long playlistId);
    List<PlaylistTrack> findByPlaylistIdOrderByTrackNumber(Long playlistId);
}
