package com.xdpsx.music.dto.response;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String avatar;
    private String email;
    private boolean accountLocked;
    private boolean enabled;
}
