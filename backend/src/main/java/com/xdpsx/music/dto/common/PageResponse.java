package com.xdpsx.music.dto.common;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Data
@Builder
public class PageResponse<T> {
    private Collection<T> items;
    private int pageNum;
    private int pageSize;
    private long totalItems;
    private int totalPages;
}
