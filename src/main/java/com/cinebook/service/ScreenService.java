package com.cinebook.service;

import com.cinebook.dto.request.ScreenRequest;
import com.cinebook.dto.response.ScreenResponse;

import java.util.List;

public interface ScreenService {

    ScreenResponse create(ScreenRequest request);

    ScreenResponse getScreen(Long id);

    List<ScreenResponse> getScreens(Long theatreId);

    void delete(Long id);
}