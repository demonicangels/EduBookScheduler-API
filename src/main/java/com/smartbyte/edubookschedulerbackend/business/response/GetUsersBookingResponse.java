package com.smartbyte.edubookschedulerbackend.business.response;

import com.smartbyte.edubookschedulerbackend.domain.Booking;
import lombok.Builder;

import java.util.*;

@Builder
public class GetUsersBookingResponse {
    List<Booking> bookings;
}
