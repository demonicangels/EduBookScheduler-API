package com.smartbyte.edubookschedulerbackend.domain;

import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityDomain {
    @Id
    private Long id;

    @NotNull
    private Date date;

    /*
        start and end time are defined as the
        number of minutes since midnight.
        therefore:
            time = 60*hour + minute;
        thus:
            hour = time / 60 (int division) and
            minute = time % 60
     */

    @NotNull
    @Min(0)
    @Max(9999)
    private Integer startTime;

    @NotNull
    @Min(0)
    @Max(9999)
    private Integer endTime;

    @NotNull
    private User tutor;

}
