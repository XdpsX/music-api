package com.xdpsx.music.controller;

import com.xdpsx.music.dto.request.params.AlbumParams;
import com.xdpsx.music.dto.request.params.ArtistParams;
import com.xdpsx.music.dto.request.ArtistRequest;
import com.xdpsx.music.dto.request.params.TrackParams;
import com.xdpsx.music.dto.response.AlbumResponse;
import com.xdpsx.music.dto.response.ArtistResponse;
import com.xdpsx.music.dto.common.PageResponse;
import com.xdpsx.music.dto.response.TrackResponse;
import com.xdpsx.music.service.AlbumService;
import com.xdpsx.music.service.ArtistService;
import com.xdpsx.music.service.TrackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/artists")
@RequiredArgsConstructor
public class ArtistController {
    private final ArtistService artistService;
    private final AlbumService albumService;
    private final TrackService trackService;

    @PostMapping
    public ResponseEntity<ArtistResponse> createArtist(
            @Valid @ModelAttribute ArtistRequest request,
            @RequestParam(required = false) MultipartFile image
    ) {
        ArtistResponse response = artistService.createArtist(request, image);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArtistResponse> updateArtist(
            @PathVariable Long id,
            @Valid @ModelAttribute ArtistRequest request,
            @RequestParam(required = false) MultipartFile image) {
        ArtistResponse response = artistService.updateArtist(id, request, image);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable Long id) {
        artistService.deleteArtist(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistResponse> getArtistById(@PathVariable Long id) {
        ArtistResponse response = artistService.getArtistById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<ArtistResponse>> getAllArtists(
            @Valid ArtistParams params
    ) {
        PageResponse<ArtistResponse> responses = artistService.getAllArtists(params);
        // Get the base URI of the current request
        String baseUri = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        responses.addPaginationLinks(baseUri);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{artistId}/albums")
    public ResponseEntity<PageResponse<AlbumResponse>> getAlbumsByArtist(
            @PathVariable Long artistId,
            @Valid AlbumParams params
    ){
        PageResponse<AlbumResponse> responses = albumService.getAlbumsByArtistId(artistId, params);
        String baseUri = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        responses.addPaginationLinks(baseUri);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{artistId}/tracks")
    public ResponseEntity<PageResponse<TrackResponse>> getTracksByArtist(
            @PathVariable Long artistId,
            @Valid TrackParams params
    ){
        PageResponse<TrackResponse> responses = trackService.getTracksByArtistId(artistId, params);
        String baseUri = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        responses.addPaginationLinks(baseUri);
        return ResponseEntity.ok(responses);
    }
}
