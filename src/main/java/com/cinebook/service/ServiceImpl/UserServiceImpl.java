package com.cinebook.service.ServiceImpl;

import com.cinebook.dto.request.*;
import com.cinebook.dto.response.*;
import com.cinebook.entity.User;
import com.cinebook.entity.UserPaymentMethod;
import com.cinebook.entity.UserPreference;
import com.cinebook.exceptions.BadRequestException;
import com.cinebook.exceptions.ForbiddenException;
import com.cinebook.exceptions.ResourceNotFoundException;
import com.cinebook.repository.BookingRepository;
import com.cinebook.repository.UserPaymentMethodRepository;
import com.cinebook.repository.UserPreferenceRepository;
import com.cinebook.repository.UserRepository;
import com.cinebook.security.CurrentUserService;
import com.cinebook.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final UserPaymentMethodRepository paymentMethodRepository;
    private final UserPreferenceRepository preferenceRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ProfileResponse profile() {
        return ProfileResponse.from(currentUserService.getCurrentUser());
    }

    @Override
    public ProfileResponse updateProfile(ProfileUpdateRequest request) {
        User user = currentUserService.getCurrentUser();
        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }
        if (request.phone() != null) {
            user.setPhone(request.phone());
        }
        return ProfileResponse.from(userRepository.save(user));
    }

    @Override
    public ProfileResponse updateImage(String imageUrl) {
        User user = currentUserService.getCurrentUser();
        user.setProfileImage(imageUrl);
        return ProfileResponse.from(userRepository.save(user));
    }

    @Override
    public Map<String, Object> rewards() {
        User user = currentUserService.getCurrentUser();
        return Map.of(
                "points", user.getRewardPoints() == null ? 0 : user.getRewardPoints(),
                "transactions", List.of()
        );
    }

    @Override
    public Map<String, Object> overview() {
        User user = currentUserService.getCurrentUser();
        return Map.of(
                "profile", ProfileResponse.from(user),
                "bookingCount", bookingRepository.findByUserId(user.getId()).size(),
                "rewardPoints", user.getRewardPoints() == null ? 0 : user.getRewardPoints()
        );
    }

    @Override
    public List<PaymentMethodResponse> paymentMethods() {
        User user = currentUserService.getCurrentUser();
        return paymentMethodRepository.findByUserId(user.getId()).stream()
                .map(PaymentMethodResponse::from)
                .toList();
    }

    @Override
    public PaymentMethodResponse addPaymentMethod(PaymentMethodRequest request) {
        User user = currentUserService.getCurrentUser();
        UserPaymentMethod paymentMethod = UserPaymentMethod.builder()
                .provider(request.provider())
                .type(request.type())
                .maskedNumber(request.maskedNumber())
                .defaultMethod(Boolean.TRUE.equals(request.defaultMethod()))
                .user(user)
                .build();

        return PaymentMethodResponse.from(paymentMethodRepository.save(paymentMethod));
    }

    @Override
    public void deletePaymentMethod(Long id) {
        User user = currentUserService.getCurrentUser();
        UserPaymentMethod paymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found"));

        if (!paymentMethod.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Payment method does not belong to current user");
        }

        paymentMethodRepository.delete(paymentMethod);
    }

    @Override
    public String changePassword(ChangePasswordRequest request) {
        User user = currentUserService.getCurrentUser();
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        return "Password changed successfully";
    }

    @Override
    public ProfileResponse changeEmail(ChangeEmailRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already registered");
        }
        User user = currentUserService.getCurrentUser();
        user.setEmail(request.email());
        return ProfileResponse.from(userRepository.save(user));
    }

    @Override
    public ProfileResponse changePhone(ChangePhoneRequest request) {
        if (userRepository.existsByPhone(request.phone())) {
            throw new BadRequestException("Phone already registered");
        }
        User user = currentUserService.getCurrentUser();
        user.setPhone(request.phone());
        return ProfileResponse.from(userRepository.save(user));
    }

    @Override
    public PreferenceResponse preferences() {
        return PreferenceResponse.from(currentPreference());
    }

    @Override
    public PreferenceResponse updatePreferences(PreferenceRequest request) {
        UserPreference preference = currentPreference();
        if (request.emailNotifications() != null) {
            preference.setEmailNotifications(request.emailNotifications());
        }
        if (request.smsNotifications() != null) {
            preference.setSmsNotifications(request.smsNotifications());
        }
        if (request.pushNotifications() != null) {
            preference.setPushNotifications(request.pushNotifications());
        }
        if (request.language() != null) {
            preference.setLanguage(request.language());
        }
        if (request.theme() != null) {
            preference.setTheme(request.theme());
        }
        return PreferenceResponse.from(preferenceRepository.save(preference));
    }

    @Override
    public PreferenceResponse notifications() {
        return preferences();
    }

    @Override
    public PreferenceResponse updateNotifications(NotificationPreferenceRequest request) {
        UserPreference preference = currentPreference();
        if (request.emailNotifications() != null) {
            preference.setEmailNotifications(request.emailNotifications());
        }
        if (request.smsNotifications() != null) {
            preference.setSmsNotifications(request.smsNotifications());
        }
        if (request.pushNotifications() != null) {
            preference.setPushNotifications(request.pushNotifications());
        }
        return PreferenceResponse.from(preferenceRepository.save(preference));
    }

    @Override
    public List<String> languages() {
        return List.of("English", "Hindi", "Kannada", "Tamil", "Telugu", "Malayalam");
    }

    @Override
    public PreferenceResponse updateLanguage(LanguagePreferenceRequest request) {
        UserPreference preference = currentPreference();
        preference.setLanguage(request.language());
        return PreferenceResponse.from(preferenceRepository.save(preference));
    }

    @Override
    public PreferenceResponse updateTheme(ThemePreferenceRequest request) {
        UserPreference preference = currentPreference();
        preference.setTheme(request.theme());
        return PreferenceResponse.from(preferenceRepository.save(preference));
    }

    private UserPreference currentPreference() {
        User user = currentUserService.getCurrentUser();
        return preferenceRepository.findByUserId(user.getId())
                .orElseGet(() -> preferenceRepository.save(UserPreference.builder()
                        .user(user)
                        .emailNotifications(true)
                        .smsNotifications(true)
                        .pushNotifications(true)
                        .language("English")
                        .theme("SYSTEM")
                        .build()));
    }
}
