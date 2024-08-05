package com.xdpsx.music.dto.request;

import com.xdpsx.music.model.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ArtistRequest {
    @NotBlank
    @Size(max = 128, message = "Artist name can not exceed 128 characters")
    private String name;

    @NotNull
    private Gender gender;

    private String description;

    @Past(message = "Artist dob must be in the past")
    private LocalDate dob;
}
