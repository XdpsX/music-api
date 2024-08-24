package com.xdpsx.music.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"playlistId", "trackId", "exists"})
public class PlaylistTrackExistsResponse {
    @JsonProperty("playlist_id")
    private Long playlistId;
    @JsonProperty("track_id")
    private Long trackId;
    private boolean exists;
}
