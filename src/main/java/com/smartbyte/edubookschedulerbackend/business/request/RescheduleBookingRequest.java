package com.smartbyte.edubookschedulerbackend.business.request;

import com.smartbyte.edubookschedulerbackend.domain.Booking;
import com.smartbyte.edubookschedulerbackend.domain.Student;
import com.smartbyte.edubookschedulerbackend.domain.Tutor;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class RescheduleBookingRequest {
    @NotNull
    Long id;
    @NotNull
    Date date;
    @NotNull
    Integer startTime;
    @NotNull
    Integer endTime;

    String description;
    @NotNull
    Long studentId;
    @NotNull
    Long tutorId;
}
