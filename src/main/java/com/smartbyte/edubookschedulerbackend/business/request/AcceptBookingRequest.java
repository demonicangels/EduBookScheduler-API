package com.smartbyte.edubookschedulerbackend.business.request;

import com.smartbyte.edubookschedulerbackend.domain.BookingRequestAnswer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AcceptBookingRequest {
    private Long bookingRequestId;
    private Integer answer;
}
