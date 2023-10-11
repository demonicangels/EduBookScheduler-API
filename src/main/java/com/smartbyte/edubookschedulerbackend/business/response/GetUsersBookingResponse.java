package com.smartbyte.edubookschedulerbackend.business.response;

import com.smartbyte.edubookschedulerbackend.domain.Booking;
import lombok.Builder;
import lombok.Getter;

import java.util.*;

@Getter
@Builder
public class GetUsersBookingResponse {
    List<Booking> bookings;
}
