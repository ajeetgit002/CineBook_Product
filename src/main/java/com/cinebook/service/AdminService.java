package com.cinebook.service;

import com.cinebook.dto.response.AdminDashboardResponse;
import com.cinebook.dto.response.UserSummaryResponse;

import java.util.List;

public interface AdminService {
    List<UserSummaryResponse> users();

    UserSummaryResponse user(Long id);

    UserSummaryResponse block(Long id);

    UserSummaryResponse unblock(Long id);

    AdminDashboardResponse dashboard();
}
