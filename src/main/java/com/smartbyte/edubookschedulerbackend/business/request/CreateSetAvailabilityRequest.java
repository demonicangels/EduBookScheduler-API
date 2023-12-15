package com.smartbyte.edubookschedulerbackend.business.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class CreateSetAvailabilityRequest {
    @NotNull
    Date date;
    @NotNull
    Integer startTime;
    @NotNull
    Integer endTime;
    @NotNull
    Long tutorId;
}
