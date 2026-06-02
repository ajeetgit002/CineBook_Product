package com.cinebook.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ThemePreferenceRequest(@NotBlank String theme) {
}
