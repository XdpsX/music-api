package com.xdpsx.music.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GenreRequest {
    @NotBlank(message = "Genre name is required")
    @Size(max = 64, message = "Genre name cannot exceed 64 characters")
    private String name;
}
