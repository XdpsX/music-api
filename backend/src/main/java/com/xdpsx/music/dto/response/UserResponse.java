package com.xdpsx.music.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String avatar;
    private String email;
    @JsonProperty("account_locked")
    private boolean accountLocked;
    private boolean enabled;
}
