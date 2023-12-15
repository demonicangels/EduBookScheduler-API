package com.smartbyte.edubookschedulerbackend.domain;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum BookingRequestType {
    Schedule(0),
    Reschedule(1);

    final int id;
}
