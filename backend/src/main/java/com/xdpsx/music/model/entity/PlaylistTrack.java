package com.xdpsx.music.model.entity;

import com.xdpsx.music.model.id.PlaylistTrackId;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "playlist_tracks")
@EntityListeners(AuditingEntityListener.class)
public class PlaylistTrack {
    @EmbeddedId
    private PlaylistTrackId id;

    @ManyToOne
    @MapsId("playlistId")
    @JoinColumn(name = "playlist_id")
    private Playlist playlist;

    @ManyToOne
    @MapsId("trackId")
    @JoinColumn(name = "track_id")
    private Track track;

    @Column(nullable = false)
    private Integer trackNumber;
}
