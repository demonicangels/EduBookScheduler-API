package com.smartbyte.edubookschedulerbackend.business.response;

import com.smartbyte.edubookschedulerbackend.domain.Booking;
import lombok.Builder;
import lombok.Data;

import java.util.*;
@Data
@Builder
public class RescheduleBookingResponse {
    Booking booking;
}
