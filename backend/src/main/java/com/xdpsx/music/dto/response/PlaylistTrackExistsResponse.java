package com.xdpsx.music.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistTrackExistsResponse {
    private Long playlistId;
    private Long trackId;
    private boolean exists;
}
