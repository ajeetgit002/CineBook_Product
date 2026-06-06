package com.cinebook.dto.request;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingRegistration {

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String password;
}

