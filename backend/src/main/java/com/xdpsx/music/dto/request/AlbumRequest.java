package com.xdpsx.music.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumRequest {
    @NotBlank
    @Size(min = 3, max = 128)
    private String name;

    @NotNull
    private LocalDate releaseDate;

    @NotNull
    private Integer genreId;

    @NotNull
    @Size(min = 1, message = "Album must have at least 1 artist")
    private List<Long> artistIds;
}
