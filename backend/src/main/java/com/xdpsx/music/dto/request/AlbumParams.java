package com.xdpsx.music.dto.request;

import com.xdpsx.music.dto.common.PageParams;
import com.xdpsx.music.validator.SortFieldConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.xdpsx.music.constant.PageConstants.DEFAULT_SORT_FIELD;

@Data
@NoArgsConstructor
public class AlbumParams extends PageParams {
    private String search;

    @SortFieldConstraint(sortFields = {"date", "name", "numTracks"})
    private String sort = DEFAULT_SORT_FIELD;
}
