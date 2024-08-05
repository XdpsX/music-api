package com.xdpsx.music.controller;

import com.xdpsx.music.dto.common.PageResponse;
import com.xdpsx.music.dto.request.TrackRequest;
import com.xdpsx.music.dto.request.params.TrackParams;
import com.xdpsx.music.dto.response.TrackResponse;
import com.xdpsx.music.model.entity.User;
import com.xdpsx.music.security.UserContext;
import com.xdpsx.music.service.LikeService;
import com.xdpsx.music.service.TrackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tracks")
@RequiredArgsConstructor
public class TrackController {
    private final UserContext userContext;
    private final TrackService trackService;
    private final LikeService likeService;

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
    public ResponseEntity<PageResponse<TrackResponse>> getAllTracks(
            @Valid TrackParams params
    ) {
        PageResponse<TrackResponse> responses = trackService.getAllTracks(params);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrack(@PathVariable Long id) {
        trackService.deleteTrack(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{trackId}/likes")
    public ResponseEntity<?> likeTrack(@PathVariable Long trackId){
        User loggedUser = userContext.getLoggedUser();
        likeService.likeTrack(trackId, loggedUser);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{trackId}/unlikes")
    public ResponseEntity<?> unlikeTrack(@PathVariable Long trackId){
        User loggedUser = userContext.getLoggedUser();
        likeService.unlikeTrack(trackId, loggedUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/likes/contains")
    public ResponseEntity<List<Boolean>> checkLikesTrack(
            @RequestParam List<Long> trackIds) {
        User loggedUser = userContext.getLoggedUser();
        List<Boolean> responses = trackIds.stream()
                .map(trackId -> likeService.isTrackLikedByUser(trackId, loggedUser.getId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}
