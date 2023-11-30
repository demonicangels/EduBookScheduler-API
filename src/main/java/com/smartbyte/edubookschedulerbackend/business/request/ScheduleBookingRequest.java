package com.smartbyte.edubookschedulerbackend.business.request;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleBookingRequest {

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

}
