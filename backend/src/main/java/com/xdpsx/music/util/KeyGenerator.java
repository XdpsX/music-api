package com.xdpsx.music.util;

import com.xdpsx.music.dto.request.params.AlbumParams;
import com.xdpsx.music.dto.request.params.ArtistParams;
import com.xdpsx.music.dto.request.params.TrackParams;

public class KeyGenerator {
    private final static String ARTISTS = "artists";
    private final static String ALBUMS = "albums";
    private final static String TRACKS = "tracks";

    // ARTISTS LIST
    public static String getArtistsKey(){
        return ARTISTS;
    }
    public static String getArtistsKey(ArtistParams params){
        return String.format("%s:%s", ARTISTS, getArtistParamsPart(params));
    }

    // ALBUMS LIST
    public static String getAlbumsKey(){
        return ALBUMS;
    }
    public static String getAlbumsKey(AlbumParams params){
        return String.format("%s:%s", ALBUMS, getAlbumParamsPart(params));
    }
    // ALBUMS BY GENRE
    public static String getGenreAlbumsKey(Integer genreId){
        return String.format("genre:%s:albums", genreId);
    }
    public static String getGenreAlbumsKey(Integer genreId, AlbumParams params){
        return String.format("genre:%s:albums:%s", genreId, getAlbumParamsPart(params));
    }
    // ALBUMS BY ARTIST
    public static String getArtistAlbumsKey(Long artistId){
        return String.format("artist:%s:albums", artistId);
    }
    public static String getArtistAlbumsKey(Long artistId, AlbumParams params){
        return String.format("artist:%s:albums:%s", artistId, getAlbumParamsPart(params));
    }

    // TRACKS LIST
    public static String getTracksKey(){
        return TRACKS;
    }
    public static String getTracksKey(TrackParams params){
        return String.format("%s:%s", TRACKS, getTrackParamsPart(params));
    }
    // TRACKS BY GENRE
    public static String getGenreTracksKey(Integer genreId){
        return String.format("genre:%s:tracks", genreId);
    }
    public static String getGenreTracksKey(Integer genreId, TrackParams params){
        return String.format("genre:%s:tracks:%s", genreId, getTrackParamsPart(params));
    }
    // TRACKS BY ARTIST
    public static String getArtistTracksKey(Long artistId){
        return String.format("artist:%s:tracks", artistId);
    }
    public static String getArtistTracksKey(Long artistId, TrackParams params){
        return String.format("artist:%s:tracks:%s", artistId, getTrackParamsPart(params));
    }
    // TRACKS BY ALBUM
    public static String getAlbumTracksKey(Long albumId){
        return String.format("album:%s:tracks", albumId);
    }
    public static String getAlbumTracksKey(Long albumId, TrackParams params){
        return String.format("album:%s:tracks:%s", albumId, getTrackParamsPart(params));
    }

    // PARAMS
    private static String getArtistParamsPart(ArtistParams params){
        return String.format("%s,%s,%s,%s,%s",
                params.getPageNum(), params.getPageSize(), params.getSearch(), params.getSort(), params.getGender());
    }
    private static String getAlbumParamsPart(AlbumParams params){
        return String.format("%s,%s,%s,%s",
                params.getPageNum(), params.getPageSize(), params.getSearch(), params.getSort());
    }
    private static String getTrackParamsPart(TrackParams params){
        return String.format("%s,%s,%s,%s",
                params.getPageNum(), params.getPageSize(), params.getSearch(), params.getSort());
    }
}
