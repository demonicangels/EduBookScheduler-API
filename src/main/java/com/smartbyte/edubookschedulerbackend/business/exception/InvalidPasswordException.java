package com.smartbyte.edubookschedulerbackend.business.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidPasswordException extends ResponseStatusException {

    public InvalidPasswordException() {
        super(HttpStatus.BAD_REQUEST,"INVALID_PASSWORD");
    }
}
