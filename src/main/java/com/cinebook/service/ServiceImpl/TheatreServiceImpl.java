package com.cinebook.service.ServiceImpl;

import com.cinebook.dto.request.TheatreRequest;
import com.cinebook.dto.response.ShowResponse;
import com.cinebook.dto.response.TheatreResponse;
import com.cinebook.entity.Theatre;
import com.cinebook.exceptions.ResourceNotFoundException;
import com.cinebook.repository.ShowRepository;
import com.cinebook.repository.TheatreRepository;
import com.cinebook.service.TheatreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TheatreServiceImpl implements TheatreService {

    private final TheatreRepository theatreRepository;
    private final ShowRepository showRepository;

    @Override
    public List<TheatreResponse> getTheatres() {
        return theatreRepository.findAll().stream()
                .map(TheatreResponse::from)
                .toList();
    }

    @Override
    public TheatreResponse getTheatre(Long theatreId) {
        return TheatreResponse.from(findTheatre(theatreId));
    }

    @Override
    public List<ShowResponse> getShows(Long theatreId) {
        findTheatre(theatreId);
        return showRepository.findByScreenTheatreId(theatreId).stream()
                .map(ShowResponse::from)
                .toList();
    }

    @Override
    public List<String> getFilters() {
        return theatreRepository.findAll().stream()
                .flatMap(theatre -> List.of(theatre.getCity(), theatre.getArea(), theatre.getFormat()).stream())
                .filter(value -> value != null && !value.isBlank())
                .distinct()
                .toList();
    }

    @Override
    public List<TheatreResponse> search(String keyword) {
        return theatreRepository.findByNameContainingIgnoreCase(keyword == null ? "" : keyword).stream()
                .map(TheatreResponse::from)
                .toList();
    }

    @Override
    public List<TheatreResponse> filter(String city, String area, String format) {
        Specification<Theatre> spec = Specification.where(null);

        if (hasText(city)) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("city").as(String.class)), city.toLowerCase()));
        }
        if (hasText(area)) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("area").as(String.class)), area.toLowerCase()));
        }
        if (hasText(format)) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("format").as(String.class)), format.toLowerCase()));
        }

        return theatreRepository.findAll(spec).stream()
                .map(TheatreResponse::from)
                .toList();
    }

    @Override
    public List<TheatreResponse> popular() {
        return theatreRepository.findTop10ByActiveTrueOrderByRatingDesc().stream()
                .map(TheatreResponse::from)
                .toList();
    }

    @Override
    public List<TheatreResponse> recommended() {
        return popular();
    }

    @Override
    public TheatreResponse create(TheatreRequest request) {
        Theatre theatre = new Theatre();
        apply(theatre, request);
        return TheatreResponse.from(theatreRepository.save(theatre));
    }

    @Override
    public TheatreResponse update(Long id, TheatreRequest request) {
        Theatre theatre = findTheatre(id);
        apply(theatre, request);
        return TheatreResponse.from(theatreRepository.save(theatre));
    }

    @Override
    public void delete(Long id) {
        Theatre theatre = findTheatre(id);
        theatreRepository.delete(theatre);
    }

    private Theatre findTheatre(Long theatreId) {
        return theatreRepository.findById(theatreId)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found"));
    }

    private void apply(Theatre theatre, TheatreRequest request) {
        theatre.setName(request.name());
        theatre.setAddress(request.address());
        theatre.setCity(request.city());
        theatre.setArea(request.area());
        theatre.setFormat(request.format());
        theatre.setRating(request.rating());
        theatre.setActive(request.active() == null || request.active());
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
