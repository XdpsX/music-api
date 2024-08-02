package com.xdpsx.music.service;

import com.xdpsx.music.dto.request.params.ArtistParams;
import com.xdpsx.music.dto.request.ArtistRequest;
import com.xdpsx.music.dto.response.ArtistResponse;
import com.xdpsx.music.dto.common.PageResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ArtistService {
    PageResponse<ArtistResponse> getAllArtists(ArtistParams params);
    ArtistResponse createArtist(ArtistRequest request, MultipartFile image);
    ArtistResponse updateArtist(Long id, ArtistRequest request, MultipartFile image);
    ArtistResponse getArtistById(Long id);
    void deleteArtist(Long id);
}
