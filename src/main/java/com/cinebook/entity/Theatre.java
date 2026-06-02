package com.cinebook.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "theatres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Theatre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;

    private String city;

    private String area;

    private String format;

    private Double rating;

    private Boolean active;
}
