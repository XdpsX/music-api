package com.xdpsx.music.dto.response;

import com.xdpsx.music.entity.Artist;
import com.xdpsx.music.entity.Genre;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class AlbumResponse {
    private Long id;
    private String name;
    private String image;
    private LocalDate releaseDate;
    private GenreResponse genre;
    private List<ArtistResponse> artists;
}
