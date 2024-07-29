package com.xdpsx.music.dto.request;

import com.xdpsx.music.dto.common.PageParams;
import com.xdpsx.music.entity.Gender;
import lombok.Data;

@Data
public class ArtistParams extends PageParams {
    private Gender gender;
}
