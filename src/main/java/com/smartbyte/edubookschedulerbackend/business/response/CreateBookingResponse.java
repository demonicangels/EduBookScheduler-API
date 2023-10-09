package com.smartbyte.edubookschedulerbackend.business.response;

import com.smartbyte.edubookschedulerbackend.domain.Booking;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateBookingResponse {
    Booking booking;
}
