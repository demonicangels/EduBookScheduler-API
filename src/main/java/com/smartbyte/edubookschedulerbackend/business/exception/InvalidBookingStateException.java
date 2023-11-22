package com.smartbyte.edubookschedulerbackend.business.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidBookingStateException extends ResponseStatusException {
    public InvalidBookingStateException() {
        super(HttpStatus.BAD_REQUEST,"INVALID_BOOKING_STATE");
    }
}
