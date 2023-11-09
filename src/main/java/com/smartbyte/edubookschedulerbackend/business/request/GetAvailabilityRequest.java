package com.smartbyte.edubookschedulerbackend.business.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Builder
public class GetAvailabilityRequest {
    @NotNull
    String date;
    @NotNull
    int startTime;
    @NotNull
    int endTime;


}
