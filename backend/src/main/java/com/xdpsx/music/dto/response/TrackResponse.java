package com.xdpsx.music.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class TrackResponse {
    private Long id;
    private String name;
    private Integer durationMs;
    private String image;
    private String url;
    private LocalDateTime createdAt;
    private Integer trackNumber;
    private AlbumResponse album;
    private GenreResponse genre;
    private List<ArtistResponse> artists;
}
