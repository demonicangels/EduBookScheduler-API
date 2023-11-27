package com.smartbyte.edubookschedulerbackend.business.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateBookingStateRequest {
    private long bookingId;
}
