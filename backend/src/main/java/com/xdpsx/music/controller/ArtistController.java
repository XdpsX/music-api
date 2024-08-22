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

@Tag(name = "REST APIs for Artist")
@RestController
@RequestMapping("/artists")
@RequiredArgsConstructor
public class ArtistController {
    private final ArtistService artistService;
    private final AlbumService albumService;
    private final TrackService trackService;

    @Operation(summary = "Create new artist", description = "Need Role Admin")
    @SecurityRequirement(name = "JWT")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArtistResponse> createArtist(
            @ParameterObject @Valid @ModelAttribute ArtistRequest request,
            @RequestParam(required = false) MultipartFile image
    ) {
        ArtistResponse response = artistService.createArtist(request, image);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Update artist", description = "Need Role Admin")
    @SecurityRequirement(name = "JWT")
    @PutMapping(path="/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArtistResponse> updateArtist(
            @PathVariable Long id,
            @ParameterObject @Valid @ModelAttribute ArtistRequest request,
            @RequestParam(required = false) MultipartFile image) {
        ArtistResponse response = artistService.updateArtist(id, request, image);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete artist by id", description = "Need Role Admin")
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable Long id) {
        artistService.deleteArtist(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get artist by id")
    @GetMapping("/{id}")
    public ResponseEntity<ArtistResponse> getArtistById(@PathVariable Long id) {
        ArtistResponse response = artistService.getArtistById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all artists")
    @GetMapping
    public ResponseEntity<PageResponse<ArtistResponse>> getAllArtists(
            @ParameterObject @Valid ArtistParams params
    ) {
        PageResponse<ArtistResponse> responses = artistService.getAllArtists(params);
        // Get the base URI of the current request
        String baseUri = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        responses.addPaginationLinks(baseUri);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get artist's albums")
    @GetMapping("/{artistId}/albums")
    public ResponseEntity<PageResponse<AlbumResponse>> getAlbumsByArtist(
            @PathVariable Long artistId,
            @ParameterObject @Valid AlbumParams params
    ){
        PageResponse<AlbumResponse> responses = albumService.getAlbumsByArtistId(artistId, params);
        String baseUri = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        responses.addPaginationLinks(baseUri);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get artist's tracks")
    @GetMapping("/{artistId}/tracks")
    public ResponseEntity<PageResponse<TrackResponse>> getTracksByArtist(
            @PathVariable Long artistId,
            @ParameterObject @Valid TrackParams params
    ){
        PageResponse<TrackResponse> responses = trackService.getTracksByArtistId(artistId, params);
        String baseUri = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        responses.addPaginationLinks(baseUri);
        return ResponseEntity.ok(responses);
    }
}
