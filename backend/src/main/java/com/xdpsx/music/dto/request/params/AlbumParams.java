package com.xdpsx.music.dto.request.params;

import com.xdpsx.music.dto.common.PageParams;
import com.xdpsx.music.validator.SortFieldConstraint;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.*;

import static com.xdpsx.music.constant.PageConstants.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AlbumParams extends PageParams {
    @Parameter(description = "search by name")
    private String search;

    @Parameter(description = "sort by " + DATE_FIELD + ", " + NAME_FIELD + ", " + TOTAL_TRACKS_FIELD,
            example = DEFAULT_SORT_FIELD)
    @SortFieldConstraint(sortFields = {DATE_FIELD, NAME_FIELD, TOTAL_TRACKS_FIELD})
    private String sort = DEFAULT_SORT_FIELD;

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s", getPageNum(), getPageSize(), getSearch(), getSort());
    }
}
