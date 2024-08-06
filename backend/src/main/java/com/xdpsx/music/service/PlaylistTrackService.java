package com.xdpsx.music.service;

public interface PlaylistTrackService {
    void addTrackToPlaylist(Long playlistId, Long trackId);
    void removeTrackFromPlaylist(Long playlistId, Long trackId);
}
