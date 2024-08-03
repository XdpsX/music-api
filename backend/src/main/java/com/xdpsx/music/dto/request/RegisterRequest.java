package com.xdpsx.music.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterRequest {
    @NotBlank
    @Size(max=128)
    private String name;

    @Email(message = "Email is in wrong format")
    @NotBlank
    @Size(max=128)
    private String email;

    @NotBlank
    @Size(min = 6, message = "Password should be 8 characters long minimum")
    private String password;
}
