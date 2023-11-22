package com.smartbyte.edubookschedulerbackend.business.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BookingNotFoundException extends ResponseStatusException {
    public BookingNotFoundException() {
        super(HttpStatus.BAD_REQUEST,"BOOKING_NOT_FOUND");
    }
}
