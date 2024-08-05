package com.xdpsx.music.dto.request.params;

import com.xdpsx.music.dto.common.PageParams;
import com.xdpsx.music.validator.SortFieldConstraint;
import lombok.Data;

import static com.xdpsx.music.constant.PageConstants.*;
import static com.xdpsx.music.constant.PageConstants.DEFAULT_SORT_FIELD;

@Data
public class PlaylistParam extends PageParams {
    private String search;

    @SortFieldConstraint(sortFields = {DATE_FIELD, NAME_FIELD, NUM_TRACKS_FIELD})
    private String sort = DEFAULT_SORT_FIELD;
}
