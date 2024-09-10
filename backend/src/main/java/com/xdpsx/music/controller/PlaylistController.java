package com.xdpsx.music.controller;

import com.xdpsx.music.dto.common.ErrorDTO;
import com.xdpsx.music.dto.common.ErrorDetails;
import com.xdpsx.music.dto.common.PageResponse;
import com.xdpsx.music.dto.request.PlaylistRequest;
import com.xdpsx.music.dto.request.PlaylistTrackExistsRequest;
import com.xdpsx.music.dto.request.params.PlaylistParam;
import com.xdpsx.music.dto.request.params.TrackParams;
import com.xdpsx.music.dto.response.MessageResponse;
import com.xdpsx.music.dto.response.PlaylistResponse;
import com.xdpsx.music.dto.response.PlaylistTrackExistsResponse;
import com.xdpsx.music.dto.response.TrackResponse;
import com.xdpsx.music.service.PlaylistService;
import com.xdpsx.music.service.PlaylistTrackService;
import com.xdpsx.music.service.TrackService;
import com.xdpsx.music.util.I18nUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@Tag(name = "REST APIs for Playlist")
@SecurityRequirement(name = "JWT")
@RestController
@RequestMapping("/playlists")
@RequiredArgsConstructor
public class PlaylistController {
    private final PlaylistService playlistService;
    private final PlaylistTrackService playlistTrackService;
    private final TrackService trackService;
    private final I18nUtils i18nUtils;

    @Operation(summary = "Create new playlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PlaylistResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetails.class)
                    )),
            @ApiResponse(responseCode = "409", description = "Duplicate",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )),
    })
    @PostMapping
    public ResponseEntity<PlaylistResponse> createPlaylist(
            @Valid @RequestBody PlaylistRequest request
    ){
        PlaylistResponse response = playlistService.createPlaylist(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get playlist by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PlaylistResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetails.class)
                    )),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )),
    })
    @GetMapping("/{id}")
    public ResponseEntity<PlaylistResponse> getPlaylistById(@PathVariable Long id) {
        PlaylistResponse response = playlistService.getPlaylistById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update playlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PlaylistResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )),
    })
    @PutMapping("/{id}")
    public ResponseEntity<PlaylistResponse> updatePlaylist(
            @PathVariable Long id,
            @Valid @RequestBody PlaylistRequest request
    ) {
        PlaylistResponse response = playlistService.updatePlaylist(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete playlist by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )),
    })
    @DeleteMapping("/{playlistId}")
    public ResponseEntity<Void> deletePlaylist(
            @PathVariable Long playlistId
    ){
        playlistService.deletePlaylist(playlistId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get user's playlists")
    @GetMapping("/my")
    public ResponseEntity<PageResponse<PlaylistResponse>> getAllUserPlaylists(
            @ParameterObject @Valid PlaylistParam params
    ){
        PageResponse<PlaylistResponse> responses = playlistService.getAllUserPlaylists(params);
        String baseUri = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        responses.addPaginationLinks(baseUri);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Add track to playlist")
    @PostMapping("/{playlistId}/add/{trackId}")
    public ResponseEntity<MessageResponse> addTrackToPlaylist(@PathVariable Long playlistId, @PathVariable Long trackId) {
        playlistTrackService.addTrackToPlaylist(playlistId, trackId);
        return ResponseEntity.ok(new MessageResponse(i18nUtils.getAddTrackPlaylistMsg()));
    }

    @Operation(summary = "Remove track from playlist")
    @DeleteMapping("/{playlistId}/remove/{trackId}")
    public ResponseEntity<MessageResponse> removeTrackFromPlaylist(@PathVariable Long playlistId, @PathVariable Long trackId) {
        playlistTrackService.removeTrackFromPlaylist(playlistId, trackId);
        return ResponseEntity.ok(new MessageResponse(i18nUtils.getRemoveTrackPlaylistMsg()));
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

    @Operation(summary = "Check tracks in user playlists")
    @PostMapping("/tracks/exists")
    public ResponseEntity<List<PlaylistTrackExistsResponse>> checkTracksInPlaylists(
            @Valid @RequestBody PlaylistTrackExistsRequest request) {
        List<PlaylistTrackExistsResponse> responses = playlistService.checkTracksInPlaylists(
                request.getPlaylistIds(), request.getTrackIds());
        return ResponseEntity.ok(responses);
    }
}
