package com.xdpsx.music.dto.response;

import lombok.Data;

@Data
public class UserProfileResponse {
    private Long id;
    private String name;
    private String avatar;
    private String email;
}
