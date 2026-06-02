package com.cinebook.service;

import com.cinebook.dto.request.MovieRequest;
import com.cinebook.dto.response.MovieCastResponse;
import com.cinebook.dto.response.MovieResponse;
import com.cinebook.dto.response.MovieReviewResponse;

import java.util.List;

public interface MovieService {
    List<MovieResponse> getMovies();

    MovieResponse getMovie(Long movieId);

    List<MovieCastResponse> getCast(Long movieId);

    List<MovieReviewResponse> getReviews(Long movieId);

    List<MovieResponse> getRecommended(Long movieId);

    List<String> getFilters();

    List<MovieResponse> search(String keyword);

    List<MovieResponse> filter(String language, String genre, String format);

    List<MovieResponse> topRated();

    List<MovieResponse> upcoming();

    List<MovieResponse> nowShowing();

    MovieResponse create(MovieRequest request);

    MovieResponse update(Long id, MovieRequest request);

    void delete(Long id);
}
