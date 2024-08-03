package com.xdpsx.music.constant;

public class SecurityConstants {
    public static final String GENRE_URL = "/genres/**";
    public static final String ALBUM_URL = "/albums/**";
    public static final String ARTIST_URL = "/artists/**";
    public static final String TRACK_URL = "/tracks/**";
    public static final String AUTH_URL = "/auth/**";

    public static final String[] PUBLIC_URLS = {
            AUTH_URL
    };

    public static final String[] PUBLIC_GET_URLS = {
            GENRE_URL,
            ALBUM_URL,
            ARTIST_URL,
            TRACK_URL
    };
}
