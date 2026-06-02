package com.cinebook.service.ServiceImpl;

import com.cinebook.dto.request.MovieRequest;
import com.cinebook.dto.response.MovieCastResponse;
import com.cinebook.dto.response.MovieResponse;
import com.cinebook.dto.response.MovieReviewResponse;
import com.cinebook.entity.Movie;
import com.cinebook.enums.MovieStatus;
import com.cinebook.exceptions.ResourceNotFoundException;
import com.cinebook.repository.MovieCastRepository;
import com.cinebook.repository.MovieRepository;
import com.cinebook.repository.MovieReviewRepository;
import com.cinebook.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final MovieCastRepository movieCastRepository;
    private final MovieReviewRepository movieReviewRepository;

    @Override
    public List<MovieResponse> getMovies() {
        return movieRepository.findAll().stream()
                .map(MovieResponse::from)
                .toList();
    }

    @Override
    public MovieResponse getMovie(Long movieId) {
        return MovieResponse.from(findMovie(movieId));
    }

    @Override
    public List<MovieCastResponse> getCast(Long movieId) {
        findMovie(movieId);
        return movieCastRepository.findByMovieId(movieId).stream()
                .map(MovieCastResponse::from)
                .toList();
    }

    @Override
    public List<MovieReviewResponse> getReviews(Long movieId) {
        findMovie(movieId);
        return movieReviewRepository.findByMovieId(movieId).stream()
                .map(MovieReviewResponse::from)
                .toList();
    }

    @Override
    public List<MovieResponse> getRecommended(Long movieId) {
        Movie movie = findMovie(movieId);
        return movieRepository.findAll().stream()
                .filter(candidate -> !Objects.equals(candidate.getId(), movie.getId()))
                .filter(candidate -> movie.getGenre() == null
                        || movie.getGenre().equalsIgnoreCase(candidate.getGenre()))
                .limit(10)
                .map(MovieResponse::from)
                .toList();
    }

    @Override
    public List<String> getFilters() {
        return movieRepository.findAll().stream()
                .flatMap(movie -> List.of(movie.getLanguage(), movie.getGenre(), movie.getFormat()).stream())
                .filter(value -> value != null && !value.isBlank())
                .distinct()
                .toList();
    }

    @Override
    public List<MovieResponse> search(String keyword) {
        return movieRepository.findByTitleContainingIgnoreCase(keyword == null ? "" : keyword).stream()
                .map(MovieResponse::from)
                .toList();
    }

    @Override
    public List<MovieResponse> filter(String language, String genre, String format) {
        Specification<Movie> spec = Specification.where(null);

        if (hasText(language)) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("language").as(String.class)), language.toLowerCase()));
        }
        if (hasText(genre)) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("genre").as(String.class)), genre.toLowerCase()));
        }
        if (hasText(format)) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("format").as(String.class)), format.toLowerCase()));
        }

        return movieRepository.findAll(spec).stream()
                .map(MovieResponse::from)
                .toList();
    }

    @Override
    public List<MovieResponse> topRated() {
        return movieRepository.findTop10ByOrderByRatingDesc().stream()
                .map(MovieResponse::from)
                .toList();
    }

    @Override
    public List<MovieResponse> upcoming() {
        return movieRepository.findByStatus(MovieStatus.UPCOMING).stream()
                .map(MovieResponse::from)
                .toList();
    }

    @Override
    public List<MovieResponse> nowShowing() {
        return movieRepository.findByStatus(MovieStatus.NOW_SHOWING).stream()
                .map(MovieResponse::from)
                .toList();
    }

    @Override
    public MovieResponse create(MovieRequest request) {
        Movie movie = new Movie();
        apply(movie, request);
        return MovieResponse.from(movieRepository.save(movie));
    }

    @Override
    public MovieResponse update(Long id, MovieRequest request) {
        Movie movie = findMovie(id);
        apply(movie, request);
        return MovieResponse.from(movieRepository.save(movie));
    }

    @Override
    public void delete(Long id) {
        Movie movie = findMovie(id);
        movieRepository.delete(movie);
    }

    private Movie findMovie(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
    }

    private void apply(Movie movie, MovieRequest request) {
        movie.setTitle(request.title());
        movie.setDescription(request.description());
        movie.setDurationMinutes(request.durationMinutes());
        movie.setLanguage(request.language());
        movie.setGenre(request.genre());
        movie.setFormat(request.format());
        movie.setReleaseDate(request.releaseDate());
        movie.setRating(request.rating());
        movie.setPosterUrl(request.posterUrl());
        movie.setBannerUrl(request.bannerUrl());
        movie.setTrailerUrl(request.trailerUrl());
        movie.setStatus(request.status() == null ? MovieStatus.UPCOMING : request.status());
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
