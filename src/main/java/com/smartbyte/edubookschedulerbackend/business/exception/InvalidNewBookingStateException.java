package com.smartbyte.edubookschedulerbackend.business.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidNewBookingStateException extends ResponseStatusException {
    public InvalidNewBookingStateException() {
        super(HttpStatus.BAD_REQUEST,"INVALID_NEW_BOOKING_STATE");
    }
}
