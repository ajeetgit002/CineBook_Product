package com.cinebook.exceptions;

public class MaxResendLimitException
        extends RuntimeException {

    public MaxResendLimitException() {

        super(
                "Maximum OTP resend limit reached"
        );
    }
}