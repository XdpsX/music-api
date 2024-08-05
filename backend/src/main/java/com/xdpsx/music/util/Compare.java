package com.xdpsx.music.util;

import com.xdpsx.music.model.entity.Artist;

import java.util.List;
import java.util.stream.Collectors;

public class Compare {
    public static boolean isSameArtists(List<Artist> artistList, List<Long> ids){
        boolean areEqual = true;
        List<Long> artistIds = artistList.stream()
                .map(Artist::getId)
                .collect(Collectors.toList());
        for (Long id : ids) {
            if (!artistIds.contains(id)) {
                areEqual = false;
                break;
            }
        }
        return areEqual;
    }
}
