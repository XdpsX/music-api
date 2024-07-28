package com.xdpsx.music.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDetails {
    private Date timestamp;
    private int status;
    private String path;
    private String error;
    private Map<String, String> details;

    public ErrorDetails(String error) {
        this.timestamp = new Date();
        this.error = error;
    }
}
