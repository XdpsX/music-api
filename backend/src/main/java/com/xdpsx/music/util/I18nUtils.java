package com.xdpsx.music.util;

import com.xdpsx.music.constant.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class I18nUtils {
    private final MessageSource messageSource;

    public String getGenreExistsMsg(String name){
        return getMessage(MessageKeys.ERROR_DUPLICATE, "Genre", "name", name);
    }
    public String getPlaylistExistsMsg(String name){
        return getMessage(MessageKeys.ERROR_DUPLICATE, "Playlist", "name", name);
    }
    public String getUserExistsMsg(String email){
        return getMessage(MessageKeys.ERROR_DUPLICATE, "User", "email", email);
    }

    public String getGenreNotFoundMsg(Integer genreId){
        return getMessage(MessageKeys.ERROR_NOT_FOUND, "Genre", "id", genreId);
    }
    public String getArtistNotFoundMsg(Long artistId){
        return getMessage(MessageKeys.ERROR_NOT_FOUND, "Artist", "id", artistId);
    }
    public String getAlbumNotFoundMsg(Long albumId){
        return getMessage(MessageKeys.ERROR_NOT_FOUND, "Album", "id", albumId);
    }
    public String getTrackNotFoundMsg(Long trackId){
        return getMessage(MessageKeys.ERROR_NOT_FOUND, "Track", "id", trackId);
    }
    public String getPlaylistNotFoundMsg(Long playlistId){
        return getMessage(MessageKeys.ERROR_NOT_FOUND, "Playlist", "id", playlistId);
    }
    public String getUserNotFoundMsg(Long userId){
        return getMessage(MessageKeys.ERROR_NOT_FOUND, "User", "id", userId);
    }
    public String getUserNotFoundMsg(String email){
        return getMessage(MessageKeys.ERROR_NOT_FOUND, "User", "email", email);
    }
    public String getConfirmTokenNotFoundMsg(String code){
        return getMessage(MessageKeys.ERROR_NOT_FOUND, "Confirm token", "code", code);
    }
    public String getRoleNotFoundMsg(String name){
        return getMessage(MessageKeys.ERROR_NOT_FOUND, "Role", "name", name);
    }

    public String getNotSamePwMsg(){
        return getMessage(MessageKeys.NOT_SAME_PW);
    }

    public String getWrongPwMsg(){
        return getMessage(MessageKeys.WRONG_PW);
    }

    public String getPlaylistTrackExistsMsg(Long trackId){
        return getMessage(MessageKeys.TRACK_EXISTS_IN_PLAYLIST, trackId);
    }

    public String getPlaylistTrackNotExistMsg(Long trackId){
        return getMessage(MessageKeys.TRACK_NOT_EXIST_IN_PLAYLIST, trackId);
    }

    public String getLikeExistsMsg(Long trackId){
        return getMessage(MessageKeys.TRACK_ALREADY_LIKED, trackId);
    }
    public String getLikeNotExistMsg(Long trackId){
        return getMessage(MessageKeys.TRACK_NOT_LIKED, trackId);
    }

    public String getResendMailMsg(int minutes){
        return getMessage(MessageKeys.RESEND_MAIL, minutes);
    }
    public String getLimitMailMsg(int number){
        return getMessage(MessageKeys.LIMIT_MAIL, number);
    }
    public String getActiveCodeExpiredMsg(){
        return getMessage(MessageKeys.ACTIVE_CODE_EXPIRED);
    }
    public String getResetCodeExpiredMsg(){
        return getMessage(MessageKeys.RESET_CODE_EXPIRED);
    }
    public String getActivatedAccountMsg(){
        return getMessage(MessageKeys.ACTIVATED_ACCOUNT);
    }

    public String getNotActiveAccountMsg(String email){
        return getMessage(MessageKeys.NOT_ACTIVE_ACCOUNT, email);
    }
    public String getLockedAccountMsg(String email){
        return getMessage(MessageKeys.LOCKED_ACCOUNT, email);
    }

    public String getMessage(String msgKey, Object... params) {
        return messageSource.getMessage(msgKey, params, LocaleContextHolder.getLocale());
    }
}
