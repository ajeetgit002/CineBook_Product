package com.cinebook.entity;

import com.cinebook.enums.ScreenType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "screens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer capacity;

    @Enumerated(EnumType.STRING)
    private ScreenType screenType;

    @ManyToOne
    @JoinColumn(name = "theatre_id")
    private Theatre theatre;
}