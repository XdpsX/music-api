package com.xdpsx.music.controller;

import com.xdpsx.music.dto.common.PageResponse;
import com.xdpsx.music.dto.request.PlaylistRequest;
import com.xdpsx.music.dto.request.params.PlaylistParam;
import com.xdpsx.music.dto.response.PlaylistResponse;
import com.xdpsx.music.model.entity.User;
import com.xdpsx.music.security.UserContext;
import com.xdpsx.music.service.PlaylistService;
import com.xdpsx.music.service.PlaylistTrackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/playlists")
@RequiredArgsConstructor
public class PlaylistController {
    private final UserContext userContext;
    private final PlaylistService playlistService;
    private final PlaylistTrackService playlistTrackService;

    @PostMapping
    public ResponseEntity<PlaylistResponse> createPlaylist(
            @Valid @RequestBody PlaylistRequest request
    ){
        User loggedUser = userContext.getLoggedUser();
        PlaylistResponse response = playlistService.createPlaylist(request, loggedUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{playlistId}")
    public ResponseEntity<Void> deletePlaylist(
            @PathVariable Long playlistId
    ){
        User loggedUser = userContext.getLoggedUser();
        playlistService.deletePlaylist(playlistId, loggedUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    public ResponseEntity<PageResponse<PlaylistResponse>> getAllUserPlaylists(
            @Valid PlaylistParam params
    ){
        User loggedUser = userContext.getLoggedUser();
        PageResponse<PlaylistResponse> responses = playlistService.getAllUserPlaylists(params, loggedUser);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{playlistId}/add/{trackId}")
    public ResponseEntity<Void> addTrackToPlaylist(@PathVariable Long playlistId, @PathVariable Long trackId) {
        playlistTrackService.addTrackToPlaylist(playlistId, trackId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{playlistId}/remove/{trackId}")
    public ResponseEntity<Void> removeTrackFromPlaylist(@PathVariable Long playlistId, @PathVariable Long trackId) {
        playlistTrackService.removeTrackFromPlaylist(playlistId, trackId);
        return ResponseEntity.noContent().build();
    }
}
