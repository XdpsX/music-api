package com.xdpsx.music.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GenreResponse {
    private Integer id;
    private String name;
    private String image;
}
