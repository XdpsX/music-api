package com.xdpsx.music.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "playlists")
@EntityListeners(AuditingEntityListener.class)
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "playlist_id_seq_gen")
    @SequenceGenerator(name = "playlist_id_seq_gen", sequenceName = "playlists_id_seq", allocationSize = 1)
    private Long id;

    @Column(length = 128, nullable = false)
    private String name;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private User owner;

    @OneToMany(
            cascade = CascadeType.REMOVE,
            mappedBy = "playlist"
    )
    private List<PlaylistTrack> tracks;
}
