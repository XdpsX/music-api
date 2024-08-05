package com.xdpsx.music.service;

import com.xdpsx.music.dto.common.PageResponse;
import com.xdpsx.music.dto.request.PlaylistRequest;
import com.xdpsx.music.dto.request.params.PlaylistParam;
import com.xdpsx.music.dto.response.PlaylistResponse;
import com.xdpsx.music.model.entity.User;

public interface PlaylistService {
    PlaylistResponse createPlaylist(PlaylistRequest request, User loggedUser);
    void deletePlaylist(Long playlistId, User loggedUser);
    PageResponse<PlaylistResponse> getAllUserPlaylists(PlaylistParam params, User loggedUser);
}
