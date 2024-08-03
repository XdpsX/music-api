package com.xdpsx.music.entity;

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
    @GeneratedValue
    private Integer id;

    @Column(length = 32, nullable = false, unique = true)
    private String name;

    public static final String USER = "USER";
}
