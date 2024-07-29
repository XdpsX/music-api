package com.xdpsx.music.dto.common;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import static com.xdpsx.music.constant.PageConstants.*;

@Data
public class PageParams {
    @Min(value = 1, message = "Page number must be at least 1")
    private Integer pageNum = 1;

    @Min(value = MIN_ITEMS_PER_PAGE, message = "Page size must be at least " + MIN_ITEMS_PER_PAGE)
    @Max(value = MAX_ITEMS_PER_PAGE, message = "Page size can not be greater than " + MAX_ITEMS_PER_PAGE)
    private Integer pageSize = MIN_ITEMS_PER_PAGE;

    private String search;

    private String sort = DEFAULT_SORT_FIELD;
}
