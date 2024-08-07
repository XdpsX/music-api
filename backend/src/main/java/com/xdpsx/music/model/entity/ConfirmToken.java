package com.xdpsx.music.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "confirm_tokens")
public class ConfirmToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "confirm_token_id_seq_gen")
    @SequenceGenerator(name = "confirm_token_id_seq_gen", sequenceName = "confirm_tokens_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String code;

    private boolean revoked;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    private LocalDateTime validatedAt;

    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName = "id", nullable = false)
    private User user;
}
