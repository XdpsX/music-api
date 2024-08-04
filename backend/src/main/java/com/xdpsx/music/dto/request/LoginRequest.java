package com.xdpsx.music.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequest {
    @Email(message = "Email is in wrong format")
    @NotBlank
    @Size(max=128)
    private String email;

    @NotBlank
    private String password;
}
