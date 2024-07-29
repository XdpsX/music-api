package com.xdpsx.music.service;

import com.xdpsx.music.dto.request.ArtistRequest;
import com.xdpsx.music.dto.response.ArtistResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ArtistService {
    List<ArtistResponse> getAllArtists();
    ArtistResponse createArtist(ArtistRequest request, MultipartFile image);
    ArtistResponse updateArtist(Long id, ArtistRequest request, MultipartFile image);
    ArtistResponse getArtistById(Long id);
    void deleteArtist(Long id);
}
