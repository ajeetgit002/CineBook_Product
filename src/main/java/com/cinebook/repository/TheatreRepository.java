package com.cinebook.repository;

import com.cinebook.entity.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TheatreRepository extends JpaRepository<Theatre, Long>, JpaSpecificationExecutor<Theatre> {

    List<Theatre> findByCity(String city);

    List<Theatre> findByNameContainingIgnoreCase(String name);

    List<Theatre> findByActiveTrue();

    List<Theatre> findTop10ByActiveTrueOrderByRatingDesc();
}
