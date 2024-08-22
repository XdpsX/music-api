package com.xdpsx.music.controller;

import com.xdpsx.music.dto.common.PageResponse;
import com.xdpsx.music.dto.request.params.AlbumParams;
import com.xdpsx.music.dto.request.GenreRequest;
import com.xdpsx.music.dto.request.params.TrackParams;
import com.xdpsx.music.dto.response.AlbumResponse;
import com.xdpsx.music.dto.response.GenreResponse;
import com.xdpsx.music.dto.response.TrackResponse;
import com.xdpsx.music.service.AlbumService;
import com.xdpsx.music.service.GenreService;
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

import java.util.List;

@Tag(name = "REST APIs for Genre")
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;
    private final AlbumService albumService;
    private final TrackService trackService;

    @Operation(summary = "Create new genre", description = "Need Role Admin")
    @SecurityRequirement(name = "JWT")
    @PostMapping(path="/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenreResponse> createGenre(
            @ParameterObject @Valid @ModelAttribute GenreRequest request,
            @RequestParam MultipartFile image
            ){
        GenreResponse response = genreService.createGenre(request, image);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all genres")
    @GetMapping
    public ResponseEntity<List<GenreResponse>> fetchAllGenres(){
        List<GenreResponse> responses = genreService.getAllGenres();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Delete genre by id", description = "Need Role Admin")
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/{genreId}")
    public ResponseEntity<Void> deleteGenre(@PathVariable Integer genreId){
        genreService.deleteGenre(genreId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get genre's albums")
    @GetMapping("/{genreId}/albums")
    public ResponseEntity<PageResponse<AlbumResponse>> getAlbumsByGenre(
            @PathVariable Integer genreId,
            @ParameterObject @Valid AlbumParams params
    ){
        PageResponse<AlbumResponse> responses = albumService.getAlbumsByGenreId(genreId, params);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get genre's tracks")
    @GetMapping("/{genreId}/tracks")
    public ResponseEntity<PageResponse<TrackResponse>> getTracksByGenre(
            @PathVariable Integer genreId,
            @ParameterObject @Valid TrackParams params
    ){
        PageResponse<TrackResponse> responses = trackService.getTracksByGenreId(genreId, params);
        return ResponseEntity.ok(responses);
    }
}
