package com.xdpsx.music.dto.request;

import com.xdpsx.music.dto.common.PageParams;
import com.xdpsx.music.entity.Gender;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class ArtistParams extends PageParams {
    private Gender gender;
}
