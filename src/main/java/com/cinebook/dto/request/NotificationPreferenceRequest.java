package com.cinebook.dto.request;

public record NotificationPreferenceRequest(
        Boolean emailNotifications,
        Boolean smsNotifications,
        Boolean pushNotifications
) {
}
