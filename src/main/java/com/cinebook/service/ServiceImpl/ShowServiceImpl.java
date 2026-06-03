package com.cinebook.service.ServiceImpl;

import com.cinebook.dto.request.ShowRequest;
import com.cinebook.dto.response.SeatResponse;
import com.cinebook.dto.response.ShowResponse;
import com.cinebook.entity.Movie;
import com.cinebook.entity.Screen;
import com.cinebook.entity.Show;
import com.cinebook.enums.BookingStatus;
import com.cinebook.enums.SeatStatus;
import com.cinebook.exceptions.ResourceNotFoundException;
import com.cinebook.repository.BookingSeatRepository;
import com.cinebook.repository.MovieRepository;
import com.cinebook.repository.ScreenRepository;
import com.cinebook.repository.SeatRepository;
import com.cinebook.repository.ShowRepository;
import com.cinebook.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShowServiceImpl implements ShowService {

    private final ShowRepository showRepository;
    private final MovieRepository movieRepository;
    private final ScreenRepository screenRepository;
    private final SeatRepository seatRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final SeatLockService seatLockService;

    @Override
    public List<ShowResponse> getByMovie(Long movieId) {
        return showRepository.findByMovieId(movieId).stream()
                .map(ShowResponse::from)
                .toList();
    }

    @Override
    public List<ShowResponse> getByTheatre(Long theatreId) {
        return showRepository.findByScreenTheatreId(theatreId).stream()
                .map(ShowResponse::from)
                .toList();
    }

    @Override
    public ShowResponse getShow(Long showId) {
        return ShowResponse.from(findShow(showId));
    }

    @Override
    public List<SeatResponse> getSeats(Long showId) {
        Show show = findShow(showId);
        Set<Long> bookedSeatIds = bookingSeatRepository
                .findByBookingShowIdAndBookingStatusIn(
                        showId,
                        List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED, BookingStatus.UPCOMING))
                .stream()
                .map(bookingSeat -> bookingSeat.getSeat().getId())
                .collect(Collectors.toSet());


        return seatRepository.findByScreenId(show.getScreen().getId())
                .stream()
                .map(seat -> {

                    SeatStatus status = SeatStatus.AVAILABLE;

                    if (bookedSeatIds.contains(seat.getId())) {

                        status = SeatStatus.BOOKED;

                    } else if (
                            seatLockService.isSeatLocked(
                                    show.getId(),
                                    seat.getSeatNumber()
                            )
                    ) {

                        status = SeatStatus.LOCKED;
                    }

                    return SeatResponse.from(seat, status);

                })
                .toList();


    }

    @Override
    public ShowResponse create(ShowRequest request) {
        Show show = new Show();
        apply(show, request);
        return ShowResponse.from(showRepository.save(show));
    }

    @Override
    public ShowResponse update(Long id, ShowRequest request) {
        Show show = findShow(id);
        apply(show, request);
        return ShowResponse.from(showRepository.save(show));
    }

    @Override
    public void delete(Long id) {
        Show show = findShow(id);
        showRepository.delete(show);
    }

    private Show findShow(Long showId) {
        return showRepository.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found"));
    }

    private void apply(Show show, ShowRequest request) {
        Movie movie = movieRepository.findById(request.movieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        Screen screen = screenRepository.findById(request.screenId())
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found"));

        show.setMovie(movie);
        show.setScreen(screen);
        show.setShowDate(request.showDate());
        show.setStartTime(request.startTime());
        show.setEndTime(request.endTime());
        show.setPrice(request.price());
    }
}
