package com.xdpsx.music.service;

import com.xdpsx.music.dto.request.GenreRequest;
import com.xdpsx.music.dto.response.GenreResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GenreService {
    GenreResponse createGenre(GenreRequest request, MultipartFile image);
    List<GenreResponse> getAllGenres();
    void deleteGenre(Integer genreId);
}
