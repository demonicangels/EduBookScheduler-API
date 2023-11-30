package com.smartbyte.edubookschedulerbackend.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BookingRequest {
    private Long id;
    private BookingRequestType requestType;
    private User requester;
    private User receiver;
    private Booking bookingToSchedule;
    private Booking bookingToReschedule;
    @Builder.Default
    private BookingRequestAnswer answer = BookingRequestAnswer.NoAnswer;
}
