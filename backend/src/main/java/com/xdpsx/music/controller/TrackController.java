package com.xdpsx.music.controller;

import com.xdpsx.music.dto.request.TrackRequest;
import com.xdpsx.music.dto.response.TrackResponse;
import com.xdpsx.music.service.TrackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/tracks")
@RequiredArgsConstructor
public class TrackController {
    private final TrackService trackService;

    @PostMapping
    public ResponseEntity<TrackResponse> createTrack(
            @Valid @ModelAttribute TrackRequest request,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam MultipartFile file
    ) {
        TrackResponse response = trackService.createTrack(request, image, file);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrackResponse> updateTrack(
            @PathVariable Long id,
            @Valid @ModelAttribute TrackRequest request,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam(required = false) MultipartFile file
    ) {
        TrackResponse response = trackService.updateTrack(id, request, image, file);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrackResponse> getTrackById(@PathVariable Long id) {
        TrackResponse response = trackService.getTrackById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TrackResponse>> getAllTracks() {
        List<TrackResponse> responses = trackService.getAllTracks();
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrack(@PathVariable Long id) {
        trackService.deleteTrack(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
