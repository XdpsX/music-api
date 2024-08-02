package com.xdpsx.music.controller;

import com.xdpsx.music.dto.common.PageResponse;
import com.xdpsx.music.dto.request.params.AlbumParams;
import com.xdpsx.music.dto.request.AlbumRequest;
import com.xdpsx.music.dto.request.params.TrackParams;
import com.xdpsx.music.dto.response.AlbumResponse;
import com.xdpsx.music.dto.response.TrackResponse;
import com.xdpsx.music.service.AlbumService;
import com.xdpsx.music.service.TrackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;
    private final TrackService trackService;

    @PostMapping
    public ResponseEntity<AlbumResponse> createAlbum(
            @Valid @ModelAttribute AlbumRequest request,
            @RequestParam(required = false) MultipartFile image
    ) {
        AlbumResponse response = albumService.createAlbum(request, image);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlbumResponse> updateAlbum(
            @PathVariable Long id,
            @Valid @ModelAttribute AlbumRequest request,
            @RequestParam(required = false) MultipartFile image
    ) {
        AlbumResponse response = albumService.updateAlbum(id, request, image);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable Long id) {
        albumService.deleteAlbum(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponse> getAlbumById(@PathVariable Long id) {
        AlbumResponse response = albumService.getAlbumById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<AlbumResponse>> getAllAlbums(
            @Valid AlbumParams params
    ) {
        PageResponse<AlbumResponse> responses = albumService.getAllAlbums(params);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{albumId}/tracks")
    public ResponseEntity<PageResponse<TrackResponse>> getTracksByAlbum(
            @PathVariable Long albumId,
            @Valid TrackParams params
    ) {
        PageResponse<TrackResponse> response = trackService.getTracksByAlbumId(albumId, params);
        return ResponseEntity.ok(response);
    }
}
