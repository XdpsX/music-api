package com.xdpsx.music.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_id_seq_gen")
    @SequenceGenerator(name = "role_id_seq_gen", sequenceName = "roles_id_seq", allocationSize = 1)
    private Integer id;

    @Column(length = 32, nullable = false, unique = true)
    private String name;

    public static final String USER = "USER";
    public static final String ADMIN = "ADMIN";
}
