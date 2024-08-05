package com.xdpsx.music.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank
    private String resetCode;

    @NotBlank
    @Size(min = 8)
    private String newPassword;
}
