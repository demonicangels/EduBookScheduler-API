package com.smartbyte.edubookschedulerbackend.business.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
public class GetUpcomingBookingsResponse {
    private long id;
    private String tutorName;
    private String date;
    private int startHour;
    private int startMinute;
    private String description;
}
