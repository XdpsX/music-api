package com.xdpsx.music.constant;

public class Keys {
    public static final String ARTISTS = "artists";
    public static final String ARTIST_ITEM = "artist";
    public static final String ALBUMS = "albums";
    public static final String ALBUM_ITEM = "album";
    public static final String GENRE_ALBUMS = "genre-albums";
    public static final String ARTIST_ALBUMS = "artist-albums";
    public static final String TRACKS = "tracks";
    public static final String TRACK_ITEM = "track";
    public static final String GENRE_TRACKS = "genre-tracks";
    public static final String ARTIST_TRACKS = "artist-tracks";
    public static final String ALBUM_TRACKS = "album-tracks";

    public static String getListeningKey(Long userId){
        return "listening:" + userId;
    }
    public static String getSendMailKey(Long userId){
        return "mail:" + userId;
    }
}
