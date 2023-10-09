package com.smartbyte.edubookschedulerbackend.business.response;

import com.smartbyte.edubookschedulerbackend.domain.Booking;
import lombok.Builder;
import lombok.Data;

import java.util.Optional;

/*
    FIXME: The response is exposing the user class inside booking which may expose sensitive info.
           either create a separate DTO for bookings and user or specify using annotations which properties
           to ignore/use.
           I prefer the first one since that makes it more difficult to accidentally break the frontend
           using our API since we will be forced to fix any breaking changes in the domain in the controller conversion
           code.
 */

@Data
@Builder
public class GetBookingByIdResponse {
    Booking booking;
}
