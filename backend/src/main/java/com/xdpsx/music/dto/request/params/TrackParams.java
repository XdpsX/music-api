package com.xdpsx.music.dto.request.params;

import com.xdpsx.music.dto.common.PageParams;
import com.xdpsx.music.validator.SortFieldConstraint;
import lombok.*;

import static com.xdpsx.music.constant.PageConstants.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TrackParams extends PageParams {
    private String search;

    @SortFieldConstraint(sortFields = {DATE_FIELD, NAME_FIELD, TOTAL_LIKES_FIELD, LISTENING_FIELD})
    private String sort = DEFAULT_SORT_FIELD;

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s", getPageNum(), getPageSize(), getSearch(), getSort());
    }
}
