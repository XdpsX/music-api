package com.xdpsx.music.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistTrackExistsRequest {
    @NotNull
    private List<Long> playlistIds;
    @NotNull
    private List<Long> trackIds;
}
