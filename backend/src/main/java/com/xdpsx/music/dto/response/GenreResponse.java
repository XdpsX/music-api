package com.xdpsx.music.dto.response;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GenreResponse implements Serializable {
    private Integer id;
    private String name;
    private String image;
}
