package com.cinebook.service.ServiceImpl;

import com.cinebook.dto.request.ScreenRequest;
import com.cinebook.dto.response.ScreenResponse;
import com.cinebook.entity.Screen;
import com.cinebook.entity.Theatre;
import com.cinebook.exceptions.ResourceNotFoundException;
import com.cinebook.repository.ScreenRepository;
import com.cinebook.repository.TheatreRepository;
import com.cinebook.service.ScreenService;
import com.cinebook.service.SeatGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScreenServiceImpl implements ScreenService {

    private final ScreenRepository screenRepository;
    private final TheatreRepository theatreRepository;
    private final SeatGeneratorService seatGeneratorService;

    @Override
    @Transactional
    public ScreenResponse create(ScreenRequest request) {
        log.info("[ScreenService] Creating screen: {} for theatre: {}", request.name(), request.theatreId());

        Theatre theatre = theatreRepository.findById(request.theatreId())
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id: " + request.theatreId()));

        Screen screen = Screen.builder()
                .name(request.name())
                .capacity(request.capacity())
                .screenType(request.screenType())
                .theatre(theatre)
                .build();

        Screen savedScreen = screenRepository.save(screen);

        log.info("[ScreenService] Screen saved successfully with id: {}. Generating seats...", savedScreen.getId());
        
        seatGeneratorService.generateSeats(savedScreen);

        return ScreenResponse.from(savedScreen);
    }

    @Override
    @Transactional(readOnly = true)
    public ScreenResponse getScreen(Long id) {
        log.debug("[ScreenService] Fetching screen with id: {}", id);
        return ScreenResponse.from(findScreen(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScreenResponse> getScreens(Long theatreId) {
        log.debug("[ScreenService] Fetching screens for theatre: {}", theatreId);
        
        if (theatreId != null) {
            return screenRepository.findByTheatreId(theatreId).stream()
                    .map(ScreenResponse::from)
                    .toList();
        }
        
        return screenRepository.findAll().stream()
                .map(ScreenResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("[ScreenService] Deleting screen with id: {}", id);
        Screen screen = findScreen(id);
        screenRepository.delete(screen);
    }

    private Screen findScreen(Long id) {
        return screenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found with id: " + id));
    }
}