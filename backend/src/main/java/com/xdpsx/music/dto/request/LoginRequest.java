package com.xdpsx.music.dto.request;

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
    @Email(message = "Email is in wrong format")
    @NotBlank
    @Size(max=128)
    private String email;

    @NotBlank
    private String password;
}
