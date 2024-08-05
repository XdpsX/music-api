package com.xdpsx.music.model.entity;

import com.xdpsx.music.model.id.LikeId;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "likes")
@EntityListeners(AuditingEntityListener.class)
public class Like {
    @EmbeddedId
    private LikeId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("trackId")
    @JoinColumn(name = "track_id")
    private Track track;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
