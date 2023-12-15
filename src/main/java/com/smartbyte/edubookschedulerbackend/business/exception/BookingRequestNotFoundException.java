package com.smartbyte.edubookschedulerbackend.business.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class BookingRequestNotFoundException extends RuntimeException {
}
