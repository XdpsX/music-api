package com.xdpsx.music.util;

import com.xdpsx.music.controller.*;
import com.xdpsx.music.dto.response.*;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class Links {
    public static void addLinksToGenre(GenreResponse response){
        Link albumsLink = linkTo(methodOn(GenreController.class).getAlbumsByGenre(response.getId(), null))
                .withRel("genreAlbums");
        Link tracksLink = linkTo(methodOn(GenreController.class).getTracksByGenre(response.getId(), null))
                .withRel("genreTracks");
        response.addCustomLinks(albumsLink, tracksLink);
    }

    public static void addLinksToArtist(ArtistResponse response){
        Link albumsLink = linkTo(methodOn(ArtistController.class).getAlbumsByArtist(response.getId(), null))
                .withRel("artistAlbums");
        Link tracksLink = linkTo(methodOn(ArtistController.class).getTracksByArtist(response.getId(), null))
                .withRel("artistTracks");
        response.addCustomLinks(albumsLink, tracksLink);
    }

    public static void addLinksToAlbum(AlbumResponse response){
        Link tracksLink = linkTo(methodOn(AlbumController.class).getTracksByAlbum(response.getId(), null))
                .withRel("albumTracks");
        response.addCustomLinks(tracksLink);
    }

    public static void addLinksToTrack(TrackResponse response){
        Link likeLink = linkTo(methodOn(TrackController.class).likeTrack(response.getId()))
                .withRel("likeTrack");
        Link unlikeLink = linkTo(methodOn(TrackController.class).unlikeTrack(response.getId()))
                .withRel("unlikeTrack");
        response.addCustomLinks(likeLink, unlikeLink);
    }

    public static void addLinksToProfile(UserProfileResponse response){
        Link playlistsLink = linkTo(methodOn(PlaylistController.class).getAllUserPlaylists(null))
                .withRel("playlists");
        Link favoritesLink = linkTo(methodOn(UserController.class).getFavoriteTracks(null))
                .withRel("favorites");
        response.addCustomLinks(playlistsLink, favoritesLink);
    }

    public static void addLinksToPlaylist(PlaylistResponse response){
        Link tracksLink = linkTo(methodOn(PlaylistController.class).getTracksByPlaylist(response.getId(), null))
                .withRel("playlistTracks");
        Link addTrackLink = linkTo(methodOn(PlaylistController.class).addTrackToPlaylist(response.getId(), null))
                .withRel("addTrack");
        Link removeTrackLink = linkTo(methodOn(PlaylistController.class).removeTrackFromPlaylist(response.getId(), null))
                .withRel("removeTrack");
        response.addCustomLinks(tracksLink,addTrackLink, removeTrackLink);
    }
}
