package com.cinebook.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LanguagePreferenceRequest(@NotBlank String language) {
}
