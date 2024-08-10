package com.xdpsx.music.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistResponse {
    private Long id;
    private String name;
    private int totalTracks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserProfileResponse owner;
}
