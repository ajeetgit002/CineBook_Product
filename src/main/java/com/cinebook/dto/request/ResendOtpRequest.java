package com.cinebook.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResendOtpRequest {

    @Email
    private String email;
}