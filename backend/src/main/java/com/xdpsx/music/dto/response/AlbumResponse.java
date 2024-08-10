package com.xdpsx.music.dto.response;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumResponse implements Serializable {
    private Long id;
    private String name;
    private String image;
    private LocalDate releaseDate;
    private int totalTracks;
    private GenreResponse genre;
    private List<ArtistResponse> artists;
}
