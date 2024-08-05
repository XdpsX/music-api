package com.xdpsx.music.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserProfileRequest {
    @NotBlank
    @Size(max=128)
    private String name;
}
