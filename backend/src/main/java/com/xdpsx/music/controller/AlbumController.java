package com.xdpsx.music.controller;

import com.xdpsx.music.dto.common.PageResponse;
import com.xdpsx.music.dto.request.params.AlbumParams;
import com.xdpsx.music.dto.request.AlbumRequest;
import com.xdpsx.music.dto.request.params.TrackParams;
import com.xdpsx.music.dto.response.AlbumResponse;
import com.xdpsx.music.dto.response.TrackResponse;
import com.xdpsx.music.service.AlbumService;
import com.xdpsx.music.service.TrackService;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name = "REST APIs for Album")
@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;
    private final TrackService trackService;

    @Operation(summary = "Create new album", description = "Need Role Admin")
    @SecurityRequirement(name = "JWT")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AlbumResponse> createAlbum(
            @ParameterObject @Valid @ModelAttribute AlbumRequest request,
            @RequestParam(required = false) MultipartFile image
    ) {
        AlbumResponse response = albumService.createAlbum(request, image);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Update album", description = "Need Role Admin")
    @SecurityRequirement(name = "JWT")
    @PutMapping(path="/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AlbumResponse> updateAlbum(
            @PathVariable Long id,
            @ParameterObject @Valid @ModelAttribute AlbumRequest request,
            @RequestParam(required = false) MultipartFile image
    ) {
        AlbumResponse response = albumService.updateAlbum(id, request, image);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete album by id", description = "Need Role Admin")
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable Long id) {
        albumService.deleteAlbum(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get album by id")
    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponse> getAlbumById(@PathVariable Long id) {
        AlbumResponse response = albumService.getAlbumById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all albums")
    @GetMapping
    public ResponseEntity<PageResponse<AlbumResponse>> getAllAlbums(
            @ParameterObject @Valid AlbumParams params
    ) {
        PageResponse<AlbumResponse> responses = albumService.getAllAlbums(params);
        String baseUri = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        responses.addPaginationLinks(baseUri);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get album's tracks")
    @GetMapping("/{albumId}/tracks")
    public ResponseEntity<PageResponse<TrackResponse>> getTracksByAlbum(
            @PathVariable Long albumId,
            @ParameterObject @Valid TrackParams params
    ) {
        PageResponse<TrackResponse> response = trackService.getTracksByAlbumId(albumId, params);
        String baseUri = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        response.addPaginationLinks(baseUri);
        return ResponseEntity.ok(response);
    }
}
