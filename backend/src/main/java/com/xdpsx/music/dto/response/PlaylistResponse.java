package com.xdpsx.music.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PlaylistResponse {
    private Long id;
    private String name;
    private int totalTracks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserProfileResponse owner;
}
