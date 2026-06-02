package com.cinebook.dto.request;

public record PreferenceRequest(
        Boolean emailNotifications,
        Boolean smsNotifications,
        Boolean pushNotifications,
        String language,
        String theme
) {
}
