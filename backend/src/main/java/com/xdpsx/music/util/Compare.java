package com.xdpsx.music.util;

import com.xdpsx.music.entity.Artist;

import java.util.List;

public class Compare {
    public static boolean isSameArtists(List<Artist> artistList, List<Long> ids){
        boolean areEqual = true;
        for (Artist artist : artistList) {
            if (!ids.contains(artist.getId())) {
                areEqual = false;
                break;
            }
        }
        return areEqual;
    }
}
