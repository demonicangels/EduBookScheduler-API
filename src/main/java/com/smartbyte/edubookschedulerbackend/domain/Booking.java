package com.smartbyte.edubookschedulerbackend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

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

    @NotEmpty
    private String description;


    @NotNull
    private User student;

    @NotNull
    private User tutor;

    /*
     * State
     * 0 = Requested
     * 1 = Scheduled
     * 2 = Cancelled
     * 3 = Missed
     * 4 = Finished
     * 5 = Tutor_Reschedule_Requested
     * 6 = Student_Reschedule_Requested
     * 7 = Rescheduled
     */

    @NotNull
    @Builder.Default
    private State state = State.Requested;
}
