package com.xdpsx.music.dto.request;

import com.xdpsx.music.dto.common.PageParams;
import com.xdpsx.music.entity.Gender;
import com.xdpsx.music.validator.SortFieldConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.xdpsx.music.constant.PageConstants.DEFAULT_SORT_FIELD;


@Data
@NoArgsConstructor
public class ArtistParams extends PageParams {
    private String search;

    @SortFieldConstraint(sortFields = {"date", "name"})
    private String sort = DEFAULT_SORT_FIELD;

    private Gender gender;
}
