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
public class UserParams extends PageParams {
    @Parameter(description = "search by name")
    private String search;

    @Parameter(description = "sort by " + DATE_FIELD + ", " + NAME_FIELD, example = DEFAULT_SORT_FIELD)
    @SortFieldConstraint(sortFields = {DATE_FIELD, NAME_FIELD})
    private String sort = DEFAULT_SORT_FIELD;

    private Boolean accountLocked;

    private Boolean enabled;
}
