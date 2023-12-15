package com.smartbyte.edubookschedulerbackend.business.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class TutorNotFoundException extends ResponseStatusException {
    public TutorNotFoundException() {
        super(HttpStatus.BAD_REQUEST,"TUTOR_NOT_FOUND");
    }
}
