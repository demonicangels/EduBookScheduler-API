package com.smartbyte.edubookschedulerbackend.business.request;

import com.smartbyte.edubookschedulerbackend.domain.Booking;
import com.smartbyte.edubookschedulerbackend.domain.Student;
import com.smartbyte.edubookschedulerbackend.domain.Tutor;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class RescheduleBookingRequest {
    @NotNull
    private Date date;
    @NotNull
    @Min(0)
    @Max(9999)
    private Integer startTime;
    @NotNull
    @Min(0)
    @Max(9999)
    private Integer endTime;
    @NotEmpty
    private String description;
    @NotNull
    private Long requesterId;
    @NotNull
    private Long receiverId;
    @NotNull
    private Long rescheduledBookingId;
}
