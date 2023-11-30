package com.smartbyte.edubookschedulerbackend.domain;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum BookingRequestAnswer {
    NoAnswer(0),
    Accepted(1),
    Rejected(2);

    final int id;

}
