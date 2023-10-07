package com.smartbyte.edubookschedulerbackend.business.response;

import com.smartbyte.edubookschedulerbackend.domain.Booking;
import lombok.Builder;
import lombok.Data;

import java.util.Optional;

@Data
@Builder
public class GetBookingByIdResponse {
    Optional<Booking> booking;
}
