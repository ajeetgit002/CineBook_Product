package com.cinebook.dto.response;

import com.cinebook.entity.UserPreference;

public record PreferenceResponse(
        Boolean emailNotifications,
        Boolean smsNotifications,
        Boolean pushNotifications,
        String language,
        String theme
) {
    public static PreferenceResponse from(UserPreference preference) {
        return new PreferenceResponse(
                preference.getEmailNotifications(),
                preference.getSmsNotifications(),
                preference.getPushNotifications(),
                preference.getLanguage(),
                preference.getTheme()
        );
    }
}
