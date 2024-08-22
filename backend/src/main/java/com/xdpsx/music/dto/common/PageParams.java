package com.xdpsx.music.dto.common;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import static com.xdpsx.music.constant.PageConstants.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageParams {
    @Parameter(description = "page number", example = "1")
    @Min(value = 1, message = "Page number must be at least 1")
    private Integer pageNum = 1;

    @Parameter(description = "page size", example = "" + DEFAULT_ITEMS_PER_PAGE)
    @Min(value = MIN_ITEMS_PER_PAGE, message = "Page size must be at least " + MIN_ITEMS_PER_PAGE)
    @Max(value = MAX_ITEMS_PER_PAGE, message = "Page size can not be greater than " + MAX_ITEMS_PER_PAGE)
    private Integer pageSize = DEFAULT_ITEMS_PER_PAGE;

}
