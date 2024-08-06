package com.xdpsx.music.model.id;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class PlaylistTrackId implements Serializable {
    private Long playlistId;
    private Long trackId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlaylistTrackId that = (PlaylistTrackId) o;

        if (!playlistId.equals(that.playlistId)) return false;
        return trackId.equals(that.trackId);
    }

    @Override
    public int hashCode() {
        int result = playlistId.hashCode();
        result = 31 * result + trackId.hashCode();
        return result;
    }
}
