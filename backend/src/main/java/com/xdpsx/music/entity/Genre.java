package com.xdpsx.music.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "genres")
public class Genre {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(length = 64, nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String image;
}
