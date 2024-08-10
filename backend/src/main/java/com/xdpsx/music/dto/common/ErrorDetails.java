package com.xdpsx.music.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDetails {
    private Date timestamp;
    private int status;
    private String path;
    private String error;
    private Map<String, String> details;

    public ErrorDetails() {
        this.timestamp = new Date();
    }

    public ErrorDetails(String error) {
        this.timestamp = new Date();
        this.error = error;
    }
}
