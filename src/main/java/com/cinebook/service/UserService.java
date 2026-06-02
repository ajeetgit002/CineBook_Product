package com.cinebook.service;

import com.cinebook.dto.request.*;
import com.cinebook.dto.response.*;

import java.util.List;
import java.util.Map;

public interface UserService {
    ProfileResponse profile();

    ProfileResponse updateProfile(ProfileUpdateRequest request);

    ProfileResponse updateImage(String imageUrl);

    Map<String, Object> rewards();

    Map<String, Object> overview();

    List<PaymentMethodResponse> paymentMethods();

    PaymentMethodResponse addPaymentMethod(PaymentMethodRequest request);

    void deletePaymentMethod(Long id);

    String changePassword(ChangePasswordRequest request);

    ProfileResponse changeEmail(ChangeEmailRequest request);

    ProfileResponse changePhone(ChangePhoneRequest request);

    PreferenceResponse preferences();

    PreferenceResponse updatePreferences(PreferenceRequest request);

    PreferenceResponse notifications();

    PreferenceResponse updateNotifications(NotificationPreferenceRequest request);

    List<String> languages();

    PreferenceResponse updateLanguage(LanguagePreferenceRequest request);

    PreferenceResponse updateTheme(ThemePreferenceRequest request);
}
