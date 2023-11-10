package com.smartbyte.edubookschedulerbackend.business.request;

import com.smartbyte.edubookschedulerbackend.domain.Student;
import com.smartbyte.edubookschedulerbackend.domain.Tutor;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Builder
public class CreateBookingRequest {
    @NotNull
    String date;
    @NotNull
    String description;
    @NotNull
    Long studentId;
    @NotNull
    Integer startTime;

    @NotNull
    Integer endTime;

    @NotNull
    String tutorName;
}
