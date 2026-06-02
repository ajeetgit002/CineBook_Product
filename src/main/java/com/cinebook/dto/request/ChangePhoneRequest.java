package com.cinebook.dto.request;

import jakarta.validation.constraints.Pattern;

public record ChangePhoneRequest(
        @Pattern(regexp = "^[0-9]{10}$") String phone
) {
}
