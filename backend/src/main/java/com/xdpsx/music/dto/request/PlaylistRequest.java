package com.xdpsx.music.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PlaylistRequest {
    @NotBlank
    @Size(min = 3, max = 128)
    private String name;
}
