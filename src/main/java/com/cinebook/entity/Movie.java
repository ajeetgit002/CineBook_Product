package com.cinebook.entity;

import com.cinebook.enums.MovieStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 5000)
    private String description;

    private Integer durationMinutes;

    private String language;

    private String genre;

    private String format;

    private LocalDate releaseDate;

    private Double rating;

    private String posterUrl;

    private String bannerUrl;

    private String trailerUrl;

    @Enumerated(EnumType.STRING)
    private MovieStatus status;
}
