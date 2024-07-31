package com.xdpsx.music.service;

import com.xdpsx.music.dto.request.TrackRequest;
import com.xdpsx.music.dto.response.TrackResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TrackService {
    TrackResponse createTrack(TrackRequest request, MultipartFile image, MultipartFile file);
    TrackResponse updateTrack(Long id, TrackRequest request, MultipartFile newImage, MultipartFile newFile);
    TrackResponse getTrackById(Long id);
    List<TrackResponse> getAllTracks();
    void deleteTrack(Long id);
}
