package com.cinebook.dto.response;

import com.cinebook.entity.User;

public record UserSummaryResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        Boolean enabled,
        String role
) {
    public static UserSummaryResponse from(User user) {
        return new UserSummaryResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getEnabled(),
                user.getRole() == null ? null : user.getRole().getName()
        );
    }
}
