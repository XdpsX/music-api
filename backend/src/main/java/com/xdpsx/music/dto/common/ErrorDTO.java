package com.xdpsx.music.dto.common;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class ErrorDTO {
    private Date timestamp;
    private int status;
    private String path;
    private String error;
}
