package com.xdpsx.music.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    @Schema(description = "User's email address", example = "admin@xdpsx.com")
    @Email(message = "Email is in wrong format")
    @NotBlank
    @Size(max=128)
    private String email;

    @Schema(description = "User's password", example = "password")
    @NotBlank
    private String password;
}
