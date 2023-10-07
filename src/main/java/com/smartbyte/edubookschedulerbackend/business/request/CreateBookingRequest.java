package com.smartbyte.edubookschedulerbackend.business.request;

import com.smartbyte.edubookschedulerbackend.domain.Student;
import com.smartbyte.edubookschedulerbackend.domain.Tutor;
import lombok.*;

import java.util.Date;

@Data
@Builder
public class CreateBookingRequest {
    @NonNull
    Date dateAndTime;
    String description;
    @NonNull
    Student student;
    @NonNull
    Tutor tutor;
}
