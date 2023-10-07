package com.smartbyte.edubookschedulerbackend.business.request;

import com.smartbyte.edubookschedulerbackend.domain.Booking;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RescheduleBookingRequest {
    Booking booking;
}
