package com.cinebook.dto.response;

import com.cinebook.entity.User;

public record ProfileResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String profileImage,
        Integer rewardPoints,
        String role
) {
    public static ProfileResponse from(User user) {
        return new ProfileResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getProfileImage(),
                user.getRewardPoints(),
                user.getRole() == null ? null : user.getRole().getName()
        );
    }
}
