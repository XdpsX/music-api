package com.xdpsx.music.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "artists")
@EntityListeners(AuditingEntityListener.class)
public class Artist {
    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 128, nullable = false)
    private String name;

    private String avatar;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(columnDefinition = "text")
    private String description;

    private LocalDate dob;

    @CreatedDate
    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @ManyToMany(mappedBy = "artists")
    private List<Album> albums;

    @ManyToMany(mappedBy = "artists")
    private List<Track> tracks;

}
