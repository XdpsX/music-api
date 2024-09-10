package com.xdpsx.music.controller;

import com.xdpsx.music.dto.common.ErrorDTO;
import com.xdpsx.music.dto.common.ErrorDetails;
import com.xdpsx.music.dto.common.PageResponse;
import com.xdpsx.music.dto.request.TrackRequest;
import com.xdpsx.music.dto.request.params.TrackParams;
import com.xdpsx.music.dto.response.MessageResponse;
import com.xdpsx.music.dto.response.TrackResponse;
import com.xdpsx.music.model.entity.User;
import com.xdpsx.music.security.UserContext;
import com.xdpsx.music.service.LikeService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "REST APIs for Track")
@RestController
@RequestMapping("/tracks")
@RequiredArgsConstructor
public class TrackController {
    private final UserContext userContext;
    private final TrackService trackService;
    private final LikeService likeService;
    private final I18nUtils i18nUtils;

    @Operation(summary = "Create new track", description = "Need Role Admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TrackResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )),
    })
    @SecurityRequirement(name = "JWT")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TrackResponse> createTrack(
            @ParameterObject @Valid @ModelAttribute TrackRequest request,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam MultipartFile file
    ) {
        TrackResponse response = trackService.createTrack(request, image, file);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Update track", description = "Need Role Admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TrackResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )),
    })
    @SecurityRequirement(name = "JWT")
    @PutMapping(path="/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TrackResponse> updateTrack(
            @PathVariable Long id,
            @ParameterObject @Valid @ModelAttribute TrackRequest request,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam(required = false) MultipartFile file
    ) {
        TrackResponse response = trackService.updateTrack(id, request, image, file);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get track by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TrackResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )),
    })
    @GetMapping("/{id}")
    public ResponseEntity<TrackResponse> getTrackById(@PathVariable Long id) {
        TrackResponse response = trackService.getTrackById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all tracks")
    @GetMapping
    public ResponseEntity<PageResponse<TrackResponse>> getAllTracks(
            @ParameterObject @Valid TrackParams params
    ) {
        PageResponse<TrackResponse> responses = trackService.getAllTracks(params);
        String baseUri = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        responses.addPaginationLinks(baseUri);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Delete track", description = "Need Role Admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )),
    })
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrack(@PathVariable Long id) {
        trackService.deleteTrack(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Like track")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(oneOf = { ErrorDTO.class, ErrorDetails.class })
                    )),
    })
    @SecurityRequirement(name = "JWT")
    @PostMapping("/{trackId}/likes")
    public ResponseEntity<?> likeTrack(@PathVariable Long trackId){
        User loggedUser = userContext.getLoggedUser();
        likeService.likeTrack(trackId, loggedUser);
        return ResponseEntity.ok(i18nUtils.getLikeTrackSucMsg());
    }

    @Operation(summary = "Unlike track")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(oneOf = { ErrorDTO.class, ErrorDetails.class })
                    )),
    })
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/{trackId}/unlikes")
    public ResponseEntity<?> unlikeTrack(@PathVariable Long trackId){
        User loggedUser = userContext.getLoggedUser();
        likeService.unlikeTrack(trackId, loggedUser);
        return ResponseEntity.ok(i18nUtils.getUnlikeTrackSucMsg());
    }

    @Operation(summary = "Check user likes tracks")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/likes/contains")
    public ResponseEntity<List<Boolean>> checkLikesTrack(
            @RequestParam List<Long> trackIds) {
        User loggedUser = userContext.getLoggedUser();
        List<Boolean> responses = trackIds.stream()
                .map(trackId -> likeService.isTrackLikedByUser(trackId, loggedUser.getId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Increase track's listening count")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )),
    })
    @SecurityRequirement(name = "JWT")
    @PostMapping("/{trackId}/listen")
    public ResponseEntity<?> incrementListeningCount(@PathVariable Long trackId) {
        User loggedUser = userContext.getLoggedUser();
        trackService.incrementListeningCount(trackId, loggedUser);
        return ResponseEntity.ok().build();
    }
}
