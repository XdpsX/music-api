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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;
    private final AlbumService albumService;
    private final TrackService trackService;

    @PostMapping("/create")
    public ResponseEntity<GenreResponse> createGenre(
            @Valid @ModelAttribute GenreRequest request,
            @RequestParam MultipartFile image
            ){
        GenreResponse response = genreService.createGenre(request, image);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<GenreResponse>> fetchAllGenres(){
        List<GenreResponse> responses = genreService.getAllGenres();
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{genreId}")
    public ResponseEntity<Void> deleteGenre(@PathVariable Integer genreId){
        genreService.deleteGenre(genreId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{genreId}/albums")
    public ResponseEntity<PageResponse<AlbumResponse>> getAlbumsByGenre(
            @PathVariable Integer genreId,
            @Valid AlbumParams params
    ){
        PageResponse<AlbumResponse> responses = albumService.getAlbumsByGenreId(genreId, params);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{genreId}/tracks")
    public ResponseEntity<PageResponse<TrackResponse>> getTracksByGenre(
            @PathVariable Integer genreId,
            @Valid TrackParams params
    ){
        PageResponse<TrackResponse> responses = trackService.getTracksByGenreId(genreId, params);
        return ResponseEntity.ok(responses);
    }
}
