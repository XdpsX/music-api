package com.xdpsx.music.service;

import com.xdpsx.music.dto.common.PageResponse;
import com.xdpsx.music.dto.request.PlaylistRequest;
import com.xdpsx.music.dto.request.params.PlaylistParam;
import com.xdpsx.music.dto.response.PlaylistResponse;
import com.xdpsx.music.dto.response.PlaylistTrackExistsResponse;

import java.util.List;

public interface PlaylistService {
    PlaylistResponse createPlaylist(PlaylistRequest request);
    void deletePlaylist(Long playlistId);
    PageResponse<PlaylistResponse> getAllUserPlaylists(PlaylistParam params);
    PlaylistResponse getPlaylistById(Long id);
    PlaylistResponse updatePlaylist(Long id, PlaylistRequest request);
    List<PlaylistTrackExistsResponse> checkTracksInPlaylists(List<Long> playlistIds, List<Long> trackIds);
}
