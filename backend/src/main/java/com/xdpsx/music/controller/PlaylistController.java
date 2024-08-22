package com.xdpsx.music.controller;

import com.xdpsx.music.dto.common.PageResponse;
import com.xdpsx.music.dto.request.PlaylistRequest;
import com.xdpsx.music.dto.request.params.PlaylistParam;
import com.xdpsx.music.dto.request.params.TrackParams;
import com.xdpsx.music.dto.response.PlaylistResponse;
import com.xdpsx.music.dto.response.TrackResponse;
import com.xdpsx.music.model.entity.User;
import com.xdpsx.music.security.UserContext;
import com.xdpsx.music.service.PlaylistService;
import com.xdpsx.music.service.PlaylistTrackService;
import com.xdpsx.music.service.TrackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Tag(name = "REST APIs for Playlist")
@SecurityRequirement(name = "JWT")
@RestController
@RequestMapping("/playlists")
@RequiredArgsConstructor
public class PlaylistController {
    private final UserContext userContext;
    private final PlaylistService playlistService;
    private final PlaylistTrackService playlistTrackService;
    private final TrackService trackService;

    @Operation(summary = "Create new playlist")
    @PostMapping
    public ResponseEntity<PlaylistResponse> createPlaylist(
            @Valid @RequestBody PlaylistRequest request
    ){
        User loggedUser = userContext.getLoggedUser();
        PlaylistResponse response = playlistService.createPlaylist(request, loggedUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Delete playlist by id")
    @DeleteMapping("/{playlistId}")
    public ResponseEntity<Void> deletePlaylist(
            @PathVariable Long playlistId
    ){
        User loggedUser = userContext.getLoggedUser();
        playlistService.deletePlaylist(playlistId, loggedUser);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get user's playlists")
    @GetMapping("/my")
    public ResponseEntity<PageResponse<PlaylistResponse>> getAllUserPlaylists(
            @ParameterObject @Valid PlaylistParam params
    ){
        User loggedUser = userContext.getLoggedUser();
        PageResponse<PlaylistResponse> responses = playlistService.getAllUserPlaylists(params, loggedUser);
        String baseUri = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        responses.addPaginationLinks(baseUri);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Add track to playlist")
    @PostMapping("/{playlistId}/add/{trackId}")
    public ResponseEntity<Void> addTrackToPlaylist(@PathVariable Long playlistId, @PathVariable Long trackId) {
        playlistTrackService.addTrackToPlaylist(playlistId, trackId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove track from playlist")
    @DeleteMapping("/{playlistId}/remove/{trackId}")
    public ResponseEntity<Void> removeTrackFromPlaylist(@PathVariable Long playlistId, @PathVariable Long trackId) {
        playlistTrackService.removeTrackFromPlaylist(playlistId, trackId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get playlist's tracks")
    @GetMapping("/{playlistId}/tracks")
    public ResponseEntity<PageResponse<TrackResponse>> getTracksByPlaylist(
            @PathVariable Long playlistId,
            @ParameterObject @Valid TrackParams params
            ){
        PageResponse<TrackResponse> responses = trackService.getTracksByPlaylist(playlistId, params);
        String baseUri = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        responses.addPaginationLinks(baseUri);
        return ResponseEntity.ok(responses);
    }
}
