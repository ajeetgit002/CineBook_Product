package com.cinebook.service.ServiceImpl;

import com.cinebook.dto.response.AdminDashboardResponse;
import com.cinebook.dto.response.UserSummaryResponse;
import com.cinebook.entity.User;
import com.cinebook.exceptions.ResourceNotFoundException;
import com.cinebook.repository.BookingRepository;
import com.cinebook.repository.MovieRepository;
import com.cinebook.repository.OfferRepository;
import com.cinebook.repository.ShowRepository;
import com.cinebook.repository.TheatreRepository;
import com.cinebook.repository.UserRepository;
import com.cinebook.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final TheatreRepository theatreRepository;
    private final ShowRepository showRepository;
    private final BookingRepository bookingRepository;
    private final OfferRepository offerRepository;

    @Override
    public List<UserSummaryResponse> users() {
        return userRepository.findAll().stream()
                .map(UserSummaryResponse::from)
                .toList();
    }

    @Override
    public UserSummaryResponse user(Long id) {
        return UserSummaryResponse.from(findUser(id));
    }

    @Override
    public UserSummaryResponse block(Long id) {
        User user = findUser(id);
        user.setEnabled(false);
        return UserSummaryResponse.from(userRepository.save(user));
    }

    @Override
    public UserSummaryResponse unblock(Long id) {
        User user = findUser(id);
        user.setEnabled(true);
        return UserSummaryResponse.from(userRepository.save(user));
    }

    @Override
    public AdminDashboardResponse dashboard() {
        return new AdminDashboardResponse(
                userRepository.count(),
                movieRepository.count(),
                theatreRepository.count(),
                showRepository.count(),
                bookingRepository.count(),
                offerRepository.count()
        );
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
