package com.xdpsx.music.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackRequest {
    @NotBlank
    @Size(max = 128)
    private String name;

    @NotNull
    @Positive
    private Integer durationMs;

    private Long albumId;

    @NotNull
    private Integer genreId;

    @NotNull
    @Size(min = 1, message = "Track must have at least 1 artist")
    private List<Long> artistIds;
}
