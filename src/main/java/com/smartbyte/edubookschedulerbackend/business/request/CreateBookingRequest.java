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
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    Date dateAndTime;
    String description;
    @NotNull
    Long studentId;
    @NotNull
    Long tutorId;
}
