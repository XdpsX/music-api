package com.xdpsx.music.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tracks")
@EntityListeners(AuditingEntityListener.class)
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "track_id_seq_gen")
    @SequenceGenerator(name = "track_id_seq_gen", sequenceName = "tracks_id_seq", allocationSize = 1)
    private Long id;

    @Column(length = 128, nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer durationMs;

    private String image;

    @Column(nullable = false)
    private String url;

    @CreatedDate
    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    private Integer trackNumber;

    private int listeningCount;

    @ManyToOne
    @JoinColumn(name="album_id", referencedColumnName = "id")
    private Album album;

    @ManyToOne
    @JoinColumn(name="genre_id", referencedColumnName = "id")
    private Genre genre;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "artist_tracks",
            joinColumns = @JoinColumn(name = "track_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "artist_id", referencedColumnName = "id")
    )
    private List<Artist> artists;

    @OneToMany(
            cascade = CascadeType.REMOVE,
            mappedBy = "track"
    )
    private List<Like> usersLiked;

    @OneToMany(
            cascade = CascadeType.REMOVE,
            mappedBy = "track"
    )
    private List<PlaylistTrack> playlists;
}
